package com.tesistitulacion.noticiaslocales.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Gestión segura de preferencias del usuario usando EncryptedSharedPreferences.
 * Almacena de forma encriptada: token, usuario_id, email, nombre, rol.
 *
 * Cumple con ISO/IEC 25010 - Seguridad (Confidencialidad)
 */
public class UsuarioPreferences {
    private static final String TAG = "UsuarioPreferences";
    private static final String PREFS_NAME = "usuario_prefs_encrypted";

    // Claves para los valores almacenados
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USUARIO_ID = "usuario_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_APELLIDO = "apellido";
    private static final String KEY_ROL = "rol";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_BIO = "bio";
    private static final String KEY_TELEFONO = "telefono";
    private static final String KEY_UBICACION = "ubicacion";
    private static final String KEY_FOTO_PERFIL = "foto_perfil";
    private static final String KEY_FECHA_REGISTRO = "fecha_registro";
    private static final String KEY_NOTIFICACIONES_ACTIVAS = "notificaciones_activas";
    private static final String KEY_MODO_OSCURO = "modo_oscuro";
    private static final String KEY_MOSTRAR_SOLO_CERCANAS = "mostrar_solo_cercanas";
    private static final String KEY_TTS_ENABLED = "tts_enabled";
    private static final String KEY_TTS_SPEED = "tts_speed";
    private static final String KEY_FIREBASE_UID = "firebase_uid";

    private static SharedPreferences sharedPreferences;

    /**
     * Inicializa EncryptedSharedPreferences con MasterKey
     * Se debe llamar antes de usar los métodos get/set
     */
    private static void initializePreferences(Context context) {
        if (sharedPreferences != null) {
            return; // Ya está inicializado
        }

        try {
            // Crear MasterKey para encriptación
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // Crear EncryptedSharedPreferences
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            Log.i(TAG, "EncryptedSharedPreferences inicializado correctamente");

        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error al inicializar EncryptedSharedPreferences", e);
            // Fallback a SharedPreferences normal (solo en caso de error crítico)
            Log.w(TAG, "Usando SharedPreferences normal como fallback");
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * Guarda la sesión completa del usuario
     */
    public static void guardarSesion(Context context, String token, int usuarioId,
                                      String email, String nombre, String rol) {
        initializePreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_USUARIO_ID, usuarioId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NOMBRE, nombre);
        editor.putString(KEY_ROL, rol);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();

        Log.i(TAG, "Sesión guardada de forma encriptada para usuario: " + email);
    }

    /**
     * Obtiene el token de autenticación encriptado
     */
    public static String getToken(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * Obtiene el ID del usuario
     */
    public static int getUsuarioId(Context context) {
        initializePreferences(context);
        return sharedPreferences.getInt(KEY_USUARIO_ID, -1);
    }

    /**
     * Obtiene el email del usuario
     */
    public static String getEmail(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    /**
     * Obtiene el nombre del usuario
     */
    public static String getNombre(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_NOMBRE, null);
    }

    /**
     * Obtiene el rol del usuario
     */
    public static String getRol(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_ROL, "usuario");
    }

    /**
     * Verifica si hay una sesión activa
     */
    public static boolean isLoggedIn(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Cierra la sesión eliminando todos los datos encriptados
     */
    public static void cerrarSesion(Context context) {
        initializePreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Log.i(TAG, "Sesión cerrada - datos encriptados eliminados");
    }

    /**
     * Actualiza solo el token (útil si se renueva)
     */
    public static void actualizarToken(Context context, String nuevoToken) {
        initializePreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, nuevoToken);
        editor.apply();

        Log.i(TAG, "Token actualizado de forma encriptada");
    }

    // ==================== MÉTODOS ADICIONALES PARA PERFIL ====================

    /**
     * Obtiene el apellido del usuario
     */
    public static String getApellido(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_APELLIDO, null);
    }

    /**
     * Guarda el apellido del usuario
     */
    public static void guardarApellido(Context context, String apellido) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_APELLIDO, apellido);
        editor.apply();
    }

    /**
     * Guarda el nombre del usuario
     */
    public static void guardarNombre(Context context, String nombre) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NOMBRE, nombre);
        editor.apply();
    }

    /**
     * Obtiene la biografía del usuario
     */
    public static String getBio(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_BIO, null);
    }

    /**
     * Guarda la biografía del usuario
     */
    public static void guardarBio(Context context, String bio) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BIO, bio);
        editor.apply();
    }

    /**
     * Obtiene el teléfono del usuario
     */
    public static String getTelefono(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_TELEFONO, null);
    }

    /**
     * Guarda el teléfono del usuario
     */
    public static void guardarTelefono(Context context, String telefono) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TELEFONO, telefono);
        editor.apply();
    }

    /**
     * Obtiene la ubicación del usuario
     */
    public static String getUbicacion(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_UBICACION, null);
    }

    /**
     * Guarda la ubicación del usuario
     */
    public static void guardarUbicacion(Context context, String ubicacion) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_UBICACION, ubicacion);
        editor.apply();
    }

    /**
     * Obtiene la URL de la foto de perfil del usuario
     */
    public static String getFotoPerfil(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_FOTO_PERFIL, null);
    }

    /**
     * Guarda la URL de la foto de perfil del usuario
     */
    public static void guardarFotoPerfil(Context context, String fotoUrl) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FOTO_PERFIL, fotoUrl);
        editor.apply();
    }

    /**
     * Obtiene la fecha de registro del usuario (timestamp)
     */
    public static Long getFechaRegistro(Context context) {
        initializePreferences(context);
        long timestamp = sharedPreferences.getLong(KEY_FECHA_REGISTRO, 0);
        return timestamp > 0 ? timestamp : null;
    }

    /**
     * Guarda la fecha de registro del usuario
     */
    public static void guardarFechaRegistro(Context context, long timestamp) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_FECHA_REGISTRO, timestamp);
        editor.apply();
    }

    /**
     * Verifica si las notificaciones están activas
     */
    public static boolean getNotificacionesActivas(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_NOTIFICACIONES_ACTIVAS, true); // Por defecto activadas
    }

    /**
     * Guarda el estado de las notificaciones
     */
    public static void guardarNotificacionesActivas(Context context, boolean activas) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIFICACIONES_ACTIVAS, activas);
        editor.apply();
    }

    // ==================== PREFERENCIAS DE TIPOS DE NOTIFICACIONES ====================

    private static final String KEY_NOTIF_NOTICIAS = "notif_noticias";
    private static final String KEY_NOTIF_DESTACADAS = "notif_destacadas";

    /**
     * Verifica si las notificaciones de noticias están activas
     */
    public static boolean getNotificacionesNoticias(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_NOTIF_NOTICIAS, true);
    }

    /**
     * Guarda el estado de las notificaciones de noticias
     */
    public static void guardarNotificacionesNoticias(Context context, boolean activas) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIF_NOTICIAS, activas);
        editor.apply();
        Log.i(TAG, "Notificaciones de noticias: " + activas);
    }

    /**
     * Verifica si las notificaciones destacadas están activas
     */
    public static boolean getNotificacionesDestacadas(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_NOTIF_DESTACADAS, true);
    }

    /**
     * Guarda el estado de las notificaciones destacadas
     */
    public static void guardarNotificacionesDestacadas(Context context, boolean activas) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NOTIF_DESTACADAS, activas);
        editor.apply();
        Log.i(TAG, "Notificaciones destacadas: " + activas);
    }

    /**
     * Obtiene el ID del usuario como String (para Firebase)
     */
    public static String getUserId(Context context) {
        initializePreferences(context);
        // Primero intentar obtener el UID de Firebase (String)
        String firebaseUid = sharedPreferences.getString(KEY_FIREBASE_UID, null);
        if (firebaseUid != null) {
            return firebaseUid;
        }
        // Fallback al ID numérico
        int userId = sharedPreferences.getInt(KEY_USUARIO_ID, -1);
        return userId > 0 ? String.valueOf(userId) : null;
    }

    /**
     * Guarda el UID de Firebase (String)
     */
    public static void guardarUserId(Context context, String userId) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FIREBASE_UID, userId);
        editor.apply();
        Log.i(TAG, "Firebase UID guardado");
    }

    /**
     * Verifica si el modo oscuro está activado
     */
    public static boolean getModoOscuro(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_MODO_OSCURO, false); // Por defecto desactivado
    }

    /**
     * Guarda el estado del modo oscuro
     */
    public static void guardarModoOscuro(Context context, boolean activado) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_MODO_OSCURO, activado);
        editor.apply();
    }

    /**
     * Verifica si se deben mostrar solo noticias cercanas en el mapa (modo Pokémon GO)
     */
    public static boolean getMostrarSoloCercanas(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_MOSTRAR_SOLO_CERCANAS, false); // Por defecto muestra todas
    }

    /**
     * Guarda el estado de mostrar solo noticias cercanas
     */
    public static void guardarMostrarSoloCercanas(Context context, boolean soloCercanas) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_MOSTRAR_SOLO_CERCANAS, soloCercanas);
        editor.apply();
        Log.i(TAG, "Configuración actualizada - Mostrar solo cercanas: " + soloCercanas);
    }

    // ==================== MÉTODOS PARA EDITAR PERFIL ====================

    private static final String KEY_UBICACION_PUBLICA = "ubicacion_publica";

    /**
     * Verifica si la ubicación es pública
     */
    public static boolean getUbicacionPublica(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_UBICACION_PUBLICA, true); // Por defecto pública
    }

    /**
     * Guarda el estado de ubicación pública
     */
    public static void guardarUbicacionPublica(Context context, boolean publica) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_UBICACION_PUBLICA, publica);
        editor.apply();
    }

    // ==================== MÉTODOS PARA ARTÍCULOS GUARDADOS ====================

    private static final String KEY_NOTICIAS_GUARDADAS = "noticias_guardadas";

    /**
     * Obtiene la lista de IDs de noticias guardadas
     */
    public static java.util.List<String> getNoticiasGuardadas(Context context) {
        initializePreferences(context);
        String noticiasJson = sharedPreferences.getString(KEY_NOTICIAS_GUARDADAS, "");

        java.util.List<String> lista = new java.util.ArrayList<>();
        if (noticiasJson != null && !noticiasJson.isEmpty()) {
            String[] ids = noticiasJson.split(",");
            for (String id : ids) {
                if (!id.trim().isEmpty()) {
                    lista.add(id.trim());
                }
            }
        }
        return lista;
    }

    /**
     * Guarda una noticia en favoritos
     */
    public static void guardarNoticia(Context context, String noticiaId) {
        initializePreferences(context);
        java.util.List<String> guardadas = getNoticiasGuardadas(context);

        if (!guardadas.contains(noticiaId)) {
            guardadas.add(noticiaId);
            String noticiasJson = android.text.TextUtils.join(",", guardadas);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NOTICIAS_GUARDADAS, noticiasJson);
            editor.apply();

            Log.i(TAG, "Noticia guardada: " + noticiaId);
        }
    }

    /**
     * Elimina una noticia de favoritos
     */
    public static void eliminarNoticiaGuardada(Context context, String noticiaId) {
        initializePreferences(context);
        java.util.List<String> guardadas = getNoticiasGuardadas(context);

        if (guardadas.remove(noticiaId)) {
            String noticiasJson = android.text.TextUtils.join(",", guardadas);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NOTICIAS_GUARDADAS, noticiasJson);
            editor.apply();

            Log.i(TAG, "Noticia eliminada de guardados: " + noticiaId);
        }
    }

    /**
     * Verifica si una noticia está guardada
     */
    public static boolean isNoticiaGuardada(Context context, String noticiaId) {
        java.util.List<String> guardadas = getNoticiasGuardadas(context);
        return guardadas.contains(noticiaId);
    }

    // ==================== INTERESES DEL USUARIO ====================

    private static final String KEY_INTERESES = "intereses";

    /**
     * Obtiene la lista de intereses del usuario
     */
    public static java.util.List<String> getIntereses(Context context) {
        initializePreferences(context);
        String interesesJson = sharedPreferences.getString(KEY_INTERESES, "");

        java.util.List<String> lista = new java.util.ArrayList<>();
        if (interesesJson != null && !interesesJson.isEmpty()) {
            String[] intereses = interesesJson.split(",");
            for (String interes : intereses) {
                if (!interes.trim().isEmpty()) {
                    lista.add(interes.trim());
                }
            }
        }
        return lista;
    }

    /**
     * Guarda la lista completa de intereses del usuario
     */
    public static void guardarIntereses(Context context, java.util.List<String> intereses) {
        initializePreferences(context);
        String interesesJson = android.text.TextUtils.join(",", intereses);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_INTERESES, interesesJson);
        editor.apply();

        Log.i(TAG, "Intereses guardados: " + intereses.size());
    }

    /**
     * Agrega un interés a la lista
     */
    public static void agregarInteres(Context context, String interes) {
        java.util.List<String> intereses = getIntereses(context);
        if (!intereses.contains(interes)) {
            intereses.add(interes);
            guardarIntereses(context, intereses);
            Log.i(TAG, "Interés agregado: " + interes);
        }
    }

    /**
     * Elimina un interés de la lista
     */
    public static void eliminarInteres(Context context, String interes) {
        java.util.List<String> intereses = getIntereses(context);
        if (intereses.remove(interes)) {
            guardarIntereses(context, intereses);
            Log.i(TAG, "Interés eliminado: " + interes);
        }
    }

    /**
     * Verifica si un interés está en la lista
     */
    public static boolean tieneInteres(Context context, String interes) {
        return getIntereses(context).contains(interes);
    }

    // ==================== FCM TOKEN ====================

    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_FCM_TOKEN_TIMESTAMP = "fcm_token_timestamp";

    /**
     * Guarda el token FCM
     */
    public static void guardarFCMToken(Context context, String token) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FCM_TOKEN, token);
        editor.putLong(KEY_FCM_TOKEN_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
        Log.i(TAG, "Token FCM guardado");
    }

    /**
     * Obtiene el token FCM guardado
     */
    public static String getFCMToken(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_FCM_TOKEN, null);
    }

    /**
     * Obtiene la fecha de actualización del token FCM
     */
    public static long getFCMTokenTimestamp(Context context) {
        initializePreferences(context);
        return sharedPreferences.getLong(KEY_FCM_TOKEN_TIMESTAMP, 0);
    }

    /**
     * Verifica si el token FCM está guardado
     */
    public static boolean hasFCMToken(Context context) {
        return getFCMToken(context) != null;
    }

    // ==================== EMAIL DIGEST ====================

    private static final String KEY_EMAIL_DIGEST_ENABLED = "email_digest_enabled";
    private static final String KEY_EMAIL_DIGEST_FREQUENCY = "email_digest_frequency"; // "daily" o "weekly"
    private static final String KEY_EMAIL_DIGEST_LAST_SENT = "email_digest_last_sent";
    private static final String KEY_EMAIL_DIGEST_TIME = "email_digest_time"; // Hora del día (0-23)

    /**
     * Guarda si el resumen por email está activado
     */
    public static void guardarEmailDigestActivado(Context context, boolean activado) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_EMAIL_DIGEST_ENABLED, activado);
        editor.apply();
        Log.i(TAG, "Email digest " + (activado ? "activado" : "desactivado"));
    }

    /**
     * Obtiene si el resumen por email está activado
     */
    public static boolean getEmailDigestActivado(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_EMAIL_DIGEST_ENABLED, false);
    }

    /**
     * Guarda la frecuencia del resumen por email
     * @param frecuencia "daily" o "weekly"
     */
    public static void guardarEmailDigestFrecuencia(Context context, String frecuencia) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL_DIGEST_FREQUENCY, frecuencia);
        editor.apply();
        Log.i(TAG, "Frecuencia de email digest: " + frecuencia);
    }

    /**
     * Obtiene la frecuencia del resumen por email
     * @return "daily" o "weekly" (por defecto "daily")
     */
    public static String getEmailDigestFrecuencia(Context context) {
        initializePreferences(context);
        return sharedPreferences.getString(KEY_EMAIL_DIGEST_FREQUENCY, "daily");
    }

    /**
     * Guarda la hora del día para enviar el resumen (0-23)
     */
    public static void guardarEmailDigestTime(Context context, int hora) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_EMAIL_DIGEST_TIME, hora);
        editor.apply();
        Log.i(TAG, "Hora de email digest: " + hora + ":00");
    }

    /**
     * Obtiene la hora del día para enviar el resumen (por defecto 8:00 AM)
     */
    public static int getEmailDigestTime(Context context) {
        initializePreferences(context);
        return sharedPreferences.getInt(KEY_EMAIL_DIGEST_TIME, 8); // 8 AM por defecto
    }

    /**
     * Guarda el timestamp del último envío de email digest
     */
    public static void guardarEmailDigestLastSent(Context context, long timestamp) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_EMAIL_DIGEST_LAST_SENT, timestamp);
        editor.apply();
    }

    /**
     * Obtiene el timestamp del último envío de email digest
     */
    public static long getEmailDigestLastSent(Context context) {
        initializePreferences(context);
        return sharedPreferences.getLong(KEY_EMAIL_DIGEST_LAST_SENT, 0);
    }

    // ==================== TEXT-TO-SPEECH ====================

    /**
     * Verifica si TTS está activado
     */
    public static boolean getTTSEnabled(Context context) {
        initializePreferences(context);
        return sharedPreferences.getBoolean(KEY_TTS_ENABLED, true); // Por defecto activado
    }

    /**
     * Guarda el estado de TTS
     */
    public static void guardarTTSEnabled(Context context, boolean enabled) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_TTS_ENABLED, enabled);
        editor.apply();
        Log.i(TAG, "TTS " + (enabled ? "activado" : "desactivado"));
    }

    /**
     * Obtiene la velocidad de lectura TTS (0.5 - 2.0)
     */
    public static float getTTSSpeed(Context context) {
        initializePreferences(context);
        return sharedPreferences.getFloat(KEY_TTS_SPEED, 1.0f); // Velocidad normal por defecto
    }

    /**
     * Guarda la velocidad de lectura TTS
     */
    public static void guardarTTSSpeed(Context context, float speed) {
        initializePreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_TTS_SPEED, speed);
        editor.apply();
        Log.i(TAG, "Velocidad TTS: " + speed);
    }
}

