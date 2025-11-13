# 🔍 Diagnóstico: Firebase no muestra datos

## Paso 1: Verifica los Logs en Android Studio

En **Logcat**, filtra por `FirebaseManager` y busca estos mensajes:

### ✅ Si ves esto (BIEN):
```
D/FirebaseManager: Iniciando consulta de noticias...
D/FirebaseManager: Consulta exitosa. Documentos recibidos: 7
D/FirebaseManager: Procesando documento: [ID]
D/FirebaseManager: Título: [Título de noticia]
D/FirebaseManager: Noticia agregada: [Título]
D/FirebaseManager: Total noticias procesadas: 7
```

### ❌ Si ves esto (MAL):
```
E/FirebaseManager: Error al obtener noticias
```

---

## Paso 2: Verifica las Reglas de Firestore

1. Ve a **Firebase Console**: https://console.firebase.google.com
2. Selecciona tu proyecto: **noticiaslocalesibarra**
3. Ve a **Firestore Database** → **Rules**

### Reglas Actuales (deben ser así):

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;  // Modo test - cualquiera puede leer
    }
  }
}
```

Si las reglas son diferentes o dicen algo como:
```
allow read, write: if false;
```

Cámbialas a las de arriba y haz clic en **Publish**.

---

## Paso 3: Verifica que hay datos en Firestore

1. En Firebase Console → **Firestore Database**
2. Ve a la colección `noticias`
3. Deberías ver **7 documentos**
4. Abre uno y verifica que tenga campos como:
   - titulo
   - descripcion
   - fechaPublicacion
   - activa (true/false)

---

## Paso 4: Verifica google-services.json

En Android Studio:
1. Ve a la carpeta `app/`
2. Verifica que exista `google-services.json`
3. Ábrelo y verifica que el `project_id` sea: `"noticiaslocalesibarra"`

---

## Paso 5: Verifica conexión a Internet

En el AndroidManifest.xml debe estar el permiso:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Paso 6: Prueba con emulador o dispositivo real

- ¿Estás usando emulador o dispositivo físico?
- El dispositivo ¿tiene conexión a Internet activa?
- Intenta abrir un navegador en el dispositivo para confirmar

---

## 🚨 Errores Comunes:

### Error: "PERMISSION_DENIED"
**Solución**: Cambia las reglas de Firestore (Paso 2)

### Error: "Failed to get documents"
**Solución**: Verifica conexión a Internet

### Error: "Documentos recibidos: 0"
**Solución**: Verifica que hay datos en Firestore (Paso 3)

### Error: No aparece nada en Logcat
**Solución**: La app no está llamando a `cargarNoticias()`, revisa `ListaNoticiasActivity`

---

## 📝 Información que necesito:

Por favor copia y pega:

1. **Todos los logs de Logcat** filtrados por `FirebaseManager`
2. **Las reglas actuales** de Firestore (desde Firebase Console)
3. **¿Cuántos documentos** hay en la colección `noticias`?
4. **¿Emulador o dispositivo físico?**
5. **¿Tiene Internet el dispositivo?**
