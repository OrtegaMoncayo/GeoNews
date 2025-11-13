package com.tesistitulacion.noticiaslocales.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Helper para obtener el token FCM del dispositivo
 * Útil para pruebas de notificaciones
 */
public class FCMTokenHelper {

    private static final String TAG = "FCMTokenHelper";

    /**
     * Obtiene el token FCM y lo muestra en un Toast y Logcat
     * Úsalo en cualquier Activity para obtener el token
     */
    public static void mostrarTokenFCM(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Error al obtener token FCM", task.getException());
                        Toast.makeText(context,
                            "Error al obtener token FCM",
                            Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Obtener token
                    String token = task.getResult();

                    // Mostrar en Logcat (búscalo con filtro "FCM_TOKEN")
                    Log.d("FCM_TOKEN", "═══════════════════════════════════════");
                    Log.d("FCM_TOKEN", "TOKEN FCM:");
                    Log.d("FCM_TOKEN", token);
                    Log.d("FCM_TOKEN", "═══════════════════════════════════════");

                    // Mostrar en Toast
                    Toast.makeText(context,
                        "Token FCM copiado en Logcat. Busca: FCM_TOKEN",
                        Toast.LENGTH_LONG).show();

                    // También guardarlo en SharedPreferences
                    context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("fcm_token", token)
                            .apply();
                });
    }

    /**
     * Obtiene el token almacenado en SharedPreferences
     */
    public static String getStoredToken(Context context) {
        return context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
                .getString("fcm_token", null);
    }
}
