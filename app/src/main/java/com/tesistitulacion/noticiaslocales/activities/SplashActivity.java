package com.tesistitulacion.noticiaslocales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

/**
 * Splash Screen - Pantalla de inicio de la aplicación
 * Verifica si hay sesión activa y redirige a Login o Main
 */
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Esperar 2 segundos y luego verificar sesión
        new Handler().postDelayed(() -> {
            verificarSesion();
        }, SPLASH_DURATION);
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
