# 🔥 Firebase Setup Completo - NoticiasIbarra

## ✅ Lo que YA está configurado:

### 1. **Firebase Project**
- ✅ Proyecto creado: `noticiaslocalesibarra`
- ✅ Firestore Database habilitado
- ✅ Facturación configurada (plan Blaze con límites gratuitos)
- ✅ App Android registrada

### 2. **Datos Migrados a Firestore**
```
✅ 12 Parroquias
✅ 10 Categorías
✅ 3 Usuarios
✅ 7 Noticias
✅ 9 Eventos
```

### 3. **Archivos Android Configurados**
- ✅ `google-services.json` en `/app`
- ✅ `build.gradle` con dependencias Firebase
- ✅ `FirebaseManager.java` - Helper para CRUD
- ✅ `MyFirebaseMessagingService.java` - Notificaciones push
- ✅ `AndroidManifest.xml` actualizado
- ✅ `ListaNoticiasActivity.java` usando Firebase

---

## 📱 Cómo Usar Firebase en tu App

### **Obtener Noticias:**

```java
FirebaseManager.getInstance().getAllNoticias(new FirebaseManager.FirestoreCallback<List<Noticia>>() {
    @Override
    public void onSuccess(List<Noticia> noticias) {
        // Actualizar UI con las noticias
        adapter.actualizarLista(noticias);
    }

    @Override
    public void onError(Exception e) {
        // Manejar error
        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
```

### **Obtener Eventos Futuros:**

```java
FirebaseManager.getInstance().getEventosFuturos(new FirebaseManager.FirestoreCallback<List<Evento>>() {
    @Override
    public void onSuccess(List<Evento> eventos) {
        // Mostrar eventos
    }

    @Override
    public void onError(Exception e) {
        // Manejar error
    }
});
```

### **Obtener Parroquias:**

```java
FirebaseManager.getInstance().getAllParroquias(new FirebaseManager.FirestoreCallback<List<Parroquia>>() {
    @Override
    public void onSuccess(List<Parroquia> parroquias) {
        // Usar parroquias
    }

    @Override
    public void onError(Exception e) {
        // Manejar error
    }
});
```

### **Crear Nueva Noticia:**

```java
Noticia nuevaNoticia = new Noticia();
nuevaNoticia.setTitulo("Título de la noticia");
nuevaNoticia.setDescripcion("Descripción...");
nuevaNoticia.setLatitud(-0.35);
nuevaNoticia.setLongitud(-78.12);
nuevaNoticia.setFechaPublicacion(new Date());
nuevaNoticia.setActiva(true);

FirebaseManager.getInstance().createNoticia(nuevaNoticia, new FirebaseManager.FirestoreCallback<String>() {
    @Override
    public void onSuccess(String noticiaId) {
        Toast.makeText(context, "Noticia creada con ID: " + noticiaId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(context, "Error al crear: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
```

---

## 🔔 Firebase Cloud Messaging (Notificaciones Push)

### **Obtener el Token FCM:**

```java
FirebaseMessaging.getInstance().getToken()
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            String token = task.getResult();
            Log.d(TAG, "FCM Token: " + token);
            // Guardar el token en Firestore para enviar notificaciones
        }
    });
```

### **Enviar Notificación desde Firebase Console:**

1. Firebase Console → **Build** → **Cloud Messaging**
2. Click en **"Send your first message"**
3. **Notification title**: "Nuevo evento en Ibarra"
4. **Notification text**: "Se ha registrado un nuevo evento cultural"
5. **Target**: Tu app Android
6. Click en **"Send test message"** o **"Review"** → **"Publish"**

---

## 🚀 Próximos Pasos

### **1. Actualizar más Activities**

Actualiza estas Activities para usar Firebase:

- `ListaEventosActivity` → usar `FirebaseManager.getEventosFuturos()`
- `DetalleNoticiaActivity` → usar `FirebaseManager.getNoticiaById()`
- `PublicarNoticiaActivity` → usar `FirebaseManager.createNoticia()`
- `RegistrarEventoActivity` → usar `FirebaseManager.createEvento()`

### **2. Implementar Búsqueda Geográfica**

Para búsquedas por proximidad (noticias cerca de mí), necesitas:

```gradle
// Agregar a app/build.gradle
implementation 'com.firebase:geofire-android:3.2.0'
```

### **3. Implementar Firebase Authentication**

Para login con Google/Email:

```java
// Ya tienes la dependencia:
// implementation 'com.google.firebase:firebase-auth'

FirebaseAuth mAuth = FirebaseAuth.getInstance();

// Login con email
mAuth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            FirebaseUser user = mAuth.getCurrentUser();
            // Usuario logueado
        }
    });
```

### **4. Subir Imágenes a Firebase Storage**

```java
// Ya tienes la dependencia:
// implementation 'com.google.firebase:firebase-storage'

StorageReference storageRef = FirebaseStorage.getInstance().getReference();
StorageReference imagenRef = storageRef.child("noticias/" + noticiaId + ".jpg");

imagenRef.putFile(imageUri)
    .addOnSuccessListener(taskSnapshot -> {
        imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            // Guardar URL en Firestore
        });
    });
```

---

## 🔐 Reglas de Seguridad de Firestore

Actualmente están en **modo test** (cualquiera puede leer/escribir). Para producción, actualiza las reglas:

1. Firebase Console → **Firestore Database** → **Rules**
2. Reemplaza con:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Todos pueden leer noticias y eventos activos
    match /noticias/{noticiaId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null &&
                               resource.data.autorId == request.auth.uid;
    }

    match /eventos/{eventoId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null;
    }

    // Solo lectura para parroquias y categorías
    match /parroquias/{parroquiaId} {
      allow read: if true;
      allow write: if false; // Solo admins por consola
    }

    match /categorias/{categoriaId} {
      allow read: if true;
      allow write: if false;
    }
  }
}
```

3. Click en **"Publish"**

---

## 📊 Monitoreo y Analytics

### **Ver Analytics:**
Firebase Console → **Analytics** → **Dashboard**

### **Ver Logs:**
Firebase Console → **Firestore** → Click en cualquier documento

### **Ver Notificaciones Enviadas:**
Firebase Console → **Cloud Messaging** → **Reports**

---

## 🆘 Troubleshooting

### **Error: "FirebaseApp is not initialized"**
Asegúrate de que `google-services.json` esté en `/app` y sincroniza Gradle.

### **No se cargan datos:**
1. Verifica que Firestore tenga datos (Firebase Console)
2. Revisa Logcat con filtro: `FirebaseManager`
3. Verifica reglas de seguridad

### **Notificaciones no llegan:**
1. Verifica que el servicio esté en `AndroidManifest.xml`
2. Solicita permiso de notificaciones (Android 13+):
```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
}
```

---

## 💰 Costos y Límites

### **Plan Gratuito (Spark) incluido en Blaze:**

```
Firestore:
- 50,000 lecturas/día
- 20,000 escrituras/día
- 1 GB almacenamiento

Cloud Messaging:
- ILIMITADO y GRATIS

Storage:
- 5 GB almacenamiento
- 1 GB/día descarga
```

**Tu proyecto** con 1,000 usuarios activos al mes = **$0.00/mes** ✅

---

## 📞 Soporte

- **Documentación Firebase**: https://firebase.google.com/docs
- **Firestore Guides**: https://firebase.google.com/docs/firestore
- **FCM Guides**: https://firebase.google.com/docs/cloud-messaging

---

¡Tu app ya está lista para funcionar con Firebase! 🎉
