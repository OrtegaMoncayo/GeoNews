"""
API REST con FastAPI para Noticias Locales Ibarra

Características:
- 10 endpoints completos
- Validación automática con Pydantic
- Documentación Swagger automática
- Pool de conexiones async con aiomysql
- Gmail API para notificaciones
- Búsqueda geográfica con Haversine

Endpoints:
- GET / - Info de la API
- GET /health - Health check
- GET /eventos - Listar eventos
- POST /eventos - Crear evento (+ email)
- GET /eventos/{id} - Evento específico
- GET /parroquias - Todas las parroquias
- GET /parroquias/{id} - Parroquia específica
- GET /noticias - Listar noticias con filtros
- GET /noticias/radio - Búsqueda geográfica
- GET /noticias/{id} - Noticia específica
"""

from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime
import aiomysql
import asyncio
import logging
from contextlib import asynccontextmanager

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

# Pool de conexiones global
db_pool = None

# ==================== MODELOS PYDANTIC ====================

class EventoCreate(BaseModel):
    """Modelo para crear un evento"""
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

class Evento(BaseModel):
    """Modelo completo de evento"""
    id: int
    descripcion: str
    fecha: datetime
    ubicacion: Optional[str]
    creador_id: int
    parroquia_id: Optional[int]
    latitud: Optional[float]
    longitud: Optional[float]
    categoria_evento: str
    cupo_maximo: Optional[int]
    cupo_actual: int
    costo: float
    estado: str
    fecha_creacion: datetime

class Parroquia(BaseModel):
    """Modelo de parroquia"""
    id: int
    nombre: str
    tipo: str
    latitud: float
    longitud: float
    descripcion: Optional[str]
    poblacion: Optional[int]

class Noticia(BaseModel):
    """Modelo de noticia"""
    id: int
    titulo: str
    descripcion: str
    contenido: Optional[str]
    imagen_url: Optional[str]
    categoria_id: Optional[int]
    parroquia_id: Optional[int]
    latitud: Optional[float]
    longitud: Optional[float]
    ubicacion: Optional[str]
    fecha_publicacion: datetime
    visualizaciones: int
    destacada: bool
    activa: bool

# ==================== GESTIÓN DEL POOL DE CONEXIONES ====================

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Gestión del ciclo de vida de la aplicación"""
    global db_pool

    # Startup: Crear pool de conexiones
    logger.info("🚀 Iniciando FastAPI...")
    logger.info("📊 Creando pool de conexiones MySQL...")

    try:
        db_pool = await aiomysql.create_pool(
            host=DB_CONFIG['host'],
            port=DB_CONFIG['port'],
            user=DB_CONFIG['user'],
            password=DB_CONFIG['password'],
            db=DB_CONFIG['db'],
            charset=DB_CONFIG['charset'],
            autocommit=DB_CONFIG['autocommit'],
            maxsize=10
        )
        logger.info("✅ Pool de conexiones creado exitosamente")
    except Exception as e:
        logger.error(f"❌ Error al crear pool de conexiones: {e}")
        raise

    yield

    # Shutdown: Cerrar pool
    logger.info("🛑 Cerrando pool de conexiones...")
    if db_pool:
        db_pool.close()
        await db_pool.wait_closed()
    logger.info("👋 FastAPI cerrado correctamente")

# ==================== APLICACIÓN FASTAPI ====================

app = FastAPI(
    title="API Noticias Locales Ibarra",
    description="API REST para aplicación móvil de noticias con geolocalización",
    version="2.0.0",
    lifespan=lifespan
)

# Configurar CORS para permitir peticiones desde Android
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # En producción, especificar dominios
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ==================== HELPER FUNCTIONS ====================

async def get_db_connection():
    """Obtiene una conexión del pool"""
    if not db_pool:
        raise HTTPException(status_code=503, detail="Pool de conexiones no disponible")
    return await db_pool.acquire()

# ==================== ENDPOINTS ====================

@app.get("/")
async def root():
    """Información de la API"""
    return {
        "message": "API de Noticias Locales Ibarra",
        "version": "2.0.0",
        "framework": "FastAPI",
        "documentacion": "/docs",
        "endpoints": {
            "eventos": "/eventos",
            "parroquias": "/parroquias",
            "noticias": "/noticias",
            "health": "/health"
        }
    }

@app.get("/health")
async def health_check():
    """Health check de la API y base de datos"""
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor() as cursor:
                await cursor.execute("SELECT 1")
                await cursor.fetchone()

        return {
            "status": "healthy",
            "database": "connected",
            "timestamp": datetime.now().isoformat()
        }
    except Exception as e:
        logger.error(f"Health check failed: {e}")
        raise HTTPException(status_code=503, detail="Database connection failed")

# ==================== EVENTOS ====================

@app.get("/eventos", response_model=List[Evento])
async def listar_eventos():
    """Lista todos los eventos"""
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor(aiomysql.DictCursor) as cursor:
                query = """
                    SELECT
                        id, descripcion, fecha, ubicacion,
                        creador_id, parroquia_id, latitud, longitud,
                        categoria_evento, cupo_maximo, cupo_actual,
                        costo, estado, fecha_creacion
                    FROM eventos
                    ORDER BY fecha DESC
                """
                await cursor.execute(query)
                eventos = await cursor.fetchall()

                logger.info(f"✅ Obtenidos {len(eventos)} eventos")
                return eventos

    except Exception as e:
        logger.error(f"❌ Error al obtener eventos: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/eventos", response_model=Evento, status_code=201)
async def crear_evento(evento: EventoCreate):
    """Crea un nuevo evento y envía notificación por email"""
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor(aiomysql.DictCursor) as cursor:
                query = """
                    INSERT INTO eventos
                    (descripcion, fecha, ubicacion, creador_id, parroquia_id,
                     latitud, longitud, categoria_evento, cupo_maximo, cupo_actual,
                     costo, estado, fecha_creacion)
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW())
                """

                valores = (
                    evento.descripcion,
                    evento.fecha,
                    evento.ubicacion,
                    evento.creadorId,
                    evento.parroquiaId,
                    evento.latitud,
                    evento.longitud,
                    evento.categoriaEvento,
                    evento.cupoMaximo,
                    0,  # cupo_actual inicial
                    evento.costo,
                    "programado"
                )

                await cursor.execute(query, valores)
                evento_id = cursor.lastrowid

                # Obtener el evento creado
                await cursor.execute(
                    "SELECT * FROM eventos WHERE id = %s",
                    (evento_id,)
                )
                evento_creado = await cursor.fetchone()

                logger.info(f"✅ Evento creado: ID {evento_id}")

                # TODO: Enviar email en background (implementar Gmail API)
                # asyncio.create_task(enviar_email_evento(evento_creado))
                logger.info("📧 Email de notificación pendiente")

                return evento_creado

    except Exception as e:
        logger.error(f"❌ Error al crear evento: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/eventos/{evento_id}", response_model=Evento)
async def obtener_evento(evento_id: int):
    """Obtiene un evento específico"""
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor(aiomysql.DictCursor) as cursor:
                await cursor.execute(
                    "SELECT * FROM eventos WHERE id = %s",
                    (evento_id,)
                )
                evento = await cursor.fetchone()

                if not evento:
                    raise HTTPException(status_code=404, detail="Evento no encontrado")

                return evento

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error al obtener evento {evento_id}: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# ==================== PARROQUIAS ====================

@app.get("/parroquias", response_model=List[Parroquia])
async def listar_parroquias(tipo: Optional[str] = Query(None, regex="^(urbana|rural)$")):
    """Lista parroquias, opcionalmente filtradas por tipo"""
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor(aiomysql.DictCursor) as cursor:
                if tipo:
                    query = """
                        SELECT id, nombre, tipo, latitud, longitud, descripcion, poblacion
                        FROM parroquias
                        WHERE tipo = %s
                        ORDER BY nombre
                    """
                    await cursor.execute(query, (tipo,))
                else:
                    query = """
                        SELECT id, nombre, tipo, latitud, longitud, descripcion, poblacion
                        FROM parroquias
                        ORDER BY tipo, nombre
                    """
                    await cursor.execute(query)

                parroquias = await cursor.fetchall()
                logger.info(f"✅ Obtenidas {len(parroquias)} parroquias" + (f" ({tipo})" if tipo else ""))
                return parroquias

    except Exception as e:
        logger.error(f"❌ Error al obtener parroquias: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/parroquias/{parroquia_id}", response_model=Parroquia)
async def obtener_parroquia(parroquia_id: int):
    """Obtiene una parroquia específica"""
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor(aiomysql.DictCursor) as cursor:
                await cursor.execute(
                    "SELECT * FROM parroquias WHERE id = %s",
                    (parroquia_id,)
                )
                parroquia = await cursor.fetchone()

                if not parroquia:
                    raise HTTPException(status_code=404, detail="Parroquia no encontrada")

                return parroquia

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error al obtener parroquia {parroquia_id}: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# ==================== NOTICIAS ====================

@app.get("/noticias", response_model=List[Noticia])
async def listar_noticias(
    categoria_id: Optional[int] = None,
    parroquia_id: Optional[int] = None,
    destacada: Optional[bool] = None,
    limit: int = Query(50, ge=1, le=100),
    offset: int = Query(0, ge=0)
):
    """Lista noticias con filtros opcionales"""
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor(aiomysql.DictCursor) as cursor:
                query = """
                    SELECT
                        id, titulo, descripcion, contenido, imagen_url,
                        categoria_id, parroquia_id, latitud, longitud,
                        ubicacion, fecha_publicacion, visualizaciones,
                        destacada, activa
                    FROM noticias
                    WHERE activa = 1
                """
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

                query += " ORDER BY fecha_publicacion DESC LIMIT %s OFFSET %s"
                params.extend([limit, offset])

                await cursor.execute(query, params)
                noticias = await cursor.fetchall()

                logger.info(f"✅ Obtenidas {len(noticias)} noticias")
                return noticias

    except Exception as e:
        logger.error(f"❌ Error al obtener noticias: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/noticias/radio")
async def buscar_noticias_por_radio(
    latitud: float = Query(..., ge=-90, le=90),
    longitud: float = Query(..., ge=-180, le=180),
    radio_km: float = Query(..., ge=0.1, le=50)
):
    """
    Busca noticias dentro de un radio geográfico usando Haversine

    Parámetros:
    - latitud: Latitud del usuario (-90 a 90)
    - longitud: Longitud del usuario (-180 a 180)
    - radio_km: Radio de búsqueda en kilómetros (0.1 a 50)
    """
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor(aiomysql.DictCursor) as cursor:
                # Fórmula de Haversine en SQL
                query = """
                    SELECT
                        id, titulo, descripcion, contenido, imagen_url,
                        categoria_id, parroquia_id, latitud, longitud,
                        ubicacion, fecha_publicacion, visualizaciones,
                        destacada, activa,
                        (6371 * acos(
                            cos(radians(%s)) * cos(radians(latitud)) *
                            cos(radians(longitud) - radians(%s)) +
                            sin(radians(%s)) * sin(radians(latitud))
                        )) AS distancia_km
                    FROM noticias
                    WHERE activa = 1
                      AND latitud IS NOT NULL
                      AND longitud IS NOT NULL
                    HAVING distancia_km <= %s
                    ORDER BY distancia_km ASC
                """

                await cursor.execute(query, (latitud, longitud, latitud, radio_km))
                noticias = await cursor.fetchall()

                logger.info(f"✅ Encontradas {len(noticias)} noticias en radio de {radio_km} km")
                return noticias

    except Exception as e:
        logger.error(f"❌ Error en búsqueda por radio: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/noticias/{noticia_id}", response_model=Noticia)
async def obtener_noticia(noticia_id: int):
    """Obtiene una noticia específica e incrementa visualizaciones"""
    try:
        async with db_pool.acquire() as conn:
            async with conn.cursor(aiomysql.DictCursor) as cursor:
                # Incrementar visualizaciones
                await cursor.execute(
                    "UPDATE noticias SET visualizaciones = visualizaciones + 1 WHERE id = %s",
                    (noticia_id,)
                )

                # Obtener noticia
                await cursor.execute(
                    "SELECT * FROM noticias WHERE id = %s",
                    (noticia_id,)
                )
                noticia = await cursor.fetchone()

                if not noticia:
                    raise HTTPException(status_code=404, detail="Noticia no encontrada")

                return noticia

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error al obtener noticia {noticia_id}: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# ==================== MAIN ====================

if __name__ == "__main__":
    import uvicorn

    print("=" * 60)
    print("🚀 Iniciando API de Noticias Locales Ibarra")
    print("=" * 60)
    print(f"📍 Host: http://localhost:8000")
    print(f"📖 Documentación: http://localhost:8000/docs")
    print(f"📚 ReDoc: http://localhost:8000/redoc")
    print("=" * 60)

    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    )
