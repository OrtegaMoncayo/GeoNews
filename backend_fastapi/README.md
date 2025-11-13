# Backend FastAPI - NoticiasIbarra

API REST para la aplicación móvil NoticiasIbarra, integrada con Firebase Firestore y Cloud Messaging.

## Características

- FastAPI con documentación automática Swagger
- Firebase Firestore como base de datos
- Firebase Cloud Messaging para notificaciones push
- Listo para desplegar en Google Cloud Run
- CORS configurado para Android

## Requisitos

- Python 3.11+
- Cuenta de Firebase con proyecto configurado
- `serviceAccountKey.json` de Firebase Admin SDK

## Instalación Local

1. Instalar dependencias:
```bash
cd backend_fastapi
pip install -r requirements.txt
```

2. Colocar el archivo `serviceAccountKey.json` en este directorio

3. Ejecutar el servidor:
```bash
uvicorn main:app --reload --port 8080
```

4. Acceder a la documentación:
- Swagger UI: http://localhost:8080/docs
- ReDoc: http://localhost:8080/redoc

## Endpoints Disponibles

### Raíz y Health
- `GET /` - Info de la API
- `GET /health` - Health check con verificación de Firestore

### Noticias
- `GET /noticias` - Listar noticias (filtros: limit, activa, destacada)
- `GET /noticias/{id}` - Obtener noticia específica (incrementa visualizaciones)
- `POST /noticias` - Crear nueva noticia

### Eventos
- `GET /eventos` - Listar eventos (filtros: limit, futuros, estado)
- `GET /eventos/{id}` - Obtener evento específico
- `POST /eventos` - Crear nuevo evento
- `POST /eventos/{id}/inscribir` - Inscribir usuario a evento

### Notificaciones
- `POST /notificaciones/enviar` - Enviar notificación FCM genérica
- `POST /notificaciones/nueva-noticia` - Notificar nueva noticia

### Estadísticas
- `GET /stats` - Obtener estadísticas generales

## Deploy a Google Cloud Run

### Prerequisitos
1. Instalar [Google Cloud CLI](https://cloud.google.com/sdk/docs/install)
2. Autenticar:
```bash
gcloud auth login
gcloud config set project noticiaslocalesibarra
```

### Deployment

```bash
# Desde el directorio backend_fastapi
gcloud run deploy noticiasibarra-api \
  --source . \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --port 8080 \
  --max-instances 10 \
  --memory 512Mi
```

### Variables de Entorno (Opcional)

Si quieres usar variables de entorno en lugar de `serviceAccountKey.json`:

```bash
gcloud run deploy noticiasibarra-api \
  --source . \
  --set-env-vars FIREBASE_PROJECT_ID=noticiaslocalesibarra
```

## Arquitectura

```
Android App
    ↓
Cloud Run (FastAPI)
    ↓
Firebase Firestore
    ↓
Firebase Cloud Messaging
```

## Seguridad

### Firestore Security Rules

Actualmente en modo test. Para producción, usar:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Noticias: todos pueden leer, solo admins escribir
    match /noticias/{noticia} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.token.admin == true;
    }

    // Eventos: todos pueden leer, usuarios autenticados crear
    match /eventos/{evento} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null &&
        (request.auth.uid == resource.data.creadorId ||
         request.auth.token.admin == true);
    }
  }
}
```

## Estructura del Proyecto

```
backend_fastapi/
├── main.py                 # Aplicación FastAPI
├── requirements.txt        # Dependencias Python
├── Dockerfile             # Configuración Docker
├── .dockerignore          # Archivos excluidos de Docker
├── serviceAccountKey.json # Credenciales Firebase (NO subir a Git)
└── README.md              # Esta documentación
```

## Diferencias con backend_flask

El proyecto tiene un `backend_flask/` que usaba MySQL. Este nuevo backend FastAPI:
- Usa Firebase Firestore en lugar de MySQL
- Está alineado con la migración a Firebase del proyecto
- Incluye FCM para notificaciones push nativas
- Listo para Cloud Run (serverless)

## Pruebas con Android

Configurar en la app Android:

```java
// Para emulador (Cloud Run URL después del deploy)
public static final String BASE_URL = "https://noticiasibarra-api-xxxxx-uc.a.run.app/";

// Para pruebas locales desde emulador
public static final String BASE_URL = "http://10.0.2.2:8080/";

// Para dispositivo físico local (obtener IP con ipconfig)
public static final String BASE_URL = "http://192.168.1.XXX:8080/";
```

## Costos Estimados

Cloud Run Free Tier:
- 2 millones de peticiones/mes
- 360,000 GB-segundos de memoria
- 180,000 vCPU-segundos

Para una app local con < 1000 usuarios: **$0/mes**

## Logs y Monitoreo

Después del deploy, ver logs:
```bash
gcloud run logs read noticiasibarra-api --limit 50
```

Ver en Google Cloud Console:
https://console.cloud.google.com/run

## Soporte

Proyecto de tesis - NoticiasIbarra
Autor: [Tu nombre]
Institución: IST 17 de Julio
