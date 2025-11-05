"""
API REST con FastAPI para Noticias Locales Ibarra - Versión Simplificada

Versión sin aiomysql (usa pymysql síncrono) para compatibilidad con Python 3.14
"""

from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime
import pymysql
import logging

# Configuración de logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ==================== CONFIGURACIÓN ====================

DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': 'r1ch4rdm0nc4y0',  # ← CAMBIAR
    'db': 'noticias_ibarra2',
    'charset': 'utf8mb4',
    'autocommit': True
}

# ==================== PYDANTIC MODELS ====================

class EventoCreate(BaseModel):
    descripcion: str = Field(..., min_length=10, max_length=500)
    fecha: datetime
    ubicacion: Optional[str] = Field(None, max_length=255)
    creadorId: int = Field(default=1)
    parroquiaId: Optional[int] = None
    latitud: Optional[float] = None
    longitud: Optional[float] = None
    categoriaEvento: str = Field(default="otro")
    cupoMaximo: Optional[int] = None
    costo: float = Field(default=0.0)

# ==================== FASTAPI APP ====================

app = FastAPI(
    title="API Noticias Locales Ibarra",
    description="API REST para noticias y eventos geolocalizados",
    version="1.0.0"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ==================== DATABASE CONNECTION ====================

def get_db_connection():
    """Crea y retorna una conexión a MySQL"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        return connection
    except Exception as e:
        logger.error(f"Error conectando a MySQL: {e}")
        raise HTTPException(status_code=500, detail="Error de conexión a base de datos")

# ==================== ENDPOINTS ====================

@app.get("/")
def root():
    """Información básica de la API"""
    return {
        "nombre": "API Noticias Locales Ibarra",
        "version": "1.0.0",
        "estado": "activo",
        "endpoints": [
            "GET /health",
            "GET /eventos",
            "POST /eventos",
            "GET /eventos/{id}",
            "GET /parroquias",
            "GET /parroquias/{id}",
            "GET /noticias",
            "GET /noticias/radio",
            "GET /noticias/{id}"
        ]
    }

@app.get("/health")
def health_check():
    """Health check de la API y base de datos"""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            cursor.execute("SELECT 1")
            cursor.fetchone()
        conn.close()

        return {
            "status": "healthy",
            "database": "connected",
            "timestamp": datetime.now().isoformat()
        }
    except Exception as e:
        return {
            "status": "unhealthy",
            "database": "disconnected",
            "error": str(e),
            "timestamp": datetime.now().isoformat()
        }

@app.get("/eventos")
def listar_eventos():
    """Lista todos los eventos"""
    try:
        conn = get_db_connection()
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute("""
                SELECT e.*, p.nombre as parroquia_nombre
                FROM eventos e
                LEFT JOIN parroquias p ON e.parroquia_id = p.id
                ORDER BY e.fecha DESC
            """)
            eventos = cursor.fetchall()
        conn.close()

        logger.info(f"✅ Obtenidos {len(eventos)} eventos")
        return eventos

    except Exception as e:
        logger.error(f"❌ Error obteniendo eventos: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/eventos")
def crear_evento(evento: EventoCreate):
    """Crea un nuevo evento"""
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                INSERT INTO eventos
                (descripcion, fecha, ubicacion, creador_id, parroquia_id,
                 latitud, longitud, categoria_evento, cupo_maximo, costo)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(query, (
                evento.descripcion,
                evento.fecha,
                evento.ubicacion,
                evento.creadorId,
                evento.parroquiaId,
                evento.latitud,
                evento.longitud,
                evento.categoriaEvento,
                evento.cupoMaximo,
                evento.costo
            ))
            conn.commit()
            evento_id = cursor.lastrowid
        conn.close()

        logger.info(f"✅ Evento creado: ID {evento_id}")
        return {"id": evento_id, "mensaje": "Evento creado exitosamente"}

    except Exception as e:
        logger.error(f"❌ Error creando evento: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/eventos/{evento_id}")
def obtener_evento(evento_id: int):
    """Obtiene un evento específico por ID"""
    try:
        conn = get_db_connection()
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute("""
                SELECT e.*, p.nombre as parroquia_nombre
                FROM eventos e
                LEFT JOIN parroquias p ON e.parroquia_id = p.id
                WHERE e.id = %s
            """, (evento_id,))
            evento = cursor.fetchone()
        conn.close()

        if not evento:
            raise HTTPException(status_code=404, detail="Evento no encontrado")

        return evento

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error obteniendo evento: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/parroquias")
def listar_parroquias(tipo: Optional[str] = None):
    """Lista todas las parroquias, opcionalmente filtradas por tipo"""
    try:
        conn = get_db_connection()
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            if tipo:
                cursor.execute("SELECT * FROM parroquias WHERE tipo = %s ORDER BY nombre", (tipo,))
            else:
                cursor.execute("SELECT * FROM parroquias ORDER BY tipo, nombre")
            parroquias = cursor.fetchall()
        conn.close()

        return parroquias

    except Exception as e:
        logger.error(f"❌ Error obteniendo parroquias: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/parroquias/{parroquia_id}")
def obtener_parroquia(parroquia_id: int):
    """Obtiene una parroquia específica por ID"""
    try:
        conn = get_db_connection()
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute("SELECT * FROM parroquias WHERE id = %s", (parroquia_id,))
            parroquia = cursor.fetchone()
        conn.close()

        if not parroquia:
            raise HTTPException(status_code=404, detail="Parroquia no encontrada")

        return parroquia

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error obteniendo parroquia: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/noticias")
def listar_noticias(
    categoria_id: Optional[int] = None,
    parroquia_id: Optional[int] = None,
    destacada: Optional[bool] = None,
    limit: int = Query(default=50, ge=1, le=100),
    offset: int = Query(default=0, ge=0)
):
    """Lista noticias con filtros opcionales"""
    try:
        conn = get_db_connection()
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            query = "SELECT * FROM noticias WHERE activa = 1"
            params = []

            if categoria_id:
                query += " AND categoria_id = %s"
                params.append(categoria_id)

            if parroquia_id:
                query += " AND parroquia_id = %s"
                params.append(parroquia_id)

            if destacada is not None:
                query += " AND destacada = %s"
                params.append(destacada)

            query += " ORDER BY fecha_creacion DESC LIMIT %s OFFSET %s"
            params.extend([limit, offset])

            cursor.execute(query, params)
            noticias = cursor.fetchall()
        conn.close()

        return noticias

    except Exception as e:
        logger.error(f"❌ Error obteniendo noticias: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/noticias/radio")
def buscar_noticias_por_radio(
    latitud: float = Query(..., ge=-90, le=90),
    longitud: float = Query(..., ge=-180, le=180),
    radio_km: float = Query(..., ge=0.1, le=50)
):
    """Busca noticias en un radio de distancia (fórmula Haversine)"""
    try:
        conn = get_db_connection()
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            query = """
                SELECT *,
                (6371 * acos(
                    cos(radians(%s)) * cos(radians(latitud)) *
                    cos(radians(longitud) - radians(%s)) +
                    sin(radians(%s)) * sin(radians(latitud))
                )) AS distancia_km
                FROM noticias
                WHERE activa = 1 AND latitud IS NOT NULL AND longitud IS NOT NULL
                HAVING distancia_km <= %s
                ORDER BY distancia_km ASC
            """
            cursor.execute(query, (latitud, longitud, latitud, radio_km))
            noticias = cursor.fetchall()
        conn.close()

        logger.info(f"✅ Encontradas {len(noticias)} noticias en {radio_km}km")
        return noticias

    except Exception as e:
        logger.error(f"❌ Error en búsqueda por radio: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/noticias/{noticia_id}")
def obtener_noticia(noticia_id: int):
    """Obtiene una noticia específica por ID"""
    try:
        conn = get_db_connection()
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            cursor.execute("SELECT * FROM noticias WHERE id = %s", (noticia_id,))
            noticia = cursor.fetchone()
        conn.close()

        if not noticia:
            raise HTTPException(status_code=404, detail="Noticia no encontrada")

        return noticia

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error obteniendo noticia: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# ==================== MAIN ====================

if __name__ == "__main__":
    import uvicorn

    logger.info("🚀 Iniciando API de Noticias Locales Ibarra")
    logger.info(f"📍 Host: http://localhost:8000")
    logger.info(f"📖 Documentación: http://localhost:8000/docs")

    uvicorn.run(
        "main_simple:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    )
