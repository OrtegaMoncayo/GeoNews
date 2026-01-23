"""
Script simple para agregar coordenadas a noticias existentes
Uso: python agregar_coordenadas.py
"""

import requests
import json

# ============================================================
# CONFIGURACIÓN - Cambia esta URL por tu API desplegada
# ============================================================
API_URL = "https://noticias-ibarra-fastapi-511752828765.us-central1.run.app"
# O si estás probando localmente:
# API_URL = "http://localhost:8080"

# ============================================================
# COORDENADAS DE LUGARES COMUNES EN IBARRA/URCUQUÍ
# ============================================================
LUGARES_IBARRA = {
    "centro": {"lat": 0.3476, "lon": -78.1223, "nombre": "Centro de Ibarra"},
    "parque_central": {"lat": 0.3478, "lon": -78.1226, "nombre": "Parque Pedro Moncayo"},
    "municipio": {"lat": 0.3482, "lon": -78.1221, "nombre": "Municipio de Ibarra"},
    "gobernacion": {"lat": 0.3480, "lon": -78.1215, "nombre": "Gobernación de Imbabura"},
    "universidad": {"lat": 0.3521, "lon": -78.1187, "nombre": "Universidad Técnica del Norte"},
    "hospital": {"lat": 0.3456, "lon": -78.1198, "nombre": "Hospital San Vicente de Paúl"},
    "terminal": {"lat": 0.3392, "lon": -78.1156, "nombre": "Terminal Terrestre"},
    "estadio": {"lat": 0.3505, "lon": -78.1245, "nombre": "Estadio Olímpico de Ibarra"},
    "yachay": {"lat": 0.2015, "lon": -78.2978, "nombre": "Yachay Tech"},
    "urcuqui": {"lat": 0.4105, "lon": -78.1523, "nombre": "Urcuquí Centro"},
}

def obtener_todas_noticias():
    """Obtiene todas las noticias del API"""
    try:
        response = requests.get(f"{API_URL}/noticias?limit=100")
        response.raise_for_status()
        data = response.json()
        return data.get("noticias", [])
    except Exception as e:
        print(f"❌ Error obteniendo noticias: {e}")
        return []

def actualizar_coordenadas(noticia_id, latitud, longitud, ubicacion_texto):
    """Actualiza las coordenadas de una noticia"""
    try:
        url = f"{API_URL}/noticias/{noticia_id}/coordenadas"
        params = {
            "latitud": latitud,
            "longitud": longitud,
            "ubicacionTexto": ubicacion_texto
        }

        response = requests.put(url, params=params)
        response.raise_for_status()
        return True, response.json()
    except Exception as e:
        return False, str(e)

def main():
    print("=" * 60)
    print("📍 AGREGAR COORDENADAS A NOTICIAS")
    print("=" * 60)
    print()

    # Obtener noticias
    print("📥 Obteniendo noticias...")
    noticias = obtener_todas_noticias()

    if not noticias:
        print("❌ No se encontraron noticias")
        return

    print(f"✅ Se encontraron {len(noticias)} noticias")
    print()

    # Filtrar noticias sin coordenadas
    sin_coordenadas = [n for n in noticias if n.get("latitud") is None]

    if not sin_coordenadas:
        print("✅ Todas las noticias ya tienen coordenadas")
        return

    print(f"📍 {len(sin_coordenadas)} noticias sin coordenadas")
    print()

    # Mostrar lugares disponibles
    print("LUGARES DISPONIBLES:")
    print("-" * 60)
    for key, lugar in LUGARES_IBARRA.items():
        print(f"{key:20} → {lugar['nombre']}")
    print("-" * 60)
    print()

    # Procesar cada noticia
    actualizadas = 0

    for i, noticia in enumerate(sin_coordenadas, 1):
        print(f"\n[{i}/{len(sin_coordenadas)}] {noticia['titulo'][:50]}...")
        print(f"    ID: {noticia['id']}")

        # Preguntar ubicación
        print("\n    Opciones:")
        print("    1. Usar ubicación de lista")
        print("    2. Ingresar coordenadas manualmente")
        print("    3. Saltar esta noticia")

        opcion = input("    Selecciona (1/2/3): ").strip()

        if opcion == "1":
            # Usar lugar predefinido
            lugar_key = input(f"    Ingresa código del lugar: ").strip().lower()

            if lugar_key in LUGARES_IBARRA:
                lugar = LUGARES_IBARRA[lugar_key]
                lat = lugar["lat"]
                lon = lugar["lon"]
                ubicacion = lugar["nombre"]
            else:
                print("    ⚠️ Lugar no válido, saltando...")
                continue

        elif opcion == "2":
            # Ingresar manualmente
            try:
                lat = float(input("    Latitud: ").strip())
                lon = float(input("    Longitud: ").strip())
                ubicacion = input("    Nombre del lugar: ").strip()
            except ValueError:
                print("    ⚠️ Coordenadas inválidas, saltando...")
                continue
        else:
            print("    ⏭️  Saltando...")
            continue

        # Actualizar
        print(f"    📍 Actualizando: {ubicacion} ({lat}, {lon})")
        exito, resultado = actualizar_coordenadas(noticia['id'], lat, lon, ubicacion)

        if exito:
            print("    ✅ Actualizada exitosamente")
            actualizadas += 1
        else:
            print(f"    ❌ Error: {resultado}")

    print()
    print("=" * 60)
    print(f"✅ Proceso completado: {actualizadas} noticias actualizadas")
    print("=" * 60)

def modo_automatico():
    """
    Modo automático: Asigna ubicación del centro de Ibarra a todas las noticias sin coordenadas
    """
    print("=" * 60)
    print("🤖 MODO AUTOMÁTICO - Asignar Centro de Ibarra")
    print("=" * 60)
    print()

    confirmacion = input("¿Asignar 'Centro de Ibarra' a todas las noticias sin coordenadas? (si/no): ")

    if confirmacion.lower() != "si":
        print("❌ Cancelado")
        return

    # Obtener noticias
    print("📥 Obteniendo noticias...")
    noticias = obtener_todas_noticias()
    sin_coordenadas = [n for n in noticias if n.get("latitud") is None]

    if not sin_coordenadas:
        print("✅ Todas las noticias ya tienen coordenadas")
        return

    print(f"📍 Procesando {len(sin_coordenadas)} noticias...")
    print()

    lugar = LUGARES_IBARRA["centro"]
    actualizadas = 0

    for i, noticia in enumerate(sin_coordenadas, 1):
        print(f"[{i}/{len(sin_coordenadas)}] {noticia['titulo'][:50]}...", end=" ")

        exito, _ = actualizar_coordenadas(
            noticia['id'],
            lugar['lat'],
            lugar['lon'],
            lugar['nombre']
        )

        if exito:
            print("✅")
            actualizadas += 1
        else:
            print("❌")

    print()
    print(f"✅ Completado: {actualizadas}/{len(sin_coordenadas)} noticias actualizadas")

if __name__ == "__main__":
    print()
    print("SELECCIONA UN MODO:")
    print("1. Modo interactivo (seleccionar ubicación para cada noticia)")
    print("2. Modo automático (todas al centro de Ibarra)")
    print()

    modo = input("Ingresa opción (1/2): ").strip()
    print()

    if modo == "1":
        main()
    elif modo == "2":
        modo_automatico()
    else:
        print("❌ Opción inválida")
