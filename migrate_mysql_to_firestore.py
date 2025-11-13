#!/usr/bin/env python3
"""
Script para migrar datos de MySQL a Firestore
Migra: parroquias, categorias, usuarios, noticias, eventos
"""

import pymysql
import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime
import sys

# ==================== CONFIGURACIÓN ====================

# MySQL Config
MYSQL_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'r1ch4rdm0nc4y0',
    'db': 'noticias_ibarra2',
    'charset': 'utf8mb4'
}

# Firebase Config - Necesitas descargar la Service Account Key
# 1. Firebase Console → Project Settings → Service Accounts
# 2. Click "Generate new private key"
# 3. Guardar como "firebase-service-account.json" en esta carpeta

FIREBASE_CREDENTIALS_PATH = 'firebase-service-account.json'

# ==================== FUNCIONES ====================

def conectar_mysql():
    """Conecta a MySQL"""
    print("📊 Conectando a MySQL...")
    try:
        conn = pymysql.connect(**MYSQL_CONFIG, cursorclass=pymysql.cursors.DictCursor)
        print("✅ Conectado a MySQL")
        return conn
    except Exception as e:
        print(f"❌ Error conectando a MySQL: {e}")
        sys.exit(1)

def inicializar_firebase():
    """Inicializa Firebase Admin SDK"""
    print("🔥 Inicializando Firebase...")
    try:
        cred = credentials.Certificate(FIREBASE_CREDENTIALS_PATH)
        firebase_admin.initialize_app(cred)
        db = firestore.client()
        print("✅ Firebase inicializado")
        return db
    except Exception as e:
        print(f"❌ Error inicializando Firebase: {e}")
        print("\n💡 Necesitas:")
        print("1. Ir a Firebase Console → Project Settings → Service Accounts")
        print("2. Click 'Generate new private key'")
        print("3. Guardar como 'firebase-service-account.json' en esta carpeta")
        sys.exit(1)

def migrar_parroquias(mysql_conn, firestore_db):
    """Migra parroquias de MySQL a Firestore"""
    print("\n🏘️  Migrando parroquias...")

    with mysql_conn.cursor() as cursor:
        cursor.execute("SELECT * FROM parroquias")
        parroquias = cursor.fetchall()

    batch = firestore_db.batch()
    count = 0

    for p in parroquias:
        doc_ref = firestore_db.collection('parroquias').document(str(p['id']))

        data = {
            'id': str(p['id']),
            'nombre': p['nombre'],
            'tipo': p['tipo'],
            'ubicacion': firestore.GeoPoint(
                float(p['latitud']) if p['latitud'] else 0.0,
                float(p['longitud']) if p['longitud'] else 0.0
            ),
            'descripcion': p['descripcion'] or '',
            'poblacion': p['poblacion'] or 0
        }

        batch.set(doc_ref, data)
        count += 1

        if count % 500 == 0:  # Firestore batch limit
            batch.commit()
            batch = firestore_db.batch()
            print(f"  → {count} parroquias migradas...")

    if count % 500 != 0:
        batch.commit()

    print(f"✅ {count} parroquias migradas")
    return count

def migrar_categorias(mysql_conn, firestore_db):
    """Migra categorías de MySQL a Firestore"""
    print("\n📂 Migrando categorías...")

    with mysql_conn.cursor() as cursor:
        cursor.execute("SELECT * FROM categorias")
        categorias = cursor.fetchall()

    batch = firestore_db.batch()
    count = 0

    for c in categorias:
        doc_ref = firestore_db.collection('categorias').document(str(c['id']))

        data = {
            'id': str(c['id']),
            'nombre': c['nombre'],
            'icono': c.get('icono', 'ic_default'),
            'color': c.get('color', '#2196F3'),
            'activa': c.get('activa', 1) == 1
        }

        batch.set(doc_ref, data)
        count += 1

    batch.commit()
    print(f"✅ {count} categorías migradas")
    return count

def migrar_usuarios(mysql_conn, firestore_db):
    """Migra usuarios de MySQL a Firestore"""
    print("\n👥 Migrando usuarios...")

    with mysql_conn.cursor() as cursor:
        cursor.execute("SELECT * FROM usuarios")
        usuarios = cursor.fetchall()

    batch = firestore_db.batch()
    count = 0

    for u in usuarios:
        # Usar el email como ID (Firebase Auth lo usará)
        doc_id = u['email'].replace('@', '_at_').replace('.', '_dot_')
        doc_ref = firestore_db.collection('usuarios').document(doc_id)

        data = {
            'id': str(u['id']),
            'nombre': u['nombre'],
            'email': u['email'],
            'telefono': u.get('telefono', ''),
            'rol': u.get('rol', 'usuario'),
            'fechaRegistro': u.get('fecha_creacion', datetime.now()),
            'activo': u.get('activo', 1) == 1
        }

        if u.get('parroquia_id'):
            data['parroquiaId'] = firestore_db.collection('parroquias').document(str(u['parroquia_id']))

        batch.set(doc_ref, data)
        count += 1

        if count % 500 == 0:
            batch.commit()
            batch = firestore_db.batch()
            print(f"  → {count} usuarios migrados...")

    if count % 500 != 0:
        batch.commit()

    print(f"✅ {count} usuarios migrados")
    return count

def migrar_noticias(mysql_conn, firestore_db):
    """Migra noticias de MySQL a Firestore"""
    print("\n📰 Migrando noticias...")

    with mysql_conn.cursor() as cursor:
        cursor.execute("SELECT * FROM noticias ORDER BY fecha_publicacion DESC")
        noticias = cursor.fetchall()

    batch = firestore_db.batch()
    count = 0

    for n in noticias:
        doc_ref = firestore_db.collection('noticias').document()  # Auto-ID

        data = {
            'titulo': n['titulo'],
            'descripcion': n['descripcion'] or '',
            'contenido': n.get('contenido', ''),
            'imagenUrl': n.get('imagen_url', ''),
            'ubicacionTexto': n.get('ubicacion', ''),
            'fechaPublicacion': n.get('fecha_publicacion', datetime.now()),
            'fechaCreacion': n.get('fecha_creacion', datetime.now()),
            'visualizaciones': n.get('visualizaciones', 0),
            'destacada': n.get('destacada', 0) == 1,
            'activa': n.get('activa', 1) == 1,
            'tags': []
        }

        # Ubicación geográfica
        if n.get('latitud') and n.get('longitud'):
            data['ubicacion'] = firestore.GeoPoint(
                float(n['latitud']),
                float(n['longitud'])
            )

        # Referencias
        if n.get('categoria_id'):
            data['categoriaId'] = firestore_db.collection('categorias').document(str(n['categoria_id']))

        if n.get('parroquia_id'):
            data['parroquiaId'] = firestore_db.collection('parroquias').document(str(n['parroquia_id']))

        if n.get('autor_id'):
            data['autorId'] = firestore_db.collection('usuarios').document(str(n['autor_id']))

        batch.set(doc_ref, data)
        count += 1

        if count % 500 == 0:
            batch.commit()
            batch = firestore_db.batch()
            print(f"  → {count} noticias migradas...")

    if count % 500 != 0:
        batch.commit()

    print(f"✅ {count} noticias migradas")
    return count

def migrar_eventos(mysql_conn, firestore_db):
    """Migra eventos de MySQL a Firestore"""
    print("\n📅 Migrando eventos...")

    with mysql_conn.cursor() as cursor:
        cursor.execute("SELECT * FROM eventos ORDER BY fecha DESC")
        eventos = cursor.fetchall()

    batch = firestore_db.batch()
    count = 0

    for e in eventos:
        doc_ref = firestore_db.collection('eventos').document()  # Auto-ID

        data = {
            'descripcion': e['descripcion'],
            'fecha': e.get('fecha', datetime.now()),
            'ubicacionTexto': e.get('ubicacion', ''),
            'categoriaEvento': e.get('categoria_evento', 'otro'),
            'cupoMaximo': e.get('cupo_maximo'),
            'cupoActual': e.get('cupo_actual', 0),
            'costo': float(e.get('costo', 0)),
            'estado': e.get('estado', 'programado'),
            'fechaCreacion': e.get('fecha_creacion', datetime.now()),
            'asistentes': []
        }

        # Ubicación geográfica
        if e.get('latitud') and e.get('longitud'):
            data['ubicacion'] = firestore.GeoPoint(
                float(e['latitud']),
                float(e['longitud'])
            )

        # Referencias
        if e.get('creador_id'):
            data['creadorId'] = firestore_db.collection('usuarios').document(str(e['creador_id']))

        if e.get('parroquia_id'):
            data['parroquiaId'] = firestore_db.collection('parroquias').document(str(e['parroquia_id']))

        batch.set(doc_ref, data)
        count += 1

        if count % 500 == 0:
            batch.commit()
            batch = firestore_db.batch()
            print(f"  → {count} eventos migrados...")

    if count % 500 != 0:
        batch.commit()

    print(f"✅ {count} eventos migrados")
    return count

# ==================== MAIN ====================

def main():
    """Función principal"""
    print("=" * 60)
    print("🚀 Migración MySQL → Firestore")
    print("   NoticiasIbarra")
    print("=" * 60)

    # Conectar
    mysql_conn = conectar_mysql()
    firestore_db = inicializar_firebase()

    print("\n⚠️  ADVERTENCIA:")
    print("   Esto creará/actualizará datos en Firestore.")
    print("   Asegúrate de tener un backup de MySQL.")

    respuesta = input("\n¿Continuar? (s/n): ")
    if respuesta.lower() != 's':
        print("❌ Migración cancelada")
        sys.exit(0)

    # Migrar datos
    total = 0
    total += migrar_parroquias(mysql_conn, firestore_db)
    total += migrar_categorias(mysql_conn, firestore_db)
    total += migrar_usuarios(mysql_conn, firestore_db)
    total += migrar_noticias(mysql_conn, firestore_db)
    total += migrar_eventos(mysql_conn, firestore_db)

    mysql_conn.close()

    print("\n" + "=" * 60)
    print(f"✅ Migración completada: {total} documentos")
    print("=" * 60)
    print("\n💡 Próximos pasos:")
    print("   1. Verifica los datos en Firebase Console")
    print("   2. Configura las reglas de seguridad")
    print("   3. Crea los índices necesarios")
    print("   4. Actualiza tu app Android")

if __name__ == "__main__":
    main()
