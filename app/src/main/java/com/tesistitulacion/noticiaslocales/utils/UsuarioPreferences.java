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
    private static final String KEY_ROL = "rol";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

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
}
