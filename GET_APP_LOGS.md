# Cómo Obtener los Logs de la App

## Opción 1: Desde Android Studio (Recomendado)

1. **Abre Android Studio**
2. **Ve a la pestaña "Logcat"** (abajo de la ventana)
3. **Filtra por tu app:**
   - En el campo de búsqueda escribe: `package:com.tesistitulacion.noticiaslocales`
   - O filtra por tag: `FirebaseManager`
   - O filtra por nivel: Selecciona "Error" en el dropdown

4. **Reproduce el crash:**
   - Ejecuta la app
   - Intenta cargar las noticias
   - Cuando crashee, busca líneas rojas en el Logcat

5. **Busca estas líneas específicas:**
   ```
   FATAL EXCEPTION
   AndroidRuntime
   java.lang.RuntimeException
   Caused by:
   ```

6. **Copia TODO el stacktrace** (desde "FATAL EXCEPTION" hasta el final)

## Opción 2: Desde la terminal (si tienes adb)

Ejecuta este comando para ver solo los errores de tu app:

```bash
adb logcat -s AndroidRuntime:E FirebaseManager:* ListaNoticiasActivity:*
```

O para ver TODO el crash:

```bash
adb logcat -d | grep -A 50 "FATAL EXCEPTION"
```

## Opción 3: Captura de pantalla

Si no puedes copiar el texto:
- Toma screenshot del Logcat en Android Studio cuando ocurra el crash
- Asegúrate de que se vean las líneas del error

---

## Qué información necesito:

1. **El mensaje de error exacto** (línea que dice "Exception" o "Error")
2. **El stacktrace completo** (líneas que dicen "at com.tesistitulacion...")
3. **Logs de FirebaseManager** (antes del crash, para ver qué datos recibió)

Ejemplo de lo que busco:

```
E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.tesistitulacion.noticiaslocales, PID: 12345
    java.lang.NullPointerException: Attempt to invoke virtual method...
        at com.tesistitulacion.noticiaslocales.firebase.FirebaseManager.documentToNoticia(FirebaseManager.java:365)
        at com.tesistitulacion.noticiaslocales.activities.ListaNoticiasActivity$1.onSuccess(ListaNoticiasActivity.java:83)
```
