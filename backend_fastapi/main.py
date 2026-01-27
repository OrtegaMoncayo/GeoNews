"""
FastAPI Backend para NoticiasIbarra
Conecta con Firestore y Firebase Cloud Messaging
"""

from fastapi import FastAPI, HTTPException, Query, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import HTMLResponse
from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime
from cachetools import TTLCache
import firebase_admin
from firebase_admin import credentials, firestore, messaging
import os
from pathlib import Path
import io

# Caché en memoria para respuestas rápidas (5 minutos TTL)
noticias_cache = TTLCache(maxsize=50, ttl=300)  # 5 minutos
parroquias_cache = TTLCache(maxsize=20, ttl=600)  # 10 minutos
categorias_cache = TTLCache(maxsize=20, ttl=600)  # 10 minutos

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

# Inicializar Google Drive API (usa las mismas credenciales de Firebase)
drive_service = None
DRIVE_FOLDER_ID = os.environ.get("GOOGLE_DRIVE_FOLDER_ID", None)  # ID de carpeta en Drive

try:
    from googleapiclient.discovery import build
    from google.oauth2 import service_account

    # Usar las mismas credenciales de Firebase para Drive
    SCOPES = ['https://www.googleapis.com/auth/drive.file']
    drive_credentials = service_account.Credentials.from_service_account_file(
        'serviceAccountKey.json', scopes=SCOPES
    )
    drive_service = build('drive', 'v3', credentials=drive_credentials)
    print("✅ Google Drive API inicializada correctamente")
except Exception as e:
    print(f"⚠️ Google Drive API no disponible: {e}")
    drive_service = None

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
    # Campos de autoría
    autorNombre: Optional[str] = None
    autorId: Optional[str] = None
    esContenidoPropio: bool = True
    urlFuente: Optional[str] = None

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

class EmailDigestRequest(BaseModel):
    email: str
    nombre: str
    frecuencia: str = "daily"  # "daily" o "weekly"

class LoginRequest(BaseModel):
    email: str
    password: str

class LoginResponse(BaseModel):
    success: bool
    message: str
    usuario: Optional[dict] = None
    token: Optional[str] = None

class RegistrarPeriodistaRequest(BaseModel):
    email: str
    password: str
    nombre: str
    apellido: Optional[str] = None
    telefono: Optional[str] = None

class CambiarPasswordRequest(BaseModel):
    email: str
    password_actual: str
    password_nueva: str

class SolicitarCodigoRequest(BaseModel):
    email: str

class VerificarCodigoRequest(BaseModel):
    email: str
    codigo: str

class ResetearPasswordRequest(BaseModel):
    email: str
    codigo: str
    password_nueva: str

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

@app.post("/api/auth/login", tags=["Sistema"])
async def login(request: LoginRequest):
    """
    ## 🔐 Login de Periodista

    Autentica a un usuario y verifica que tenga rol de periodista o admin.

    ### Body (JSON):
    * **email**: Email del usuario
    * **password**: Contraseña del usuario

    ### Respuesta exitosa:
    ```json
    {
        "success": true,
        "message": "Login exitoso",
        "usuario": {
            "id": "abc123",
            "email": "periodista@email.com",
            "nombre": "Juan",
            "apellido": "Pérez",
            "tipoUsuario": "reportero"
        },
        "token": "token_generado"
    }
    ```

    ### Códigos de respuesta:
    * **200**: Login exitoso
    * **401**: Credenciales inválidas o usuario no autorizado
    * **404**: Usuario no encontrado
    """
    try:
        import hashlib
        import secrets

        # Buscar usuario por email en Firestore
        usuarios_ref = db.collection("usuarios").where("email", "==", request.email).limit(1)
        usuarios = list(usuarios_ref.stream())

        if not usuarios:
            raise HTTPException(
                status_code=404,
                detail="Usuario no encontrado"
            )

        usuario_doc = usuarios[0]
        usuario_data = usuario_doc.to_dict()
        usuario_id = usuario_doc.id

        # Verificar contraseña (comparar con hash almacenado)
        password_almacenada = usuario_data.get("password", "")

        # Si la contraseña está hasheada, comparar hash
        password_hash = hashlib.sha256(request.password.encode()).hexdigest()

        # Permitir login si: coincide el hash O coincide texto plano (para desarrollo)
        if password_almacenada != password_hash and password_almacenada != request.password:
            raise HTTPException(
                status_code=401,
                detail="Contraseña incorrecta"
            )

        # Verificar rol del usuario (soporta 'rol' y 'tipoUsuario')
        tipo_usuario = usuario_data.get("rol") or usuario_data.get("tipoUsuario", "usuario")

        if tipo_usuario not in ["reportero", "admin"]:
            raise HTTPException(
                status_code=401,
                detail="No tienes permisos para acceder al panel de periodista. Solo reporteros y administradores pueden acceder."
            )

        # Generar token simple (en producción usar JWT)
        token = secrets.token_hex(32)

        # Actualizar última conexión
        db.collection("usuarios").document(usuario_id).update({
            "ultimaConexion": datetime.now()
        })

        return {
            "success": True,
            "message": "Login exitoso",
            "usuario": {
                "id": usuario_id,
                "email": usuario_data.get("email"),
                "nombre": usuario_data.get("nombre"),
                "apellido": usuario_data.get("apellido"),
                "tipoUsuario": tipo_usuario,
                "fotoPerfil": usuario_data.get("fotoPerfil")
            },
            "token": token
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error en login: {str(e)}")


@app.post("/api/admin/registrar-periodista", tags=["Sistema"])
async def registrar_periodista(request: RegistrarPeriodistaRequest):
    """
    ## 👤 Registrar Nuevo Periodista (Solo Admin)

    Crea un nuevo usuario con rol de periodista.

    ### Body (JSON):
    * **email**: Email del periodista (requerido)
    * **password**: Contraseña (requerido)
    * **nombre**: Nombre del periodista (requerido)
    * **apellido**: Apellido (opcional)
    * **telefono**: Teléfono de contacto (opcional)

    ### Respuesta exitosa:
    ```json
    {
        "success": true,
        "message": "Periodista registrado exitosamente",
        "usuario_id": "abc123"
    }
    ```

    ### Códigos de respuesta:
    * **201**: Periodista registrado
    * **400**: Email ya existe
    * **500**: Error del servidor
    """
    try:
        import hashlib

        # Verificar si el email ya existe
        usuarios_existentes = db.collection("usuarios").where("email", "==", request.email).limit(1)
        if list(usuarios_existentes.stream()):
            raise HTTPException(
                status_code=400,
                detail="Ya existe un usuario con este email"
            )

        # Hashear contraseña
        password_hash = hashlib.sha256(request.password.encode()).hexdigest()

        # Crear documento de usuario
        nuevo_usuario = {
            "email": request.email,
            "password": password_hash,
            "nombre": request.nombre,
            "apellido": request.apellido or "",
            "telefonocelular": request.telefono or "",
            "tipoUsuario": "reportero",  # Rol de periodista
            "fechaRegistro": datetime.now(),
            "ultimaConexion": None,
            "fotoPerfil": None,
            "bio": "",
            "ubicacion": "Ibarra, Ecuador",
            "noticiasPublicadas": 0,
            "noticiasLeidas": 0,
            "verificado": True
        }

        # Guardar en Firestore
        doc_ref = db.collection("usuarios").add(nuevo_usuario)

        return {
            "success": True,
            "message": "Periodista registrado exitosamente",
            "usuario_id": doc_ref[1].id
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al registrar periodista: {str(e)}")


@app.get("/api/admin/periodistas", tags=["Sistema"])
async def listar_periodistas():
    """
    ## 📋 Listar Periodistas (Solo Admin)

    Obtiene la lista de todos los periodistas registrados con el conteo real de artículos publicados.

    ### Respuesta:
    ```json
    {
        "success": true,
        "count": 5,
        "periodistas": [
            {
                "id": "abc123",
                "email": "periodista@email.com",
                "nombre": "Juan Pérez",
                "noticiasPublicadas": 15
            }
        ]
    }
    ```
    """
    try:
        periodistas = []
        seen_ids = set()

        # Primero, contar noticias por autor desde la colección de noticias
        noticias_por_autor = {}
        for noticia_doc in db.collection("noticias").stream():
            noticia_data = noticia_doc.to_dict()
            autor_id = noticia_data.get("autorId")
            if autor_id:
                noticias_por_autor[autor_id] = noticias_por_autor.get(autor_id, 0) + 1

        # Buscar por 'rol' = 'reportero'
        for doc in db.collection("usuarios").where("rol", "==", "reportero").stream():
            if doc.id not in seen_ids:
                seen_ids.add(doc.id)
                data = doc.to_dict()
                # Usar conteo real de noticias desde la colección
                conteo_real = noticias_por_autor.get(doc.id, 0)
                periodistas.append({
                    "id": doc.id,
                    "email": data.get("email"),
                    "nombre": f"{data.get('nombre', '')} {data.get('apellido', '')}".strip(),
                    "telefono": data.get("telefonocelular"),
                    "noticiasPublicadas": conteo_real,
                    "verificado": data.get("verificado", False),
                    "fechaRegistro": data.get("fechaRegistro").isoformat() if data.get("fechaRegistro") else None
                })

        # Buscar por 'tipoUsuario' = 'reportero'
        for doc in db.collection("usuarios").where("tipoUsuario", "==", "reportero").stream():
            if doc.id not in seen_ids:
                seen_ids.add(doc.id)
                data = doc.to_dict()
                # Usar conteo real de noticias desde la colección
                conteo_real = noticias_por_autor.get(doc.id, 0)
                periodistas.append({
                    "id": doc.id,
                    "email": data.get("email"),
                    "nombre": f"{data.get('nombre', '')} {data.get('apellido', '')}".strip(),
                    "telefono": data.get("telefonocelular"),
                    "noticiasPublicadas": conteo_real,
                    "verificado": data.get("verificado", False),
                    "fechaRegistro": data.get("fechaRegistro").isoformat() if data.get("fechaRegistro") else None
                })

        return {
            "success": True,
            "count": len(periodistas),
            "periodistas": periodistas
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al listar periodistas: {str(e)}")


# ==================== SUBIDA DE IMÁGENES ====================

@app.post("/api/upload/drive", tags=["Sistema"])
async def subir_imagen_drive(file: UploadFile = File(...)):
    """
    ## 📷 Subir Imagen a Google Drive

    Sube una imagen a Google Drive y retorna la URL pública.

    ### Parámetros:
    * **file**: Archivo de imagen (JPG, PNG, GIF, WebP)

    ### Respuesta exitosa:
    ```json
    {
        "success": true,
        "url": "https://drive.google.com/uc?id=...",
        "file_id": "abc123",
        "filename": "imagen.jpg"
    }
    ```
    """
    if not drive_service:
        raise HTTPException(
            status_code=503,
            detail="Google Drive no está configurado. Use Firebase Storage."
        )

    # Validar tipo de archivo
    allowed_types = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
    if file.content_type not in allowed_types:
        raise HTTPException(
            status_code=400,
            detail=f"Tipo de archivo no permitido. Permitidos: {', '.join(allowed_types)}"
        )

    # Validar tamaño (máximo 10MB)
    contents = await file.read()
    if len(contents) > 10 * 1024 * 1024:
        raise HTTPException(
            status_code=400,
            detail="El archivo es muy grande. Máximo 10MB."
        )

    try:
        from googleapiclient.http import MediaIoBaseUpload

        # Generar nombre único
        import uuid
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        extension = file.filename.split('.')[-1] if '.' in file.filename else 'jpg'
        unique_filename = f"geonews_{timestamp}_{uuid.uuid4().hex[:8]}.{extension}"

        # Preparar metadata del archivo
        file_metadata = {
            'name': unique_filename,
            'mimeType': file.content_type
        }

        # Si hay carpeta configurada, usarla
        if DRIVE_FOLDER_ID:
            file_metadata['parents'] = [DRIVE_FOLDER_ID]

        # Crear el archivo en Drive
        media = MediaIoBaseUpload(
            io.BytesIO(contents),
            mimetype=file.content_type,
            resumable=True
        )

        uploaded_file = drive_service.files().create(
            body=file_metadata,
            media_body=media,
            fields='id, name, webViewLink, webContentLink'
        ).execute()

        file_id = uploaded_file.get('id')

        # Hacer el archivo público
        drive_service.permissions().create(
            fileId=file_id,
            body={'type': 'anyone', 'role': 'reader'}
        ).execute()

        # URL directa para mostrar la imagen
        image_url = f"https://drive.google.com/uc?export=view&id={file_id}"

        return {
            "success": True,
            "url": image_url,
            "file_id": file_id,
            "filename": unique_filename,
            "web_link": uploaded_file.get('webViewLink')
        }

    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Error al subir a Google Drive: {str(e)}"
        )


@app.get("/api/admin/crear-admin-provisional", tags=["Sistema"])
async def crear_admin_provisional():
    """
    ## 🔧 Crear Admin Provisional

    Crea un usuario admin provisional para acceder al panel.

    **Credenciales:**
    - Email: admin@geonews.com
    - Password: admin123 (cambiar después de entrar)
    """
    try:
        import hashlib

        email_admin = "admin@geonews.com"
        password_temporal = "admin123"

        # Verificar si ya existe
        existente = list(db.collection("usuarios").where("email", "==", email_admin).limit(1).stream())
        if existente:
            return {
                "success": True,
                "message": "El admin provisional ya existe",
                "email": email_admin,
                "password": password_temporal
            }

        # Hashear contraseña
        password_hash = hashlib.sha256(password_temporal.encode()).hexdigest()

        # Crear admin provisional
        admin_data = {
            "email": email_admin,
            "password": password_hash,
            "nombre": "Administrador",
            "apellido": "GeoNews",
            "rol": "admin",
            "tipoUsuario": "admin",
            "fechaRegistro": datetime.now(),
            "activo": True,
            "verificado": True,
            "noticiasPublicadas": 0,
            "noticiasLeidas": 0,
            "bio": "Administrador provisional del sistema",
            "ubicacion": "Ibarra, Ecuador"
        }

        doc_ref = db.collection("usuarios").add(admin_data)

        return {
            "success": True,
            "message": "Admin provisional creado exitosamente",
            "usuario_id": doc_ref[1].id,
            "email": email_admin,
            "password": password_temporal,
            "nota": "¡Cambia la contraseña después de entrar!"
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al crear admin: {str(e)}")


@app.post("/api/auth/cambiar-password", tags=["Sistema"])
async def cambiar_password(request: CambiarPasswordRequest):
    """
    ## 🔐 Cambiar Contraseña

    Permite cambiar la contraseña de un usuario.

    ### Body (JSON):
    * **email**: Email del usuario
    * **password_actual**: Contraseña actual
    * **password_nueva**: Nueva contraseña (mínimo 6 caracteres)

    ### Respuesta exitosa:
    ```json
    {
        "success": true,
        "message": "Contraseña actualizada exitosamente"
    }
    ```

    ### Códigos de respuesta:
    * **200**: Contraseña cambiada exitosamente
    * **400**: Nueva contraseña muy corta
    * **401**: Contraseña actual incorrecta
    * **404**: Usuario no encontrado
    """
    try:
        import hashlib

        # Validar longitud de nueva contraseña
        if len(request.password_nueva) < 6:
            raise HTTPException(
                status_code=400,
                detail="La nueva contraseña debe tener al menos 6 caracteres"
            )

        # Buscar usuario
        usuarios = list(db.collection("usuarios").where("email", "==", request.email).limit(1).stream())
        if not usuarios:
            raise HTTPException(status_code=404, detail="Usuario no encontrado")

        usuario_doc = usuarios[0]
        usuario_data = usuario_doc.to_dict()

        # Verificar contraseña actual
        password_actual_hash = hashlib.sha256(request.password_actual.encode()).hexdigest()
        password_guardada = usuario_data.get("password", "")

        if password_guardada != password_actual_hash and password_guardada != request.password_actual:
            raise HTTPException(status_code=401, detail="Contraseña actual incorrecta")

        # Actualizar contraseña
        password_nueva_hash = hashlib.sha256(request.password_nueva.encode()).hexdigest()
        db.collection("usuarios").document(usuario_doc.id).update({
            "password": password_nueva_hash,
            "passwordCambiada": True,
            "ultimoCambioPassword": datetime.now()
        })

        return {
            "success": True,
            "message": "Contraseña actualizada exitosamente"
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al cambiar contraseña: {str(e)}")


@app.post("/api/auth/solicitar-codigo", tags=["Sistema"])
async def solicitar_codigo_recuperacion(request: SolicitarCodigoRequest):
    """
    ## 📧 Solicitar Código de Recuperación

    Genera un código de 6 dígitos y lo envía al email del usuario.
    El código expira en 10 minutos.

    ### Body (JSON):
    * **email**: Email del usuario registrado

    ### Respuesta exitosa:
    ```json
    {
        "success": true,
        "message": "Código enviado al email",
        "expira_en": "10 minutos"
    }
    ```
    """
    try:
        import random
        import smtplib
        from email.mime.text import MIMEText
        from email.mime.multipart import MIMEMultipart

        # Verificar que el usuario existe
        usuarios = list(db.collection("usuarios").where("email", "==", request.email).limit(1).stream())
        if not usuarios:
            raise HTTPException(status_code=404, detail="No existe una cuenta con este email")

        # Generar código de 6 dígitos
        codigo = ''.join([str(random.randint(0, 9)) for _ in range(6)])

        # Guardar código en Firestore con expiración
        codigo_data = {
            "email": request.email,
            "codigo": codigo,
            "creado": datetime.now(),
            "expira": datetime.now().replace(minute=datetime.now().minute + 10),
            "usado": False
        }

        # Eliminar códigos anteriores del mismo email
        codigos_anteriores = db.collection("codigos_recuperacion").where("email", "==", request.email).stream()
        for doc in codigos_anteriores:
            doc.reference.delete()

        # Guardar nuevo código
        db.collection("codigos_recuperacion").add(codigo_data)

        # Preparar email HTML
        html_content = f"""
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body {{ font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }}
                .container {{ max-width: 500px; margin: 0 auto; background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }}
                .header {{ text-align: center; margin-bottom: 30px; }}
                .header h1 {{ color: #1976D2; margin: 0; }}
                .code-box {{ background: #f5f5f5; border-radius: 10px; padding: 20px; text-align: center; margin: 20px 0; }}
                .code {{ font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #333; }}
                .warning {{ color: #666; font-size: 14px; margin-top: 20px; }}
                .footer {{ text-align: center; color: #999; font-size: 12px; margin-top: 30px; }}
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🔐 GeoNews</h1>
                    <p>Código de Recuperación de Contraseña</p>
                </div>
                <p>Hola,</p>
                <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta. Usa el siguiente código:</p>
                <div class="code-box">
                    <div class="code">{codigo}</div>
                </div>
                <p class="warning">
                    ⏱️ Este código expira en <strong>10 minutos</strong>.<br>
                    Si no solicitaste este código, ignora este mensaje.
                </p>
                <div class="footer">
                    <p>© 2026 GeoNews - Noticias Locales de Ibarra</p>
                </div>
            </div>
        </body>
        </html>
        """

        # Configurar SMTP
        smtp_host = os.getenv("SMTP_HOST", "smtp.gmail.com")
        smtp_port = int(os.getenv("SMTP_PORT", "587"))
        smtp_user = os.getenv("SMTP_USER", "")
        smtp_password = os.getenv("SMTP_PASSWORD", "")

        if smtp_user and smtp_password:
            # Crear mensaje
            message = MIMEMultipart("alternative")
            message["Subject"] = f"🔐 Tu código de verificación: {codigo}"
            message["From"] = f"GeoNews <{smtp_user}>"
            message["To"] = request.email
            message.attach(MIMEText(html_content, "html"))

            # Enviar email
            with smtplib.SMTP(smtp_host, smtp_port) as server:
                server.starttls()
                server.login(smtp_user, smtp_password)
                server.send_message(message)

            return {
                "success": True,
                "message": "Código enviado al email",
                "expira_en": "10 minutos"
            }
        else:
            # En desarrollo, retornar el código directamente
            return {
                "success": True,
                "message": "Código generado (SMTP no configurado)",
                "codigo_debug": codigo,  # Solo en desarrollo
                "expira_en": "10 minutos"
            }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al enviar código: {str(e)}")


@app.post("/api/auth/verificar-codigo", tags=["Sistema"])
async def verificar_codigo(request: VerificarCodigoRequest):
    """
    ## ✅ Verificar Código de Recuperación

    Verifica que el código ingresado sea válido y no haya expirado.

    ### Body (JSON):
    * **email**: Email del usuario
    * **codigo**: Código de 6 dígitos recibido

    ### Respuesta exitosa:
    ```json
    {
        "success": true,
        "message": "Código válido",
        "token_temporal": "abc123..."
    }
    ```
    """
    try:
        import secrets

        # Buscar código válido
        codigos = list(db.collection("codigos_recuperacion")
            .where("email", "==", request.email)
            .where("codigo", "==", request.codigo)
            .where("usado", "==", False)
            .limit(1)
            .stream())

        if not codigos:
            raise HTTPException(status_code=400, detail="Código inválido o expirado")

        codigo_doc = codigos[0]
        codigo_data = codigo_doc.to_dict()

        # Verificar expiración (10 minutos)
        creado = codigo_data.get("creado")
        if creado:
            # Convertir a timestamp naive para comparar
            if hasattr(creado, 'timestamp'):
                creado_timestamp = creado.timestamp()
            else:
                creado_timestamp = creado.replace(tzinfo=None).timestamp() if hasattr(creado, 'replace') else 0

            diferencia = datetime.now().timestamp() - creado_timestamp
            if diferencia > 600:  # 10 minutos en segundos
                codigo_doc.reference.delete()
                raise HTTPException(status_code=400, detail="El código ha expirado. Solicita uno nuevo.")

        # Generar token temporal para resetear password
        token_temporal = secrets.token_hex(32)

        # Actualizar documento con token
        codigo_doc.reference.update({
            "token_temporal": token_temporal,
            "verificado": True
        })

        return {
            "success": True,
            "message": "Código válido",
            "token_temporal": token_temporal
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al verificar código: {str(e)}")


@app.post("/api/auth/resetear-password", tags=["Sistema"])
async def resetear_password(request: ResetearPasswordRequest):
    """
    ## 🔑 Resetear Contraseña con Código

    Permite cambiar la contraseña usando el código de verificación.

    ### Body (JSON):
    * **email**: Email del usuario
    * **codigo**: Código de 6 dígitos
    * **password_nueva**: Nueva contraseña (mínimo 6 caracteres)

    ### Respuesta exitosa:
    ```json
    {
        "success": true,
        "message": "Contraseña actualizada exitosamente"
    }
    ```
    """
    try:
        import hashlib

        # Validar longitud de nueva contraseña
        if len(request.password_nueva) < 6:
            raise HTTPException(
                status_code=400,
                detail="La contraseña debe tener al menos 6 caracteres"
            )

        # Buscar código válido y verificado
        codigos = list(db.collection("codigos_recuperacion")
            .where("email", "==", request.email)
            .where("codigo", "==", request.codigo)
            .where("usado", "==", False)
            .limit(1)
            .stream())

        if not codigos:
            raise HTTPException(status_code=400, detail="Código inválido o ya utilizado")

        codigo_doc = codigos[0]
        codigo_data = codigo_doc.to_dict()

        # Verificar expiración
        creado = codigo_data.get("creado")
        if creado:
            # Convertir a timestamp para comparar
            if hasattr(creado, 'timestamp'):
                creado_timestamp = creado.timestamp()
            else:
                creado_timestamp = creado.replace(tzinfo=None).timestamp() if hasattr(creado, 'replace') else 0

            diferencia = datetime.now().timestamp() - creado_timestamp
            if diferencia > 600:
                codigo_doc.reference.delete()
                raise HTTPException(status_code=400, detail="El código ha expirado")

        # Buscar usuario
        usuarios = list(db.collection("usuarios").where("email", "==", request.email).limit(1).stream())
        if not usuarios:
            raise HTTPException(status_code=404, detail="Usuario no encontrado")

        usuario_doc = usuarios[0]

        # Actualizar contraseña
        password_hash = hashlib.sha256(request.password_nueva.encode()).hexdigest()
        db.collection("usuarios").document(usuario_doc.id).update({
            "password": password_hash,
            "ultimoCambioPassword": datetime.now()
        })

        # Marcar código como usado y eliminar
        codigo_doc.reference.delete()

        return {
            "success": True,
            "message": "Contraseña actualizada exitosamente"
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al resetear contraseña: {str(e)}")


@app.get("/api/upload/status", tags=["Sistema"])
async def estado_servicios_upload():
    """
    ## 📊 Estado de Servicios de Subida

    Verifica qué servicios de subida de imágenes están disponibles.
    """
    return {
        "firebase_storage": True,  # Siempre disponible desde el cliente
        "google_drive": drive_service is not None,
        "drive_folder_configured": DRIVE_FOLDER_ID is not None
    }


@app.get("/panel-periodista", response_class=HTMLResponse, tags=["Sistema"])
async def panel_periodista():
    """
    ## 📝 Panel de Periodista

    Interfaz web para que los periodistas publiquen noticias con geolocalización.

    ### Requiere autenticación:
    Solo usuarios con rol 'reportero' o 'admin' pueden acceder.

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
        # Agregar headers para evitar caché del navegador
        return HTMLResponse(
            content=html_content,
            headers={
                "Cache-Control": "no-cache, no-store, must-revalidate",
                "Pragma": "no-cache",
                "Expires": "0"
            }
        )
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
        # Verificar caché primero
        cache_key = f"noticias_{limit}_{activa}_{destacada}"
        if cache_key in noticias_cache:
            return noticias_cache[cache_key]

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

        response = {
            "success": True,
            "count": len(noticias),
            "noticias": noticias
        }

        # Guardar en caché
        noticias_cache[cache_key] = response

        return response

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
        noticia_id = doc_ref[1].id

        # Limpiar caché para que se reflejen las nuevas noticias
        noticias_cache.clear()

        # Enviar notificación push automáticamente
        try:
            message = messaging.Message(
                notification=messaging.Notification(
                    title="📰 Nueva noticia en GeoNews",
                    body=noticia.titulo[:100] if noticia.titulo else "Nueva noticia disponible",
                ),
                data={
                    "type": "noticia_nueva",
                    "noticia_id": noticia_id,
                    "click_action": "OPEN_NOTICIA"
                },
                topic="all"
            )
            messaging.send(message)
            print(f"✅ Notificación enviada para noticia: {noticia_id}")
        except Exception as notif_error:
            print(f"⚠️ Error al enviar notificación: {notif_error}")

        return {
            "success": True,
            "message": "Noticia creada exitosamente",
            "id": noticia_id
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

@app.post("/api/email-digest", tags=["Notificaciones"])
async def enviar_email_digest(request: EmailDigestRequest):
    """
    ## 📧 Enviar Resumen por Email

    Envía un resumen de noticias recientes al email del usuario.

    ### Parámetros:
    * **email**: Email del usuario
    * **nombre**: Nombre del usuario
    * **frecuencia**: "daily" o "weekly"

    ### Funcionamiento:
    1. Obtiene las noticias más recientes según la frecuencia
    2. Genera un email HTML con el resumen
    3. Envía el email al usuario

    ### Nota:
    Requiere configuración SMTP en variables de entorno:
    - SMTP_HOST
    - SMTP_PORT
    - SMTP_USER
    - SMTP_PASSWORD
    """
    try:
        from email.mime.text import MIMEText
        from email.mime.multipart import MIMEMultipart
        import aiosmtplib

        # Obtener noticias recientes
        dias = 1 if request.frecuencia == "daily" else 7
        noticias_ref = db.collection("noticias").where("activa", "==", True).order_by("fechaPublicacion", direction=firestore.Query.DESCENDING).limit(10)
        noticias = []

        for doc in noticias_ref.stream():
            noticia_data = doc.to_dict()
            noticia_data['id'] = doc.id
            noticias.append(noticia_data)

        if not noticias:
            return {
                "success": True,
                "message": "No hay noticias nuevas para enviar"
            }

        # Generar HTML del email
        periodo = "hoy" if request.frecuencia == "daily" else "esta semana"
        html_content = f"""
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {{ font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }}
                .container {{ max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }}
                .header {{ background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px 20px; text-align: center; }}
                .header h1 {{ margin: 0; font-size: 28px; }}
                .header p {{ margin: 10px 0 0 0; opacity: 0.9; }}
                .content {{ padding: 30px 20px; }}
                .greeting {{ font-size: 18px; color: #333; margin-bottom: 20px; }}
                .noticia {{ background-color: #f9f9f9; border-left: 4px solid #667eea; padding: 15px; margin-bottom: 20px; border-radius: 5px; }}
                .noticia h3 {{ margin: 0 0 10px 0; color: #333; font-size: 18px; }}
                .noticia p {{ margin: 0; color: #666; line-height: 1.6; }}
                .noticia .meta {{ margin-top: 10px; font-size: 13px; color: #999; }}
                .footer {{ background-color: #f9f9f9; padding: 20px; text-align: center; color: #666; font-size: 14px; }}
                .footer a {{ color: #667eea; text-decoration: none; }}
                .btn {{ display: inline-block; background-color: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }}
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>📰 GeoNews - Resumen de Noticias</h1>
                    <p>Noticias locales de Ibarra</p>
                </div>
                <div class="content">
                    <div class="greeting">
                        ¡Hola {request.nombre}! 👋
                    </div>
                    <p>Aquí está tu resumen de noticias de {periodo}:</p>
        """

        # Agregar cada noticia
        for noticia in noticias[:5]:  # Máximo 5 noticias
            titulo = noticia.get('titulo', 'Sin título')
            descripcion = noticia.get('descripcion', 'Sin descripción')
            categoria = noticia.get('categoriaNombre', 'General')
            parroquia = noticia.get('parroquiaNombre', 'Ibarra')

            html_content += f"""
                    <div class="noticia">
                        <h3>{titulo}</h3>
                        <p>{descripcion[:200]}...</p>
                        <div class="meta">
                            📍 {parroquia} | 🏷️ {categoria}
                        </div>
                    </div>
            """

        html_content += """
                    <p style="text-align: center; margin-top: 30px;">
                        <a href="#" class="btn">Ver todas las noticias en la app</a>
                    </p>
                </div>
                <div class="footer">
                    <p>Has recibido este email porque activaste los resúmenes por email en GeoNews.</p>
                    <p><a href="#">Cancelar suscripción</a> | <a href="#">Cambiar frecuencia</a></p>
                    <p style="margin-top: 15px;">© 2026 GeoNews - Noticias Locales de Ibarra</p>
                </div>
            </div>
        </body>
        </html>
        """

        # Configurar SMTP (usar variables de entorno o valores por defecto para desarrollo)
        smtp_host = os.getenv("SMTP_HOST", "smtp.gmail.com")
        smtp_port = int(os.getenv("SMTP_PORT", "587"))
        smtp_user = os.getenv("SMTP_USER", "noreply.geonews@gmail.com")
        smtp_password = os.getenv("SMTP_PASSWORD", "")

        # Crear mensaje
        message = MIMEMultipart("alternative")
        message["Subject"] = f"📰 Tu resumen de noticias - GeoNews"
        message["From"] = f"GeoNews <{smtp_user}>"
        message["To"] = request.email

        # Agregar contenido HTML
        html_part = MIMEText(html_content, "html")
        message.attach(html_part)

        # Enviar email (solo si hay configuración SMTP)
        if smtp_password:
            await aiosmtplib.send(
                message,
                hostname=smtp_host,
                port=smtp_port,
                username=smtp_user,
                password=smtp_password,
                use_tls=True
            )

            return {
                "success": True,
                "message": f"Resumen enviado a {request.email}",
                "noticias_incluidas": len(noticias[:5])
            }
        else:
            # En desarrollo, solo retornar éxito sin enviar
            return {
                "success": True,
                "message": "Email digest preparado (SMTP no configurado en desarrollo)",
                "noticias_incluidas": len(noticias[:5]),
                "preview": True
            }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al enviar email: {str(e)}")

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
