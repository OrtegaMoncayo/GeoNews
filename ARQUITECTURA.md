# 🏗️ ARQUITECTURA DEL SISTEMA - NOTICIAS LOCALES IBARRA

**Proyecto:** Sistema de Noticias Locales Ibarra
**Versión:** 0.1.0
**Fecha:** Noviembre 2025

---

## 📋 ÍNDICE

1. [Visión General](#visión-general)
2. [Arquitectura de Alto Nivel](#arquitectura-de-alto-nivel)
3. [Arquitectura Android](#arquitectura-android)
4. [Arquitectura Backend](#arquitectura-backend)
5. [Base de Datos](#base-de-datos)
6. [Integraciones Externas](#integraciones-externas)
7. [Flujos de Datos](#flujos-de-datos)
8. [Decisiones Arquitectónicas](#decisiones-arquitectónicas)

---

## 🎯 VISIÓN GENERAL

### Descripción del Sistema

NoticiasIbarra es una aplicación móvil Android que permite a los ciudadanos de Ibarra acceder a noticias locales y eventos comunitarios georreferenciados. El sistema utiliza una arquitectura híbrida que combina acceso directo a Firestore desde el cliente móvil con un backend REST API en Cloud Run para operaciones complejas.

### Objetivos Arquitectónicos

1. **Tiempo Real**: Sincronización instantánea de datos usando Firestore
2. **Escalabilidad**: Arquitectura serverless que escala automáticamente
3. **Offline-First**: Funcionamiento sin conexión mediante caché local
4. **Geolocalización**: Búsqueda de noticias/eventos por proximidad
5. **Bajo Costo**: Uso de servicios gratuitos o de bajo costo
6. **Mantenibilidad**: Código limpio, documentado y modular

### Stack Tecnológico

| Capa | Tecnología | Versión | Propósito |
|------|-----------|---------|-----------|
| **Frontend** | Android | API 24+ | Aplicación móvil |
| **Lenguaje** | Java | 11 | Lenguaje principal |
| **Build** | Gradle | 8.0 | Sistema de build |
| **Backend** | FastAPI | 0.115 | REST API |
| **Backend Runtime** | Cloud Run | - | Serverless hosting |
| **Base de Datos** | Firestore | - | NoSQL database |
| **Notificaciones** | FCM | - | Push notifications |
| **Mapas** | Google Maps | - | Visualización geográfica |
| **Autenticación** | Firebase Auth | - | Gestión de usuarios |
| **Storage** | Firebase Storage | - | Almacenamiento de imágenes |
| **Analytics** | Firebase Analytics | - | Métricas de uso |

---

## 🏛️ ARQUITECTURA DE ALTO NIVEL

```
┌─────────────────────────────────────────────────────────────────┐
│                        CAPA DE PRESENTACIÓN                      │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │           Android App (NoticiasIbarra)                      │ │
│  │                                                              │ │
│  │  Activities (11) → Adapters (2) → ViewHolders              │ │
│  │       ↓                                                      │ │
│  │  Material Design 3 + RecyclerView + Navigation              │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                            │
                            │ REST API (opcional)
                            │ + Firestore SDK
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│                      CAPA DE LÓGICA DE NEGOCIO                   │
│                                                                   │
│  ┌──────────────────────┐         ┌──────────────────────────┐ │
│  │   FirebaseManager    │         │  Backend FastAPI         │ │
│  │    (Singleton)       │         │  (Cloud Run)             │ │
│  │                      │         │                          │ │
│  │  • getAllNoticias()  │         │  GET /noticias           │ │
│  │  • createNoticia()   │         │  POST /noticias          │ │
│  │  • getEventos()      │         │  GET /eventos            │ │
│  │  • createEvento()    │         │  POST /eventos           │ │
│  │  • getParroquias()   │         │  POST /notificaciones    │ │
│  │  • getCategorias()   │         │  GET /stats              │ │
│  └──────────┬───────────┘         └────────────┬─────────────┘ │
│             │                                   │               │
│             └─────────────────┬─────────────────┘               │
└───────────────────────────────┼─────────────────────────────────┘
                                │
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│                        CAPA DE DATOS                             │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                    Firebase Platform                         ││
│  │                                                               ││
│  │  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐  ││
│  │  │  Firestore  │  │   Storage    │  │  Authentication  │  ││
│  │  │  Database   │  │  (Imágenes)  │  │  (Auth usuarios) │  ││
│  │  └─────────────┘  └──────────────┘  └──────────────────┘  ││
│  │                                                               ││
│  │  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐  ││
│  │  │    Cloud    │  │  Analytics   │  │   Crashlytics    │  ││
│  │  │  Messaging  │  │  (Métricas)  │  │    (Errores)     │  ││
│  │  └─────────────┘  └──────────────┘  └──────────────────┘  ││
│  └─────────────────────────────────────────────────────────────┘│
└───────────────────────────────────────────────────────────────────┘
                                │
                                ↓
┌─────────────────────────────────────────────────────────────────┐
│                     SERVICIOS EXTERNOS                           │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────────┐  │
│  │ Google Maps  │  │ Google Cloud │  │  Google Play        │  │
│  │     SDK      │  │    Build     │  │   Services          │  │
│  └──────────────┘  └──────────────┘  └─────────────────────┘  │
└───────────────────────────────────────────────────────────────────┘
```

---

## 📱 ARQUITECTURA ANDROID

### Estructura de Capas

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                   Activities                            │ │
│  │  • SplashActivity                                       │ │
│  │  • BaseActivity (navegación común)                     │ │
│  │  • LoginActivity / RegistroActivity                    │ │
│  │  • ListaNoticiasActivity / DetalleNoticiaActivity      │ │
│  │  • ListaEventosActivity / DetalleEventoActivity        │ │
│  │  • MapaActivity / PerfilActivity                       │ │
│  │  • RegistrarEventoActivity                             │ │
│  └────────────────────────────────────────────────────────┘ │
│                            ↓                                 │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                   Adapters                              │ │
│  │  • NoticiaAdapter (RecyclerView)                       │ │
│  │  • EventoAdapter (RecyclerView)                        │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                       │
│  ┌────────────────────────────────────────────────────────┐ │
│  │            FirebaseManager (Singleton)                  │ │
│  │  • Gestión de conexiones Firestore                     │ │
│  │  • CRUD de noticias, eventos, usuarios                 │ │
│  │  • Callbacks asíncronos                                │ │
│  │  • Conversión de DocumentSnapshot a POJOs              │ │
│  └────────────────────────────────────────────────────────┘ │
│                            ↓                                 │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                   Utils & Helpers                       │ │
│  │  • UbicacionUtils (GPS, Haversine)                     │ │
│  │  • UsuarioPreferences (EncryptedSharedPrefs)           │ │
│  │  • FCMTokenHelper (Push notifications)                 │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                   Models (POJOs)                        │ │
│  │  • Noticia (id, titulo, contenido, ubicacion, etc.)    │ │
│  │  • Evento (id, descripcion, fecha, cupos, etc.)        │ │
│  │  • Usuario (id, nombre, email, rol, fcmToken)          │ │
│  │  • Parroquia (id, nombre, tipo, coords)                │ │
│  │  • Categoria (id, nombre, icono, color)                │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Patrón de Diseño: MVC Simplificado

**Model:**
- POJOs con getters/setters
- Representan entidades de Firestore
- Incluyen `firestoreId` para navegación

**View:**
- Activities + XML Layouts
- RecyclerView con Adapters
- Material Design 3 components

**Controller:**
- Activities (lógica de UI)
- FirebaseManager (lógica de negocio)
- Callbacks para operaciones asíncronas

### Ciclo de Vida de una Activity

```
SplashActivity
     ↓
LoginActivity (si no autenticado)
     ↓
ListaNoticiasActivity (pantalla principal)
     ↓
DetalleNoticiaActivity (al hacer click)
     ↓
MapaActivity (botón "Ver en mapa")
```

### Navegación

```
DrawerLayout (menú lateral)
  ├── Inicio (Noticias)
  ├── Eventos
  ├── Mapa
  ├── Crear Evento
  ├── Mi Perfil
  └── Cerrar Sesión

BottomNavigationView (navegación inferior)
  ├── Noticias
  ├── Eventos
  └── Mapa
```

---

## 🔧 ARQUITECTURA BACKEND

### FastAPI + Firestore en Cloud Run

```
Cloud Run Container
│
├── FastAPI App (main.py)
│   ├── Middleware
│   │   └── CORS (permite Android)
│   │
│   ├── Endpoints REST
│   │   ├── GET  /                    → Info API
│   │   ├── GET  /health              → Health check
│   │   ├── GET  /noticias            → Listar noticias
│   │   ├── GET  /noticias/{id}       → Obtener noticia
│   │   ├── POST /noticias            → Crear noticia
│   │   ├── GET  /eventos             → Listar eventos
│   │   ├── GET  /eventos/{id}        → Obtener evento
│   │   ├── POST /eventos             → Crear evento
│   │   ├── POST /eventos/{id}/inscribir → Inscribir
│   │   ├── POST /notificaciones/enviar  → Enviar FCM
│   │   └── GET  /stats               → Estadísticas
│   │
│   ├── Pydantic Models (validación)
│   │   ├── Noticia
│   │   ├── Evento
│   │   └── NotificacionPush
│   │
│   └── Firebase Admin SDK
│       ├── Firestore Client
│       └── FCM Client
│
└── serviceAccountKey.json (credenciales)
```

### Dockerfile

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY main.py .
COPY serviceAccountKey.json .
EXPOSE 8080
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]
```

### Despliegue en Cloud Run

```
GitHub Repo
    ↓
gcloud run deploy
    ↓
Cloud Build (construye Docker image)
    ↓
Artifact Registry (almacena imagen)
    ↓
Cloud Run (despliega servicio)
    ↓
URL pública: https://noticiasibarra-api-*.run.app
```

### Características

- **Serverless**: Escala a 0 cuando no hay tráfico
- **Autoscaling**: De 0 a 10 instancias automáticamente
- **HTTPS**: Certificado SSL automático
- **Logging**: Cloud Logging integrado
- **Monitoreo**: Cloud Monitoring dashboard

---

## 💾 BASE DE DATOS

### Firestore Database Structure

```
firestore/
│
├── noticias/ (Collection)
│   └── {noticiaId}/ (Document)
│       ├── titulo: string
│       ├── descripcion: string
│       ├── contenido: string
│       ├── imagenUrl: string
│       ├── ubicacion: geopoint {lat, lng}
│       ├── ubicacionTexto: string
│       ├── fechaPublicacion: timestamp
│       ├── activa: boolean
│       ├── destacada: boolean
│       ├── visualizaciones: number
│       ├── tags: array<string>
│       ├── categoriaId: reference → categorias/{id}
│       ├── parroquiaId: reference → parroquias/{id}
│       └── autorId: reference → usuarios/{id}
│
├── eventos/ (Collection)
│   └── {eventoId}/ (Document)
│       ├── descripcion: string
│       ├── fecha: timestamp
│       ├── fechaFin: timestamp
│       ├── ubicacion: geopoint {lat, lng}
│       ├── ubicacionTexto: string
│       ├── estado: string (programado|en_curso|finalizado|cancelado)
│       ├── categoriaEvento: string (cultural|deportivo|educativo|comunitario)
│       ├── cupoMaximo: number
│       ├── cupoActual: number
│       ├── costo: number
│       ├── contactoTelefono: string
│       ├── contactoEmail: string
│       ├── creadorId: reference → usuarios/{id}
│       ├── parroquiaId: reference → parroquias/{id}
│       │
│       └── inscripciones/ (Subcollection)
│           └── {usuarioId}/ (Document)
│               ├── fechaInscripcion: timestamp
│               └── confirmado: boolean
│
├── usuarios/ (Collection)
│   └── {userId}/ (Document)
│       ├── nombre: string
│       ├── email: string
│       ├── telefono: string
│       ├── rol: string (usuario|admin)
│       ├── fcmToken: string
│       ├── parroquiaId: reference → parroquias/{id}
│       ├── fechaRegistro: timestamp
│       ├── activo: boolean
│       │
│       ├── favoritos/ (Subcollection)
│       │   └── {noticiaId}/ (Document)
│       │       └── fechaAgregado: timestamp
│       │
│       └── historial/ (Subcollection)
│           └── {noticiaId}/ (Document)
│               └── fechaVisto: timestamp
│
├── parroquias/ (Collection)
│   └── {parroquiaId}/ (Document)
│       ├── nombre: string
│       ├── tipo: string (urbana|rural)
│       ├── ubicacion: geopoint {lat, lng}
│       ├── descripcion: string
│       └── poblacion: number
│
└── categorias/ (Collection)
    └── {categoriaId}/ (Document)
        ├── nombre: string
        ├── icono: string
        ├── color: string
        └── activa: boolean
```

### Índices Firestore

```yaml
indexes:
  - collectionGroup: noticias
    queryScope: COLLECTION
    fields:
      - fieldPath: activa
        order: ASCENDING
      - fieldPath: fechaPublicacion
        order: DESCENDING

  - collectionGroup: eventos
    queryScope: COLLECTION
    fields:
      - fieldPath: estado
        order: ASCENDING
      - fieldPath: fecha
        order: ASCENDING
```

### Datos Actuales

| Colección | Documentos | Descripción |
|-----------|-----------|-------------|
| noticias | 7 | Noticias locales de Ibarra |
| eventos | 9 | Eventos comunitarios futuros |
| parroquias | 12 | 5 urbanas, 7 rurales |
| categorias | 10 | Categorías de noticias |
| usuarios | 3 | Usuarios de prueba |

---

## 🔌 INTEGRACIONES EXTERNAS

### Firebase Services

#### 1. Firestore Database
```java
FirebaseFirestore db = FirebaseFirestore.getInstance();
db.collection("noticias")
  .orderBy("fechaPublicacion", Query.Direction.DESCENDING)
  .limit(10)
  .get()
  .addOnSuccessListener(querySnapshot -> {
      // Procesar noticias
  });
```

#### 2. Firebase Cloud Messaging (FCM)
```java
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Manejar notificación
        String noticiaId = remoteMessage.getData().get("noticiaId");
        mostrarNotificacion(remoteMessage.getNotification());
    }
}
```

#### 3. Firebase Authentication
```java
FirebaseAuth mAuth = FirebaseAuth.getInstance();
mAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            FirebaseUser user = mAuth.getCurrentUser();
            // Usuario autenticado
        }
    });
```

#### 4. Firebase Storage
```java
StorageReference storageRef = FirebaseStorage.getInstance().getReference();
StorageReference imageRef = storageRef.child("eventos/" + eventoId + ".jpg");

imageRef.putFile(uri)
    .addOnSuccessListener(taskSnapshot -> {
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            // Guardar URL en Firestore
        });
    });
```

### Google Maps SDK

```java
GoogleMap mMap;

SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
    .findFragmentById(R.id.map);
mapFragment.getMapAsync(this);

@Override
public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    // Agregar marcador
    LatLng ibarra = new LatLng(0.3476, -78.1223);
    mMap.addMarker(new MarkerOptions()
        .position(ibarra)
        .title("Noticia en Ibarra"));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ibarra, 13));
}
```

### Backend API (FastAPI)

```kotlin
// Retrofit (si se usa)
interface NoticiasApi {
    @GET("noticias")
    suspend fun getNoticias(
        @Query("limit") limit: Int = 50,
        @Query("activa") activa: Boolean = true
    ): Response<NoticiasResponse>

    @POST("notificaciones/enviar")
    suspend fun enviarNotificacion(
        @Body notificacion: NotificacionPush
    ): Response<Unit>
}
```

---

## 🔄 FLUJOS DE DATOS

### Flujo 1: Ver Lista de Noticias

```
Usuario abre app
    ↓
ListaNoticiasActivity.onCreate()
    ↓
FirebaseManager.getAllNoticias(callback)
    ↓
Firestore Query: SELECT * FROM noticias ORDER BY fechaPublicacion DESC LIMIT 50
    ↓
Firestore retorna List<DocumentSnapshot>
    ↓
FirebaseManager convierte a List<Noticia>
    ↓
Callback.onSuccess(noticias)
    ↓
NoticiaAdapter.setData(noticias)
    ↓
RecyclerView muestra noticias
```

### Flujo 2: Ver Detalle de Noticia

```
Usuario hace click en noticia
    ↓
ListaNoticiasActivity detecta click
    ↓
Intent a DetalleNoticiaActivity con noticiaId
    ↓
DetalleNoticiaActivity.onCreate()
    ↓
FirebaseManager.getNoticiaById(noticiaId, callback)
    ↓
Firestore Query: SELECT * FROM noticias WHERE id = noticiaId
    ↓
Firestore incrementa visualizaciones
    ↓
Callback.onSuccess(noticia)
    ↓
DetalleNoticiaActivity muestra datos
    ↓
Carga imagen con Glide
    ↓
Usuario puede ver en mapa o compartir
```

### Flujo 3: Crear Nuevo Evento

```
Usuario abre RegistrarEventoActivity
    ↓
Usuario llena formulario
    ↓
Usuario selecciona imagen de galería
    ↓
ImagePicker retorna Uri de imagen
    ↓
Usuario hace click en "Guardar"
    ↓
Validar campos obligatorios
    ↓
Comprimir imagen (si es muy grande)
    ↓
Firebase Storage: subir imagen
    ↓
Storage retorna downloadUrl
    ↓
Crear objeto Evento con downloadUrl
    ↓
FirebaseManager.createEvento(evento, callback)
    ↓
Firestore: INSERT INTO eventos
    ↓
Backend API: POST /notificaciones/nueva-evento
    ↓
FCM envía notificación a usuarios suscritos
    ↓
Callback.onSuccess()
    ↓
Toast "Evento creado exitosamente"
    ↓
Regresar a ListaEventosActivity
```

### Flujo 4: Notificación Push

```
Backend: Se publica nueva noticia
    ↓
POST /notificaciones/nueva-noticia { noticiaId }
    ↓
Backend obtiene tokens FCM de Firestore
    ↓
Backend envía multicast message a FCM
    ↓
FCM distribuye a dispositivos
    ↓
MyFirebaseMessagingService.onMessageReceived()
    ↓
Extraer noticiaId del data payload
    ↓
Mostrar notificación en Android
    ↓
Usuario hace tap en notificación
    ↓
Intent a DetalleNoticiaActivity(noticiaId)
    ↓
Muestra detalle de noticia
```

### Flujo 5: Búsqueda Geográfica

```
Usuario abre MapaActivity
    ↓
Solicitar permisos de ubicación
    ↓
Obtener ubicación actual del usuario
    ↓
Usuario define radio de búsqueda (ej: 5 km)
    ↓
Backend API: GET /noticias/radio?lat=0.3476&lng=-78.1223&radio_km=5
    ↓
Backend ejecuta query Haversine en Firestore
    ↓
Backend retorna noticias ordenadas por distancia
    ↓
MapaActivity muestra marcadores en mapa
    ↓
Usuario hace click en marcador
    ↓
Mostrar info window con título de noticia
    ↓
Click en info window abre DetalleNoticiaActivity
```

---

## 🎯 DECISIONES ARQUITECTÓNICAS

### 1. ¿Por qué Firebase Firestore en lugar de MySQL?

**Decisión:** Firestore
**Razones:**
- Tiempo real: sincronización instantánea
- Offline-first: caché automático
- GeoPoint nativo para geolocalización
- Escalabilidad automática
- Menor costo operativo (sin servidor)
- SDK oficial para Android

**Trade-offs:**
- Queries menos flexibles que SQL
- Requiere índices para queries complejas
- Costos por operaciones (pero gratuito hasta 50K lecturas/día)

### 2. ¿Por qué Cloud Run en lugar de AWS EC2?

**Decisión:** Cloud Run
**Razones:**
- Serverless: escala a 0 cuando no hay tráfico
- Deployment más simple (gcloud run deploy)
- Mismo ecosistema que Firebase
- HTTPS automático
- Costo: $0 para tráfico bajo (vs $11.50/mes en EC2)

**Trade-offs:**
- Cold start latency (~1s)
- Máximo 15 minutos de ejecución
- Menos control sobre infraestructura

### 3. ¿Por qué acceso directo a Firestore desde Android?

**Decisión:** Acceso híbrido (directo + API)
**Razones:**
- Tiempo real sin polling
- Menor latencia para lecturas
- Offline support automático
- Menos carga en backend

**Cuándo usar cada uno:**
- **Directo:** Lecturas simples (noticias, eventos)
- **API:** Operaciones complejas (notificaciones, estadísticas, inscripciones)

### 4. ¿Por qué Java en lugar de Kotlin?

**Decisión:** Java
**Razones:**
- Familiaridad del desarrollador
- Más documentación legacy
- Compatible con Firebase BOM 32.7.0 sin Kotlin

**Nota:** Para proyectos nuevos, se recomienda Kotlin

### 5. ¿Por qué MVC simple en lugar de MVVM?

**Decisión:** MVC simplificado
**Razones:**
- Proyecto de tamaño pequeño-mediano
- Menor curva de aprendizaje
- Menos boilerplate
- Adecuado para tesis

**Para escalar:** Considerar migrar a MVVM + Repository pattern

---

## 📊 DIAGRAMAS UML

### Diagrama de Componentes

```
┌────────────────────────────────────────────────────────────┐
│                      Android App                            │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │  Activities  │──│   Adapters   │──│  FirebaseManager │ │
│  └──────────────┘  └──────────────┘  └────────┬─────────┘ │
│         │                 │                    │            │
│         ↓                 ↓                    ↓            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐ │
│  │    Models    │  │    Utils     │  │  FCMService      │ │
│  └──────────────┘  └──────────────┘  └──────────────────┘ │
└────────────────────────────────────────────────────────────┘
                            │
                            ↓
          ┌─────────────────────────────────────┐
          │         Firebase Platform           │
          │  ┌────────────┐  ┌──────────────┐  │
          │  │ Firestore  │  │     FCM      │  │
          │  └────────────┘  └──────────────┘  │
          │  ┌────────────┐  ┌──────────────┐  │
          │  │   Storage  │  │     Auth     │  │
          │  └────────────┘  └──────────────┘  │
          └─────────────────────────────────────┘
```

### Diagrama de Secuencia: Crear Evento

```
Usuario          Activity         FirebaseManager     Storage      Firestore      Backend
  │                 │                    │               │             │             │
  │─────────────────>│ onClick()          │               │             │             │
  │                 │──────────────────>│ uploadImage()  │             │             │
  │                 │                    │───────────────>│ putFile()  │             │
  │                 │                    │<───────────────│ downloadUrl│             │
  │                 │                    │──────────────────────────────>│ create()  │
  │                 │                    │<──────────────────────────────│ success   │
  │                 │<──────────────────│ onSuccess()    │             │             │
  │                 │────────────────────────────────────────────────────────────────>│ POST /notif
  │                 │<────────────────────────────────────────────────────────────────│ 200 OK
  │<─────────────────│ Toast "Creado"     │               │             │             │
  │                 │                    │               │             │             │
```

---

## 🔐 SEGURIDAD

### Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Noticias: lectura pública, escritura solo admin
    match /noticias/{noticia} {
      allow read: if true;
      allow create, update, delete: if isAdmin();
    }

    // Eventos: lectura pública, creación autenticados
    match /eventos/{evento} {
      allow read: if true;
      allow create: if isAuthenticated();
      allow update, delete: if isOwner(evento) || isAdmin();
    }

    // Usuarios: solo el propio usuario y admins
    match /usuarios/{userId} {
      allow read: if isAuthenticated();
      allow write: if isOwner(userId) || isAdmin();
    }

    // Funciones helper
    function isAuthenticated() {
      return request.auth != null;
    }

    function isAdmin() {
      return isAuthenticated() &&
             get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.rol == 'admin';
    }

    function isOwner(documentId) {
      return isAuthenticated() && request.auth.uid == documentId;
    }
  }
}
```

### Storage Security Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /eventos/{eventoId}/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }

    match /noticias/{noticiaId}/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

---

## 📈 ESCALABILIDAD

### Límites Actuales

| Recurso | Límite Actual | Límite Firebase | Notas |
|---------|---------------|-----------------|-------|
| Lecturas Firestore | ~1000/día | 50,000/día gratis | Muy por debajo |
| Escrituras Firestore | ~100/día | 20,000/día gratis | Muy por debajo |
| Storage | ~100 MB | 5 GB gratis | Muy por debajo |
| FCM mensajes | ~50/día | Sin límite | Gratis |
| Cloud Run requests | ~500/día | 2M/mes gratis | Muy por debajo |

### Plan de Escalabilidad

**Fase 1 (0-1000 usuarios):**
- Configuración actual suficiente
- Todo en free tier

**Fase 2 (1000-10,000 usuarios):**
- Implementar paginación en listas
- Agregar caché con Room Database
- Considerar plan Blaze de Firebase

**Fase 3 (10,000+ usuarios):**
- CDN para imágenes
- Múltiples regiones de Cloud Run
- Considerar GeoFire para búsquedas geo
- Implementar rate limiting

---

## 🛠️ MANTENIBILIDAD

### Logging

```java
// Android Logcat
Log.d(TAG, "Cargando noticias...");
Log.e(TAG, "Error al cargar noticias", exception);

// Firebase Crashlytics
FirebaseCrashlytics.getInstance().recordException(exception);
FirebaseCrashlytics.getInstance().log("Usuario ID: " + userId);

// Backend Cloud Logging (automático)
```

### Monitoreo

- **Firebase Analytics**: Eventos personalizados
- **Firebase Performance**: Tiempos de carga
- **Cloud Monitoring**: Métricas del backend
- **Cloud Logging**: Logs centralizados

### Testing Strategy

```
Unit Tests (70%)
  ├── FirebaseManager
  ├── UbicacionUtils
  └── Models

Integration Tests (20%)
  ├── Firebase + Activities
  └── Backend API

UI Tests (10%)
  ├── Login flow
  └── Create evento flow
```

---

## 📚 REFERENCIAS

- [Firebase Documentation](https://firebase.google.com/docs)
- [Cloud Run Documentation](https://cloud.google.com/run/docs)
- [FastAPI Documentation](https://fastapi.tiangolo.com/)
- [Android Developer Guide](https://developer.android.com/guide)
- [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk)

---

**Última actualización:** 11 de Noviembre de 2025
**Autor:** Richard Adrian Ortega Moncayo
**Institución:** IST 17 de Julio
