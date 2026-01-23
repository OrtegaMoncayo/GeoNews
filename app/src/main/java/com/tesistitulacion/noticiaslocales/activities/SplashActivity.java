package com.tesistitulacion.noticiaslocales.activities;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.utils.LocaleManager;
import com.tesistitulacion.noticiaslocales.utils.NotificationHelper;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;
import com.tesistitulacion.noticiaslocales.utils.ThemeManager;

/**
 * Splash Screen - Pantalla de inicio de la aplicación
 * Verifica si hay sesión activa y redirige a Login o Main
 *
 * IMPORTANTE: Aquí se crean los canales de notificación
 * Los canales DEBEN crearse antes de enviar cualquier notificación
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int SPLASH_DURATION = 2000; // 2 segundos
    private ProgressBar progressBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema guardado (por defecto: CLARO)
        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // IMPORTANTE: Crear canales de notificación al inicio de la app
        // Esto debe hacerse ANTES de mostrar cualquier notificación
        crearCanalesNotificacion();

        // Inicializar ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // Animar el ProgressBar de 0 a 100
        animarProgressBar();

        // Esperar 2 segundos y luego verificar sesión
        new Handler().postDelayed(() -> {
            verificarSesion();
        }, SPLASH_DURATION);
    }

    /**
     * Crea los canales de notificación necesarios para Android 8.0+
     * DEBE ejecutarse antes de enviar cualquier notificación
     */
    private void crearCanalesNotificacion() {
        try {
            NotificationHelper.getInstance(this).crearCanalesNotificacion();
            Log.d(TAG, "Canales de notificación creados correctamente");

            // Suscribir a topics de FCM para recibir notificaciones
            suscribirATopicsFCM();
        } catch (Exception e) {
            Log.e(TAG, "Error al crear canales de notificación", e);
        }
    }

    /**
     * Suscribe al dispositivo a los topics de FCM para recibir notificaciones
     */
    private void suscribirATopicsFCM() {
        // Suscribir al topic "all" para recibir todas las notificaciones
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Suscrito a topic 'all' para notificaciones");
                } else {
                    Log.e(TAG, "Error al suscribirse a topic 'all'", task.getException());
                }
            });

        // Suscribir al topic "noticias"
        FirebaseMessaging.getInstance().subscribeToTopic("noticias")
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Suscrito a topic 'noticias'");
                }
            });
    }

    private void animarProgressBar() {
        // Crear animación de 0 a 100
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(SPLASH_DURATION); // Misma duración que el splash
        animator.setInterpolator(new DecelerateInterpolator()); // Desacelera al final para efecto suave

        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            progressBar.setProgress(progress);
        });

        animator.start();
    }

    private void verificarSesion() {
        // Verificar si hay sesión activa
        String token = UsuarioPreferences.getToken(this);

        Intent intent;
        if (token != null && !token.isEmpty()) {
            // Hay sesión activa → ir a lista de noticias
            intent = new Intent(this, ListaNoticiasActivity.class);
        } else {
            // No hay sesión → ir a login
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // No permitir volver al splash con botón atrás
    }
}
