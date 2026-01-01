"""
FastAPI Backend para NoticiasIbarra
Conecta con Firestore y Firebase Cloud Messaging
"""

from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime
import firebase_admin
from firebase_admin import credentials, firestore, messaging
import os
from pathlib import Path

# Inicializar FastAPI
app = FastAPI(
    title="NoticiasIbarra API",
    description="""
    ## 🏛️ API RESTful para Gestión de Noticias y Eventos de Ibarra

    Esta API permite gestionar noticias, eventos, parroquias y categorías de la ciudad de Ibarra y sus alrededores.

    ### 📱 Características principales:
    * **Noticias**: Gestión completa de noticias locales
    * **Eventos**: Administración de eventos comunitarios
    * **Parroquias**: Información de parroquias urbanas y rurales
    * **Categorías**: Clasificación de contenido
    * **Geolocalización**: Coordenadas para ubicación en mapa
    * **Firebase**: Integración con Firestore y FCM

    ### 🔗 Enlaces útiles:
    * [Repositorio GitHub](https://github.com/OrtegaMoncayo/NoticiasIbarra)
    * [Documentación ReDoc](/redoc)

    ### 📞 Contacto:
    * Email: richard.ortega778@ist17dejulio.edu.ec
    * Proyecto: Tesis de Grado - Instituto Tecnológico 17 de Julio
    """,
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_tags=[
        {
            "name": "Sistema",
            "description": "Endpoints de información general y estado del sistema"
        },
        {
            "name": "Noticias",
            "description": "Operaciones relacionadas con noticias locales"
        },
        {
            "name": "Eventos",
            "description": "Gestión de eventos comunitarios y actividades"
        },
        {
            "name": "Parroquias",
            "description": "Información de parroquias urbanas y rurales"
        },
        {
            "name": "Categorías",
            "description": "Categorización de noticias y eventos"
        },
        {
            "name": "Notificaciones",
            "description": "Envío de notificaciones push con Firebase Cloud Messaging"
        },
        {
            "name": "Estadísticas",
            "description": "Métricas y estadísticas del sistema"
        }
    ]
)

# Configurar CORS para permitir requests desde Android
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # En producción, especifica dominios permitidos
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Inicializar Firebase Admin SDK
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

# ==================== ARCHIVOS ESTÁTICOS ====================
# Obtener la ruta absoluta del directorio actual
BASE_DIR = Path(__file__).resolve().parent
STATIC_DIR = BASE_DIR / "static"

# Montar directorio static para servir la interfaz web del periodista
if STATIC_DIR.exists():
    app.mount("/static", StaticFiles(directory=str(STATIC_DIR)), name="static")

# ==================== MODELOS PYDANTIC ====================

class Noticia(BaseModel):
    titulo: str
    descripcion: Optional[str] = None
    contenido: Optional[str] = None
    citaDestacada: Optional[str] = None
    impactoComunitario: Optional[str] = None
    hashtags: Optional[str] = None
    imagenUrl: Optional[str] = None
    ubicacionTexto: Optional[str] = None
    latitud: Optional[float] = None
    longitud: Optional[float] = None
    categoriaId: Optional[str] = None
    parroquiaId: Optional[str] = None
    destacada: bool = False
    activa: bool = True
    tags: List[str] = []

class Evento(BaseModel):
    descripcion: str
    fecha: datetime
    ubicacionTexto: Optional[str] = None
    latitud: Optional[float] = None
    longitud: Optional[float] = None
    categoriaEvento: str  # 'cultural', 'deportivo', 'educativo', 'comunitario', 'otro'
    cupoMaximo: Optional[int] = None
    cupoActual: int = 0
    costo: float = 0.0
    imagenUrl: Optional[str] = None
    contactoTelefono: Optional[str] = None
    contactoEmail: Optional[str] = None
    estado: str = "programado"  # 'programado', 'en_curso', 'finalizado', 'cancelado'

class NotificacionPush(BaseModel):
    titulo: str
    mensaje: str
    topic: str = "all"  # Topic FCM para enviar a todos
    data: Optional[dict] = None

# ==================== ENDPOINTS RAÍZ ====================

@app.get("/", tags=["Sistema"])
async def root():
    """
    ## 🏠 Endpoint Principal

    Retorna información básica de la API y su estado.

    ### Respuesta:
    * `status`: Estado del servicio
    * `message`: Mensaje de bienvenida
    * `version`: Versión actual de la API
    * `firebase`: Estado de conexión con Firebase
    """
    return {
        "status": "ok",
        "message": "NoticiasIbarra API está funcionando",
        "version": "1.0.0",
        "firebase": "connected"
    }

@app.get("/health", tags=["Sistema"])
async def health_check():
    """
    ## 🏥 Health Check

    Verifica el estado de salud del servicio y la conexión con Firestore.

    ### Uso:
    Este endpoint es utilizado por Cloud Run para monitoreo de disponibilidad.

    ### Respuestas:
    * **200**: Servicio saludable
    * **503**: Error de conexión con Firestore
    """
    try:
        # Verificar conexión a Firestore
        db.collection("noticias").limit(1).get()
        return {"status": "healthy", "firestore": "connected"}
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"Firestore error: {str(e)}")

@app.get("/panel-periodista", response_class=HTMLResponse, tags=["Sistema"])
async def panel_periodista():
    """
    ## 📝 Panel de Periodista

    Interfaz web para que los periodistas publiquen noticias con geolocalización.

    ### Características:
    * Formulario completo de noticias
    * Mapa interactivo de Google Maps para seleccionar ubicación
    * Envío directo a la API de noticias
    * Validación de campos requeridos

    ### Acceso:
    Abrir en navegador: `https://[tu-dominio]/panel-periodista`
    """
    html_file = STATIC_DIR / "index.html"

    if not html_file.exists():
        raise HTTPException(
            status_code=404,
            detail=f"Panel de periodista no encontrado. Ruta esperada: {html_file}"
        )

    try:
        with open(html_file, "r", encoding="utf-8") as f:
            html_content = f.read()
        return HTMLResponse(content=html_content)
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Error al leer el archivo HTML: {str(e)}"
        )

# ==================== ENDPOINTS NOTICIAS ====================

@app.get("/noticias", tags=["Noticias"])
async def obtener_noticias(
    limit: int = Query(50, ge=1, le=100, description="Número máximo de noticias a retornar"),
    activa: bool = Query(True, description="Filtrar por noticias activas"),
    destacada: Optional[bool] = Query(None, description="Filtrar por noticias destacadas (opcional)")
):
    """
    ## 📰 Obtener Lista de Noticias

    Retorna una lista de noticias ordenadas por fecha de publicación (más recientes primero).

    ### Parámetros:
    * **limit**: Cantidad de noticias a retornar (1-100, default: 50)
    * **activa**: Filtrar solo noticias activas (default: true)
    * **destacada**: Filtrar noticias destacadas (opcional)

    ### Respuesta:
    ```json
    {
        "success": true,
        "count": 7,
        "noticias": [
            {
                "id": "abc123",
                "titulo": "Título de la noticia",
                "descripcion": "Resumen breve",
                "contenido": "Contenido completo...",
                "imagenUrl": "https://...",
                "latitud": 0.3476,
                "longitud": -78.1223,
                "destacada": true,
                "activa": true,
                "visualizaciones": 150
            }
        ]
    }
    ```

    ### Códigos de respuesta:
    * **200**: Noticias obtenidas exitosamente
    * **500**: Error interno del servidor
    """
    try:
        query = db.collection("noticias")

        # Ordenar por fechaPublicacion sin filtros adicionales (evita índices compuestos)
        query = query.order_by("fechaPublicacion", direction=firestore.Query.DESCENDING)
        query = query.limit(limit)

        docs = query.get()

        noticias = []
        for doc in docs:
            noticia_data = doc.to_dict()

            # Filtrar manualmente en lugar de query de Firestore
            if activa is not None and noticia_data.get("activa") != activa:
                continue

            if destacada is not None and noticia_data.get("destacada") != destacada:
                continue

            # Crear un nuevo dict solo con datos serializables
            # Manejar categoriaId y parroquiaId (pueden ser string o DocumentReference)
            categoria_id = noticia_data.get("categoriaId")
            if categoria_id:
                if hasattr(categoria_id, 'id'):
                    categoria_id = categoria_id.id
                else:
                    categoria_id = str(categoria_id)

            parroquia_id = noticia_data.get("parroquiaId")
            if parroquia_id:
                if hasattr(parroquia_id, 'id'):
                    parroquia_id = parroquia_id.id
                else:
                    parroquia_id = str(parroquia_id)

            noticia_limpia = {
                "id": doc.id,
                "titulo": noticia_data.get("titulo"),
                "descripcion": noticia_data.get("descripcion"),
                "contenido": noticia_data.get("contenido"),
                "citaDestacada": noticia_data.get("citaDestacada"),
                "impactoComunitario": noticia_data.get("impactoComunitario"),
                "hashtags": noticia_data.get("hashtags"),
                "imagenUrl": noticia_data.get("imagenUrl"),
                "ubicacionTexto": noticia_data.get("ubicacionTexto"),
                "categoriaId": categoria_id,
                "parroquiaId": parroquia_id,
                "destacada": noticia_data.get("destacada", False),
                "activa": noticia_data.get("activa", True),
                "visualizaciones": noticia_data.get("visualizaciones", 0),
                "tags": noticia_data.get("tags", [])
            }

            # Convertir GeoPoint a dict
            if "ubicacion" in noticia_data and noticia_data["ubicacion"]:
                geopoint = noticia_data["ubicacion"]
                noticia_limpia["latitud"] = geopoint.latitude
                noticia_limpia["longitud"] = geopoint.longitude

            # Convertir Timestamp a ISO string
            if "fechaPublicacion" in noticia_data and noticia_data["fechaPublicacion"]:
                noticia_limpia["fechaPublicacion"] = noticia_data["fechaPublicacion"].isoformat()

            noticias.append(noticia_limpia)

        return {
            "success": True,
            "count": len(noticias),
            "noticias": noticias
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/noticias/cercanas", tags=["Noticias"])
async def obtener_noticias_cercanas(
    latitud: float = Query(..., description="Latitud del punto de referencia"),
    longitud: float = Query(..., description="Longitud del punto de referencia"),
    radio: float = Query(5, description="Radio de búsqueda en kilómetros")
):
    """
    ## 📍 Obtener Noticias Cercanas

    Retorna noticias dentro de un radio específico desde un punto geográfico.
    Utiliza el algoritmo de Haversine para calcular distancias.

    ### Parámetros:
    * **latitud**: Latitud del punto de referencia (-90 a 90)
    * **longitud**: Longitud del punto de referencia (-180 a 180)
    * **radio**: Radio de búsqueda en kilómetros (default: 5km)

    ### Respuesta:
    Lista de noticias ordenadas por distancia (más cercanas primero)
    """
    try:
        import math

        def calcular_distancia(lat1, lon1, lat2, lon2):
            """Calcula distancia en km usando fórmula de Haversine"""
            R = 6371  # Radio de la Tierra en km

            lat1_rad = math.radians(lat1)
            lat2_rad = math.radians(lat2)
            delta_lat = math.radians(lat2 - lat1)
            delta_lon = math.radians(lon2 - lon1)

            a = math.sin(delta_lat/2)**2 + math.cos(lat1_rad) * math.cos(lat2_rad) * math.sin(delta_lon/2)**2
            c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))

            return R * c

        # Obtener todas las noticias activas
        query = db.collection("noticias").where("activa", "==", True)
        docs = query.get()

        noticias_cercanas = []
        for doc in docs:
            noticia_data = doc.to_dict()

            # Verificar que tenga coordenadas
            if "ubicacion" not in noticia_data or not noticia_data["ubicacion"]:
                continue

            geopoint = noticia_data["ubicacion"]

            # Calcular distancia
            distancia = calcular_distancia(
                latitud, longitud,
                geopoint.latitude, geopoint.longitude
            )

            # Filtrar por radio
            if distancia <= radio:
                noticia_limpia = {
                    "id": doc.id,
                    "titulo": noticia_data.get("titulo"),
                    "descripcion": noticia_data.get("descripcion"),
                    "imagenUrl": noticia_data.get("imagenUrl"),
                    "ubicacionTexto": noticia_data.get("ubicacionTexto"),
                    "latitud": geopoint.latitude,
                    "longitud": geopoint.longitude,
                    "distancia": round(distancia, 2),
                    "destacada": noticia_data.get("destacada", False),
                    "visualizaciones": noticia_data.get("visualizaciones", 0)
                }

                # Convertir timestamp
                if "fechaPublicacion" in noticia_data and noticia_data["fechaPublicacion"]:
                    noticia_limpia["fechaPublicacion"] = noticia_data["fechaPublicacion"].isoformat()

                noticias_cercanas.append(noticia_limpia)

        # Ordenar por distancia (más cercanas primero)
        noticias_cercanas.sort(key=lambda x: x["distancia"])

        return {
            "success": True,
            "count": len(noticias_cercanas),
            "radio_km": radio,
            "centro": {
                "latitud": latitud,
                "longitud": longitud
            },
            "noticias": noticias_cercanas
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/noticias/{noticia_id}", tags=["Noticias"])
async def obtener_noticia(noticia_id: str):
    """
    ## 📄 Obtener Noticia por ID

    Retorna los detalles completos de una noticia específica.

    ### Parámetros de ruta:
    * **noticia_id**: ID único de la noticia en Firestore

    ### Códigos de respuesta:
    * **200**: Noticia encontrada
    * **404**: Noticia no encontrada
    * **500**: Error del servidor
    """
    try:
        doc = db.collection("noticias").document(noticia_id).get()

        if not doc.exists:
            raise HTTPException(status_code=404, detail="Noticia no encontrada")

        noticia_data = doc.to_dict()
        noticia_data["id"] = doc.id

        # Convertir GeoPoint
        if "ubicacion" in noticia_data and noticia_data["ubicacion"]:
            geopoint = noticia_data["ubicacion"]
            noticia_data["latitud"] = geopoint.latitude
            noticia_data["longitud"] = geopoint.longitude
            del noticia_data["ubicacion"]

        # Incrementar visualizaciones
        db.collection("noticias").document(noticia_id).update({
            "visualizaciones": firestore.Increment(1)
        })

        return {
            "success": True,
            "noticia": noticia_data
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/noticias", tags=["Noticias"])
async def crear_noticia(noticia: Noticia):
    """
    ## ✏️ Crear Nueva Noticia

    Crea una nueva noticia en Firestore con geolocalización y metadatos.

    ### Body (JSON):
    * **titulo**: Título de la noticia (requerido)
    * **descripcion**: Resumen breve
    * **contenido**: Texto completo de la noticia
    * **imagenUrl**: URL de la imagen principal
    * **latitud/longitud**: Coordenadas geográficas
    * **categoriaId**: ID de la categoría
    * **parroquiaId**: ID de la parroquia
    * **destacada**: Si aparece en portada (default: false)
    * **tags**: Array de etiquetas

    ### Códigos de respuesta:
    * **201**: Noticia creada exitosamente
    * **400**: Datos inválidos
    * **500**: Error del servidor
    """
    try:
        noticia_data = noticia.dict()

        # Agregar campos automáticos
        noticia_data["fechaPublicacion"] = datetime.now()
        noticia_data["fechaCreacion"] = datetime.now()
        noticia_data["visualizaciones"] = 0

        # Convertir coordenadas a GeoPoint
        if noticia.latitud and noticia.longitud:
            noticia_data["ubicacion"] = firestore.GeoPoint(noticia.latitud, noticia.longitud)
            del noticia_data["latitud"]
            del noticia_data["longitud"]

        # Crear documento
        doc_ref = db.collection("noticias").add(noticia_data)

        return {
            "success": True,
            "message": "Noticia creada exitosamente",
            "id": doc_ref[1].id
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ==================== ENDPOINTS EVENTOS ====================

@app.get("/eventos", tags=["Eventos"])
async def obtener_eventos(
    limit: int = Query(50, ge=1, le=100, description="Número máximo de eventos a retornar"),
    futuros: bool = Query(True, description="Filtrar solo eventos futuros"),
    estado: Optional[str] = Query(None, description="Filtrar por estado (programado, en_curso, finalizado)")
):
    """
    ## 🎉 Obtener Lista de Eventos

    Retorna eventos comunitarios ordenados por fecha.

    ### Parámetros:
    * **limit**: Cantidad de eventos (1-100, default: 50)
    * **futuros**: Solo eventos futuros (default: true)
    * **estado**: Filtrar por estado específico

    ### Respuesta:
    Retorna array de eventos con:
    * Información básica (título, descripción, fecha)
    * Ubicación y coordenadas
    * Cupos disponibles
    * Costo y categoría

    ### Códigos de respuesta:
    * **200**: Eventos obtenidos exitosamente
    * **500**: Error del servidor
    """
    try:
        query = db.collection("eventos")

        # Ordenar por fecha sin filtros (evita índices compuestos)
        query = query.order_by("fecha", direction=firestore.Query.ASCENDING)
        query = query.limit(limit * 2)  # Obtener más para filtrar después

        docs = query.get()

        eventos = []
        now = datetime.now()

        for doc in docs:
            try:
                evento_data = doc.to_dict()

                # Filtrar manualmente por estado
                if estado and evento_data.get("estado") != estado:
                    continue

                # Verificar fecha antes de convertir
                fecha_evento = evento_data.get("fecha")
                if futuros and fecha_evento and fecha_evento < now:
                    continue

                # Crear un nuevo dict solo con datos serializables
                evento_limpio = {
                    "id": doc.id,
                    "descripcion": str(evento_data.get("descripcion", "")),
                    "ubicacionTexto": str(evento_data.get("ubicacionTexto", "")) if evento_data.get("ubicacionTexto") else None,
                    "categoriaEvento": str(evento_data.get("categoriaEvento", "otro")),
                    "cupoMaximo": int(evento_data.get("cupoMaximo")) if evento_data.get("cupoMaximo") else None,
                    "cupoActual": int(evento_data.get("cupoActual", 0)),
                    "costo": float(evento_data.get("costo", 0.0)) if evento_data.get("costo") is not None else 0.0,
                    "estado": str(evento_data.get("estado", "programado")),
                    "contactoTelefono": str(evento_data.get("contactoTelefono", "")) if evento_data.get("contactoTelefono") else None,
                    "contactoEmail": str(evento_data.get("contactoEmail", "")) if evento_data.get("contactoEmail") else None
                }

                # Manejar parroquiaId
                try:
                    parroquia_ref = evento_data.get("parroquiaId")
                    if parroquia_ref and hasattr(parroquia_ref, "id"):
                        evento_limpio["parroquiaId"] = str(parroquia_ref.id)
                    else:
                        evento_limpio["parroquiaId"] = None
                except:
                    evento_limpio["parroquiaId"] = None

                # Convertir GeoPoint
                try:
                    ubicacion_geo = evento_data.get("ubicacion")
                    if ubicacion_geo and hasattr(ubicacion_geo, "latitude"):
                        evento_limpio["latitud"] = float(ubicacion_geo.latitude)
                        evento_limpio["longitud"] = float(ubicacion_geo.longitude)
                except:
                    pass  # Si no hay coordenadas, simplemente las omitimos

                # Convertir Timestamp
                try:
                    if fecha_evento and hasattr(fecha_evento, "isoformat"):
                        evento_limpio["fecha"] = fecha_evento.isoformat()
                    elif fecha_evento:
                        evento_limpio["fecha"] = str(fecha_evento)
                except:
                    evento_limpio["fecha"] = None

                eventos.append(evento_limpio)

                # Limitar resultados después del filtrado
                if len(eventos) >= limit:
                    break
            except Exception as doc_error:
                # Continuar con el siguiente documento si uno falla
                print(f"Error procesando evento {doc.id}: {doc_error}")
                continue

        return {
            "success": True,
            "count": len(eventos),
            "eventos": eventos
        }

    except Exception as e:
        import traceback
        error_detail = f"{str(e)}\n{traceback.format_exc()}"
        raise HTTPException(status_code=500, detail=error_detail)

@app.get("/eventos/{evento_id}", tags=["Eventos"])
async def obtener_evento(evento_id: str):
    """
    ## 📅 Obtener Evento por ID

    Retorna los detalles completos de un evento específico.

    ### Respuesta incluye:
    * Información completa del evento
    * Cupos disponibles y ocupados
    * Ubicación y coordenadas
    * Estado actual del evento
    """
    try:
        doc = db.collection("eventos").document(evento_id).get()

        if not doc.exists:
            raise HTTPException(status_code=404, detail="Evento no encontrado")

        evento_data = doc.to_dict()
        evento_data["id"] = doc.id

        # Convertir GeoPoint
        if "ubicacion" in evento_data and evento_data["ubicacion"]:
            geopoint = evento_data["ubicacion"]
            evento_data["latitud"] = geopoint.latitude
            evento_data["longitud"] = geopoint.longitude
            del evento_data["ubicacion"]

        return {
            "success": True,
            "evento": evento_data
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/eventos", tags=["Eventos"])
async def crear_evento(evento: Evento):
    """
    ## ✨ Crear Nuevo Evento

    Crea un evento comunitario con gestión de cupos.

    ### Campos requeridos:
    * titulo, descripcion, fecha, hora
    * ubicacionTexto, latitud, longitud
    * cupoMaximo, costo

    ### Códigos de respuesta:
    * **201**: Evento creado
    * **500**: Error del servidor
    """
    try:
        evento_data = evento.dict()

        # Agregar campos automáticos
        evento_data["fechaCreacion"] = datetime.now()
        evento_data["asistentes"] = []

        # Convertir coordenadas a GeoPoint
        if evento.latitud and evento.longitud:
            evento_data["ubicacion"] = firestore.GeoPoint(evento.latitud, evento.longitud)
            del evento_data["latitud"]
            del evento_data["longitud"]

        # Crear documento
        doc_ref = db.collection("eventos").add(evento_data)

        return {
            "success": True,
            "message": "Evento creado exitosamente",
            "id": doc_ref[1].id
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.put("/noticias/{noticia_id}/coordenadas", tags=["Noticias"])
async def actualizar_coordenadas_noticia(
    noticia_id: str,
    latitud: float = Query(..., description="Latitud (-90 a 90)"),
    longitud: float = Query(..., description="Longitud (-180 a 180)"),
    ubicacionTexto: Optional[str] = Query(None, description="Descripción textual del lugar")
):
    """
    ## 📍 Actualizar Coordenadas de Noticia

    Actualiza las coordenadas geográficas de una noticia existente.

    ### Parámetros:
    * **noticia_id**: ID de la noticia (ruta)
    * **latitud**: Coordenada latitud (-90 a 90)
    * **longitud**: Coordenada longitud (-180 a 180)
    * **ubicacionTexto**: Descripción opcional del lugar (ej: "Parque Central")

    ### Ejemplo de uso:
    ```
    PUT /noticias/abc123/coordenadas?latitud=0.3476&longitud=-78.1223&ubicacionTexto=Centro de Ibarra
    ```

    ### Códigos de respuesta:
    * **200**: Coordenadas actualizadas exitosamente
    * **404**: Noticia no encontrada
    * **400**: Coordenadas inválidas
    * **500**: Error del servidor
    """
    try:
        # Validar coordenadas
        if not (-90 <= latitud <= 90):
            raise HTTPException(status_code=400, detail="Latitud debe estar entre -90 y 90")
        if not (-180 <= longitud <= 180):
            raise HTTPException(status_code=400, detail="Longitud debe estar entre -180 y 180")

        # Verificar que la noticia existe
        doc_ref = db.collection("noticias").document(noticia_id)
        doc = doc_ref.get()

        if not doc.exists:
            raise HTTPException(status_code=404, detail="Noticia no encontrada")

        # Actualizar coordenadas
        update_data = {
            "ubicacion": firestore.GeoPoint(latitud, longitud)
        }

        if ubicacionTexto:
            update_data["ubicacionTexto"] = ubicacionTexto

        doc_ref.update(update_data)

        return {
            "success": True,
            "message": "Coordenadas actualizadas exitosamente",
            "noticia_id": noticia_id,
            "latitud": latitud,
            "longitud": longitud,
            "ubicacionTexto": ubicacionTexto
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/eventos/{evento_id}/inscribir", tags=["Eventos"])
async def inscribir_evento(evento_id: str, usuario_id: str):
    """
    ## 📝 Inscribir Usuario a Evento

    Registra a un usuario en un evento verificando cupos disponibles.

    ### Parámetros:
    * **evento_id**: ID del evento (ruta)
    * **usuario_id**: ID del usuario (query)

    ### Validaciones:
    * Verifica que el evento exista
    * Comprueba cupos disponibles
    * Incrementa contador de asistentes

    ### Códigos de respuesta:
    * **200**: Inscripción exitosa
    * **400**: No hay cupos disponibles
    * **404**: Evento no encontrado
    """
    try:
        doc_ref = db.collection("eventos").document(evento_id)
        doc = doc_ref.get()

        if not doc.exists:
            raise HTTPException(status_code=404, detail="Evento no encontrado")

        evento_data = doc.to_dict()

        # Verificar cupos
        cupo_maximo = evento_data.get("cupoMaximo")
        cupo_actual = evento_data.get("cupoActual", 0)

        if cupo_maximo and cupo_actual >= cupo_maximo:
            raise HTTPException(status_code=400, detail="No hay cupos disponibles")

        # Incrementar cupo actual
        doc_ref.update({
            "cupoActual": firestore.Increment(1),
            "asistentes": firestore.ArrayUnion([usuario_id])
        })

        return {
            "success": True,
            "message": "Inscripción exitosa"
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ==================== NOTIFICACIONES PUSH ====================

@app.post("/notificaciones/enviar", tags=["Notificaciones"])
async def enviar_notificacion(notificacion: NotificacionPush):
    """
    ## 🔔 Enviar Notificación Push

    Envía notificación a usuarios suscritos mediante Firebase Cloud Messaging.

    ### Body:
    * **titulo**: Título de la notificación
    * **mensaje**: Contenido del mensaje
    * **topic**: Topic de FCM (default: "todas")
    * **data**: Datos adicionales (opcional)

    ### Ejemplo:
    ```json
    {
        "titulo": "Nueva Noticia",
        "mensaje": "Se publicó una noticia destacada",
        "topic": "noticias",
        "data": {"noticia_id": "abc123"}
    }
    ```
    """
    try:
        # Crear mensaje FCM
        message = messaging.Message(
            notification=messaging.Notification(
                title=notificacion.titulo,
                body=notificacion.mensaje,
            ),
            data=notificacion.data or {},
            topic=notificacion.topic
        )

        # Enviar notificación
        response = messaging.send(message)

        return {
            "success": True,
            "message": "Notificación enviada",
            "message_id": response
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/notificaciones/nueva-noticia", tags=["Notificaciones"])
async def notificar_nueva_noticia(noticia_id: str):
    """
    ## 📢 Notificar Nueva Noticia

    Envía notificación automática cuando se publica una noticia destacada.

    ### Parámetros:
    * **noticia_id**: ID de la noticia a notificar

    ### Funcionamiento:
    1. Obtiene datos de la noticia
    2. Crea notificación con título y descripción
    3. Envía a topic "noticias"
    """
    try:
        # Obtener noticia
        doc = db.collection("noticias").document(noticia_id).get()

        if not doc.exists:
            raise HTTPException(status_code=404, detail="Noticia no encontrada")

        noticia_data = doc.to_dict()

        # Enviar notificación
        message = messaging.Message(
            notification=messaging.Notification(
                title="Nueva noticia en Ibarra",
                body=noticia_data.get("titulo", ""),
            ),
            data={
                "type": "noticia_nueva",
                "noticia_id": noticia_id
            },
            topic="all"
        )

        response = messaging.send(message)

        return {
            "success": True,
            "message": "Notificación enviada",
            "message_id": response
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ==================== ESTADÍSTICAS ====================

@app.get("/stats", tags=["Estadísticas"])
async def obtener_estadisticas():
    """
    ## 📊 Estadísticas Generales

    Retorna métricas y estadísticas del sistema.

    ### Métricas incluidas:
    * Total de noticias y eventos
    * Noticias destacadas
    * Visualizaciones totales
    * Distribución por parroquias
    * Eventos próximos

    ### Respuesta:
    ```json
    {
        "noticias": {
            "total": 7,
            "destacadas": 3,
            "visualizaciones_totales": 2450
        },
        "eventos": {
            "total": 5,
            "proximos": 3
        },
        "parroquias": 12
    }
    ```
    """
    try:
        total_noticias = len(db.collection("noticias").get())
        total_eventos = len(db.collection("eventos").get())
        total_parroquias = len(db.collection("parroquias").get())
        total_categorias = len(db.collection("categorias").get())

        return {
            "success": True,
            "stats": {
                "noticias": total_noticias,
                "eventos": total_eventos,
                "parroquias": total_parroquias,
                "categorias": total_categorias
            }
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    port = int(os.environ.get("PORT", 8080))
    uvicorn.run(app, host="0.0.0.0", port=port)
