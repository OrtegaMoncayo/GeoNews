# Script de Base de Datos Firebase - GeoNews

## Descripción
Este documento describe la estructura completa de la base de datos Firebase Firestore del sistema GeoNews después de la eliminación del módulo de Eventos.

---

## Estructura de Colecciones Firebase Firestore

### 1. Colección: `usuarios`

**Descripción**: Almacena información de usuarios registrados en la plataforma.

**Estructura del documento:**
```javascript
{
  // Identificación
  "id": "firebase_uid_123",  // UID de Firebase Auth

  // Datos personales
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan.perez@example.com",
  "fotoPerfil": "https://firebasestorage.googleapis.com/...",
  "bio": "Reportero local de Ibarra",
  "telefonocelular": "0987654321",
  "ubicacion": "Ibarra Centro",

  // Metadata temporal
  "fechaRegistro": 1704067200000,      // Timestamp en milisegundos
  "ultimaConexion": 1704153600000,     // Timestamp en milisegundos

  // Estadísticas
  "noticiasPublicadas": 15,
  "noticiasLeidas": 234,

  // Estado y permisos
  "verificado": true,
  "tipoUsuario": "reportero",  // Enum: "usuario", "reportero", "admin"

  // Metadata de creación
  "createdAt": "2024-01-01T10:00:00Z"
}
```

**Índices necesarios:**
- `email` (ascendente, único)
- `tipoUsuario` (ascendente)

---

### 2. Colección: `noticias`

**Descripción**: Almacena todas las noticias publicadas con geolocalización.

**Estructura del documento:**
```javascript
{
  // Identificación
  "id": "noticia_001",

  // Contenido
  "titulo": "Nueva ciclovía en Ibarra Centro",
  "descripcion": "Inauguración de 2km de ciclovía",
  "contenido": "La alcaldía de Ibarra inauguró hoy...",
  "imagenUrl": "https://firebasestorage.googleapis.com/...",

  // Contenido enriquecido
  "citaDestacada": "Esta obra beneficia a 5000 ciclistas diariamente",
  "hashtags": "#Ibarra #Ciclovía #MedioAmbiente",
  "impactoComunitario": "Reducción de 20% en emisiones de CO2",

  // Autoría
  "autorId": "firebase_uid_123",
  "autorNombre": "Juan Pérez",  // Desnormalizado para performance

  // Categorización
  "categoriaId": 8,
  "categoriaNombre": "Medio Ambiente",  // Desnormalizado

  // Geolocalización
  "ubicacion": new GeoPoint(0.3514, -78.1267),  // GeoPoint de Firebase
  "ubicacionTexto": "Ibarra Centro",
  "latitud": 0.3514,
  "longitud": -78.1267,

  // Metadata temporal
  "fechaCreacion": 1704153600000,
  "fechaActualizacion": 1704240000000,

  // Métricas
  "visualizaciones": 345,
  "destacada": true,

  // Estado
  "estado": "published",  // Enum: "draft", "published", "archived"

  // Timestamps
  "createdAt": "2024-01-02T14:00:00Z",
  "updatedAt": "2024-01-03T10:00:00Z"
}
```

**Índices compuestos necesarios:**
- `categoriaId` (ASC) + `fechaCreacion` (DESC)
- `destacada` (ASC) + `estado` (ASC) + `fechaCreacion` (DESC)
- `estado` (ASC) + `fechaCreacion` (DESC)
- **GeoQuery**: Índice en campo `ubicacion` (GeoPoint)

---

### 3. Colección: `categorias`

**Descripción**: Categorías de noticias disponibles.

**Estructura del documento:**
```javascript
{
  "id": 1,
  "nombre": "Política",
  "descripcion": "Noticias políticas locales y nacionales",
  "icono": "ic_politics",
  "color": "#FF6B35"  // Color hex para UI
}
```

**Categorías predefinidas:**
1. Política (#FF6B35)
2. Economía (#004E89)
3. Cultura (#9B59B6)
4. Deportes (#27AE60)
5. Educación (#F39C12)
6. Salud (#E74C3C)
7. Seguridad (#34495E)
8. Medio Ambiente (#16A085)
9. Turismo (#8E44AD)
10. Tecnología (#3498DB)

**Índices necesarios:**
- `nombre` (ascendente, único)

---

### 4. Colección: `parroquias`

**Descripción**: Parroquias de Ibarra con sus coordenadas geográficas.

**Estructura del documento:**
```javascript
{
  "id": 1,
  "nombre": "Ibarra Centro",
  "tipo": "urbana",
  "descripcion": "Centro histórico de Ibarra",
  "ubicacion": new GeoPoint(0.3514, -78.1267),
  "latitud": 0.3514,
  "longitud": -78.1267
}
```

**Parroquias urbanas de Ibarra:**
1. Ibarra Centro (Sagrario)
2. San Francisco
3. Caranqui
4. Alpachaca
5. La Dolorosa del Priorato

**Parroquias rurales de Ibarra:**
6. San Antonio
7. La Esperanza
8. Angochagua
9. Ambuquí
10. Salinas
11. La Carolina
12. Lita

**Índices necesarios:**
- `nombre` (ascendente, único)

---

### 5. Colección: `notificaciones`

**Descripción**: Notificaciones push enviadas a usuarios.

**Estructura del documento:**
```javascript
{
  "id": "notif_001",
  "usuarioId": "firebase_uid_123",
  "titulo": "Nueva noticia destacada",
  "mensaje": "Se publicó: Nueva ciclovía en Ibarra",
  "tipo": "noticia_destacada",  // Enum: "noticia_destacada", "noticia_cercana"
  "data": {
    "noticiaId": "noticia_001",
    "categoriaId": 8
  },
  "leida": false,
  "fechaEnvio": 1704153600000,
  "fechaLeida": null
}
```

**Índices necesarios:**
- `usuarioId` (ASC) + `fechaEnvio` (DESC)
- `usuarioId` (ASC) + `leida` (ASC)

---

## Colecciones Locales (SharedPreferences Encriptados)

### 1. Preferencias de Usuario

**Ubicación**: `EncryptedSharedPreferences` en dispositivo Android

**Claves almacenadas:**
```javascript
{
  // Sesión
  "user_id": "firebase_uid_123",
  "user_email": "juan@example.com",
  "user_nombre": "Juan",
  "user_apellido": "Pérez",
  "user_foto_perfil": "https://...",

  // Perfil
  "user_bio": "Reportero local",
  "user_ubicacion": "Ibarra Centro",
  "user_telefono": "0987654321",
  "fecha_registro": 1704067200000,

  // Notificaciones
  "notificaciones_activas": true,
  "notif_noticias": true,
  "notif_destacadas": true,

  // Preferencias
  "modo_oscuro": true,
  "mostrar_solo_cercanas": false,
  "intereses": "Política,Economía,Cultura,Deportes",  // CSV

  // Noticias guardadas
  "noticias_guardadas": "noticia_001,noticia_002,noticia_003"  // CSV
}
```

---

## Firebase Storage - Estructura de Archivos

### 1. Fotos de perfil de usuarios
```
/usuarios
  /fotos_perfil
    /profile_{userId}_{timestamp}.jpg
```

**Ejemplo:**
```
/usuarios/fotos_perfil/profile_firebase_uid_123_1704153600000.jpg
```

### 2. Imágenes de noticias
```
/noticias
  /imagenes
    /noticia_{noticiaId}_{timestamp}.jpg
```

**Ejemplo:**
```
/noticias/imagenes/noticia_001_1704153600000.jpg
```

---

## Reglas de Seguridad Firestore

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Usuarios: Lectura pública, escritura solo del propio usuario
    match /usuarios/{userId} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    // Noticias: Lectura pública, escritura solo para reporteros y admins
    match /noticias/{noticiaId} {
      allow read: if true;
      allow create: if request.auth != null &&
                      (get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.tipoUsuario == 'reportero' ||
                       get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.tipoUsuario == 'admin');
      allow update, delete: if request.auth != null &&
                            (resource.data.autorId == request.auth.uid ||
                             get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.tipoUsuario == 'admin');
    }

    // Categorías: Solo lectura
    match /categorias/{categoriaId} {
      allow read: if true;
      allow write: if false;
    }

    // Parroquias: Solo lectura
    match /parroquias/{parroquiaId} {
      allow read: if true;
      allow write: if false;
    }

    // Notificaciones: Solo el usuario destinatario
    match /notificaciones/{notifId} {
      allow read, write: if request.auth != null &&
                           resource.data.usuarioId == request.auth.uid;
    }
  }
}
```

---

## Reglas de Seguridad Storage

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {

    // Fotos de perfil: Solo el usuario puede subir su foto
    match /usuarios/fotos_perfil/profile_{userId}_{timestamp}.jpg {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    // Imágenes de noticias: Solo reporteros y admins
    match /noticias/imagenes/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null &&
                     (get(/databases/$(default)/documents/usuarios/$(request.auth.uid)).data.tipoUsuario == 'reportero' ||
                      get(/databases/$(default)/documents/usuarios/$(request.auth.uid)).data.tipoUsuario == 'admin');
    }
  }
}
```

---

## Scripts de Inicialización

### Script para crear categorías iniciales

```javascript
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();

const categorias = [
  { id: 1, nombre: 'Política', descripcion: 'Noticias políticas locales', icono: 'ic_politics', color: '#FF6B35' },
  { id: 2, nombre: 'Economía', descripcion: 'Economía y finanzas', icono: 'ic_economy', color: '#004E89' },
  { id: 3, nombre: 'Cultura', descripcion: 'Eventos culturales', icono: 'ic_culture', color: '#9B59B6' },
  { id: 4, nombre: 'Deportes', descripcion: 'Deportes locales', icono: 'ic_sports', color: '#27AE60' },
  { id: 5, nombre: 'Educación', descripcion: 'Educación y ciencia', icono: 'ic_education', color: '#F39C12' },
  { id: 6, nombre: 'Salud', descripcion: 'Salud y bienestar', icono: 'ic_health', color: '#E74C3C' },
  { id: 7, nombre: 'Seguridad', descripcion: 'Seguridad ciudadana', icono: 'ic_security', color: '#34495E' },
  { id: 8, nombre: 'Medio Ambiente', descripcion: 'Ecología y ambiente', icono: 'ic_environment', color: '#16A085' },
  { id: 9, nombre: 'Turismo', descripcion: 'Turismo local', icono: 'ic_tourism', color: '#8E44AD' },
  { id: 10, nombre: 'Tecnología', descripcion: 'Tecnología e innovación', icono: 'ic_tech', color: '#3498DB' }
];

async function crearCategorias() {
  for (const cat of categorias) {
    await db.collection('categorias').doc(cat.id.toString()).set(cat);
    console.log(`Categoría ${cat.nombre} creada`);
  }
}

crearCategorias();
```

### Script para crear parroquias de Ibarra

```javascript
const parroquias = [
  { id: 1, nombre: 'Ibarra Centro', tipo: 'urbana', latitud: 0.3514, longitud: -78.1267 },
  { id: 2, nombre: 'San Francisco', tipo: 'urbana', latitud: 0.3495, longitud: -78.1289 },
  { id: 3, nombre: 'Caranqui', tipo: 'urbana', latitud: 0.3612, longitud: -78.1198 },
  { id: 4, nombre: 'Alpachaca', tipo: 'urbana', latitud: 0.3423, longitud: -78.1312 },
  { id: 5, nombre: 'La Dolorosa', tipo: 'urbana', latitud: 0.3567, longitud: -78.1234 },
  { id: 6, nombre: 'San Antonio', tipo: 'rural', latitud: 0.3347, longitud: -78.2145 },
  { id: 7, nombre: 'La Esperanza', tipo: 'rural', latitud: 0.2891, longitud: -78.0923 },
  { id: 8, nombre: 'Angochagua', tipo: 'rural', latitud: 0.2456, longitud: -78.0712 }
];

async function crearParroquias() {
  for (const par of parroquias) {
    const docData = {
      ...par,
      ubicacion: new admin.firestore.GeoPoint(par.latitud, par.longitud)
    };
    await db.collection('parroquias').doc(par.id.toString()).set(docData);
    console.log(`Parroquia ${par.nombre} creada`);
  }
}

crearParroquias();
```

---

## Resumen de Cambios (Sin Eventos)

### Colecciones Eliminadas
- ❌ **eventos** (eliminada completamente)
- ❌ **asistencias_eventos** (eliminada completamente)

### Campos Modificados en `usuarios`
- ❌ Eliminado: `eventosAsistidos`

### Navegación de la App
- ✅ 3 secciones principales: **Noticias**, **Mapa**, **Perfil**
- ❌ Eliminada sección: "Eventos"

---

**Proyecto**: GeoNews - Noticias Locales de Ibarra
**Versión**: 0.1.0 (Sin módulo de Eventos)
**Base de Datos**: Firebase Firestore + Firebase Storage
**Fecha**: Enero 2026
