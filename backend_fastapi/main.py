"""
FastAPI Backend para NoticiasIbarra
Conecta con Firestore y Firebase Cloud Messaging
"""

from fastapi import FastAPI, HTTPException, Query
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime
import firebase_admin
from firebase_admin import credentials, firestore, messaging
import os

# Inicializar FastAPI
app = FastAPI(
    title="NoticiasIbarra API",
    description="Backend para gestión de noticias y eventos de Ibarra",
    version="1.0.0"
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

# ==================== MODELOS PYDANTIC ====================

class Noticia(BaseModel):
    titulo: str
    descripcion: Optional[str] = None
    contenido: Optional[str] = None
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

@app.get("/")
async def root():
    """Endpoint raíz - Health check"""
    return {
        "status": "ok",
        "message": "NoticiasIbarra API está funcionando",
        "version": "1.0.0",
        "firebase": "connected"
    }

@app.get("/health")
async def health_check():
    """Health check para Cloud Run"""
    try:
        # Verificar conexión a Firestore
        db.collection("noticias").limit(1).get()
        return {"status": "healthy", "firestore": "connected"}
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"Firestore error: {str(e)}")

# ==================== ENDPOINTS NOTICIAS ====================

@app.get("/noticias")
async def obtener_noticias(
    limit: int = Query(50, ge=1, le=100),
    activa: bool = True,
    destacada: Optional[bool] = None
):
    """Obtiene lista de noticias con filtros opcionales"""
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
            noticia_limpia = {
                "id": doc.id,
                "titulo": noticia_data.get("titulo"),
                "descripcion": noticia_data.get("descripcion"),
                "contenido": noticia_data.get("contenido"),
                "imagenUrl": noticia_data.get("imagenUrl"),
                "ubicacionTexto": noticia_data.get("ubicacionTexto"),
                "categoriaId": str(noticia_data.get("categoriaId").id) if noticia_data.get("categoriaId") else None,
                "parroquiaId": str(noticia_data.get("parroquiaId").id) if noticia_data.get("parroquiaId") else None,
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

@app.get("/noticias/{noticia_id}")
async def obtener_noticia(noticia_id: str):
    """Obtiene una noticia por ID"""
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

@app.post("/noticias")
async def crear_noticia(noticia: Noticia):
    """Crea una nueva noticia"""
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

@app.get("/eventos")
async def obtener_eventos(
    limit: int = Query(50, ge=1, le=100),
    futuros: bool = True,
    estado: Optional[str] = None
):
    """Obtiene lista de eventos"""
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

@app.get("/eventos/{evento_id}")
async def obtener_evento(evento_id: str):
    """Obtiene un evento por ID"""
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

@app.post("/eventos")
async def crear_evento(evento: Evento):
    """Crea un nuevo evento"""
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

@app.post("/eventos/{evento_id}/inscribir")
async def inscribir_evento(evento_id: str, usuario_id: str):
    """Inscribe a un usuario en un evento"""
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

@app.post("/notificaciones/enviar")
async def enviar_notificacion(notificacion: NotificacionPush):
    """Envía una notificación push a un topic de FCM"""
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

@app.post("/notificaciones/nueva-noticia")
async def notificar_nueva_noticia(noticia_id: str):
    """Envía notificación cuando se publica una nueva noticia"""
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

@app.get("/stats")
async def obtener_estadisticas():
    """Obtiene estadísticas generales"""
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
