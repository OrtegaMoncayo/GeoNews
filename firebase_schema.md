# Esquema de Firestore para NoticiasIbarra

## 📊 Estructura de Colecciones

### 1. **usuarios**
```
usuarios/{userId}
  - id: string (UID de Firebase Auth)
  - nombre: string
  - email: string
  - telefono: string (opcional)
  - parroquiaId: reference → parroquias/{id}
  - rol: string ("usuario" | "moderador" | "admin")
  - fcmToken: string (para notificaciones push)
  - ubicacion: geopoint (latitud, longitud)
  - fechaRegistro: timestamp
  - activo: boolean
```

### 2. **parroquias**
```
parroquias/{parroquiaId}
  - id: string
  - nombre: string
  - tipo: string ("urbana" | "rural")
  - ubicacion: geopoint (latitud, longitud)
  - geohash: string (para búsquedas geográficas)
  - descripcion: string
  - poblacion: number
  - imagen: string (URL)
```

### 3. **categorias**
```
categorias/{categoriaId}
  - id: string
  - nombre: string
  - icono: string (nombre del recurso)
  - color: string (hex)
  - activa: boolean
```

### 4. **noticias**
```
noticias/{noticiaId}
  - id: string (auto-generado)
  - titulo: string
  - descripcion: string
  - contenido: string
  - imagenUrl: string
  - categoriaId: reference → categorias/{id}
  - parroquiaId: reference → parroquias/{id}
  - ubicacion: geopoint
  - geohash: string
  - ubicacionTexto: string
  - autorId: reference → usuarios/{id}
  - fechaPublicacion: timestamp
  - fechaCreacion: timestamp
  - visualizaciones: number
  - destacada: boolean
  - activa: boolean
  - tags: array<string>
```

### 5. **eventos**
```
eventos/{eventoId}
  - id: string
  - descripcion: string
  - fecha: timestamp
  - fechaFin: timestamp (opcional)
  - ubicacion: geopoint
  - geohash: string
  - ubicacionTexto: string
  - creadorId: reference → usuarios/{id}
  - parroquiaId: reference → parroquias/{id}
  - categoriaEvento: string ("cultural" | "deportivo" | "social" | "educativo" | "otro")
  - cupoMaximo: number (opcional)
  - cupoActual: number
  - costo: number
  - estado: string ("programado" | "en_curso" | "finalizado" | "cancelado")
  - imagenUrl: string (opcional)
  - fechaCreacion: timestamp
  - asistentes: array<reference> → usuarios/{id}
```

### 6. **comentarios** (subcollection de noticias)
```
noticias/{noticiaId}/comentarios/{comentarioId}
  - id: string
  - contenido: string
  - autorId: reference → usuarios/{id}
  - fechaCreacion: timestamp
  - likes: number
  - activo: boolean
```

### 7. **notificaciones**
```
notificaciones/{notificacionId}
  - id: string
  - tipo: string ("evento_nuevo" | "noticia_nueva" | "comentario" | "sistema")
  - titulo: string
  - mensaje: string
  - destinatarios: array<reference> → usuarios/{id} (o "todos")
  - parroquiaId: reference → parroquias/{id} (opcional, para filtrar por parroquia)
  - datos: map (metadata adicional)
  - fechaEnvio: timestamp
  - enviada: boolean
  - fcmMessageId: string (opcional)
```

## 🔍 Índices necesarios (Firestore Indexes)

Para búsquedas eficientes:

1. **noticias**:
   - `activa` + `fechaPublicacion` (DESC)
   - `categoriaId` + `activa` + `fechaPublicacion` (DESC)
   - `parroquiaId` + `activa` + `fechaPublicacion` (DESC)
   - `destacada` + `activa` + `fechaPublicacion` (DESC)

2. **eventos**:
   - `estado` + `fecha` (ASC)
   - `parroquiaId` + `estado` + `fecha` (ASC)
   - `categoriaEvento` + `estado` + `fecha` (ASC)

## 🌍 Búsquedas Geográficas

Para búsquedas por proximidad, usaremos **Geohashing**:

### Librería recomendada:
```gradle
implementation 'com.firebase:geofire-android:3.2.0'
```

### Ejemplo de consulta:
```java
// Buscar noticias en un radio de 5km
GeoFire geoFire = new GeoFire(FirebaseFirestore.getInstance().collection("noticias"));
GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitud, longitud), 5.0);
```

## 🔐 Reglas de Seguridad (básicas)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Usuarios: pueden leer todos, pero solo editar el suyo
    match /usuarios/{userId} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }

    // Parroquias: todos pueden leer, solo admins escribir
    match /parroquias/{parroquiaId} {
      allow read: if true;
      allow write: if request.auth != null &&
                     get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.rol == 'admin';
    }

    // Categorías: todos pueden leer, solo admins escribir
    match /categorias/{categoriaId} {
      allow read: if true;
      allow write: if request.auth != null &&
                     get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.rol == 'admin';
    }

    // Noticias: todos pueden leer, autenticados pueden crear
    match /noticias/{noticiaId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null &&
                               (resource.data.autorId == request.auth.uid ||
                                get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.rol in ['admin', 'moderador']);
    }

    // Eventos: todos pueden leer, autenticados pueden crear
    match /eventos/{eventoId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null &&
                               (resource.data.creadorId == request.auth.uid ||
                                get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.rol in ['admin', 'moderador']);
    }

    // Comentarios: todos pueden leer, autenticados pueden crear
    match /noticias/{noticiaId}/comentarios/{comentarioId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null &&
                               resource.data.autorId == request.auth.uid;
    }

    // Notificaciones: solo admins pueden escribir
    match /notificaciones/{notificacionId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
                     get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.rol == 'admin';
    }
  }
}
```

## 📝 Notas importantes

1. **Geohashing**: Para búsquedas geográficas eficientes, necesitas calcular y almacenar el geohash de cada ubicación.

2. **Referencias vs IDs**: Firestore permite referencias directas a otros documentos. Usa referencias para relaciones importantes (autor, parroquia).

3. **Índices compuestos**: Firebase te pedirá crear índices cuando hagas consultas complejas. Simplemente sigue el link que te da en el error.

4. **Paginación**: Usa `.limit(20).startAfter(lastDocument)` para paginación eficiente.

5. **Realtime**: Firestore permite escuchar cambios en tiempo real con `.addSnapshotListener()`.

## 🚀 Próximos pasos

1. ✅ Crear las colecciones en Firestore Console
2. ✅ Importar datos de prueba
3. ✅ Configurar índices
4. ✅ Actualizar código Android para usar Firestore
5. ✅ Implementar Firebase Cloud Messaging
