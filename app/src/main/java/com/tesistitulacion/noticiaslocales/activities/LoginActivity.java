package com.tesistitulacion.noticiaslocales.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.utils.LocaleManager;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

/**
 * Pantalla de Login
 * Permite a los usuarios iniciar sesión en la aplicación
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegistro;
    private boolean procesando = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarVistas();
        configurarBotones();
    }

    private void inicializarVistas() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegistro = findViewById(R.id.tv_registro);
    }

    private void configurarBotones() {
        btnLogin.setOnClickListener(v -> intentarLogin());

        tvRegistro.setOnClickListener(v -> {
            // Ir a pantalla de registro
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void intentarLogin() {
        if (procesando) {
            return;
        }

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar campos
        if (email.isEmpty()) {
            etEmail.setError("Ingrese su email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingrese su contraseña");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        Log.d(TAG, "Intentando login con email: " + email);
        procesando = true;
        btnLogin.setEnabled(false);
        btnLogin.setText("Iniciando sesión...");

        // TODO: Implementar autenticación real con API
        // Por ahora, login simulado para que la app funcione
        realizarLoginSimulado(email, password);
    }

    /**
     * Login simulado temporal
     * TODO: Reemplazar con llamada real a la API
     */
    private void realizarLoginSimulado(String email, String password) {
        new Thread(() -> {
            try {
                // Simular delay de red
                Thread.sleep(1500);

                runOnUiThread(() -> {
                    // Guardar sesión simulada
                    UsuarioPreferences.guardarSesion(
                            this,
                            "token_simulado_" + System.currentTimeMillis(),
                            1,
                            email,
                            obtenerNombreDeEmail(email),
                            "usuario"
                    );

                    Log.i(TAG, "Login exitoso para: " + email);
                    showToast("¡Bienvenido!");

                    // Ir a pantalla principal (Lista de Noticias)
                    Intent intent = new Intent(this, ListaNoticiasActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });

            } catch (InterruptedException e) {
                // Re-interrumpir el hilo para preservar el estado de interrupción
                Thread.currentThread().interrupt();
                Log.e(TAG, "Login interrumpido", e);
                runOnUiThread(() -> {
                    procesando = false;
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Iniciar Sesión");
                    showToast("Login cancelado", Toast.LENGTH_SHORT);
                });
            } catch (Exception e) {
                Log.e(TAG, "Error en login: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    procesando = false;
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Iniciar Sesión");
                    showToast("Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG);
                });
            }
        }).start();
    }

    /**
     * Extrae un nombre simple del email
     */
    private String obtenerNombreDeEmail(String email) {
        if (email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return email;
    }

    /**
     * TODO: Método para login real con API
     */
    private void realizarLoginConAPI(String email, String password) {
        new Thread(() -> {
            try {
                // TODO: Implementar con UsuarioServiceHTTP
                // 1. Hacer POST a /api/auth/login/
                // 2. Enviar { "email": email, "password": password }
                // 3. Recibir { "token": "...", "usuario": {...} }
                // 4. Guardar con UsuarioPreferences
                // 5. Navegar a ListaNoticiasActivity

                /*
                String loginUrl = ApiConfig.BASE_URL + "auth/login/";

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", email);
                jsonBody.put("password", password);

                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    jsonBody.toString()
                );

                Request request = new Request.Builder()
                    .url(loginUrl)
                    .post(body)
                    .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    JSONObject json = new JSONObject(jsonResponse);

                    String token = json.getString("token");
                    JSONObject usuario = json.getJSONObject("usuario");

                    UsuarioPreferences.guardarSesion(
                        this,
                        token,
                        usuario.getInt("id"),
                        usuario.getString("email"),
                        usuario.getString("nombre"),
                        usuario.getString("rol")
                    );

                    runOnUiThread(() -> {
                        // Login exitoso
                        Intent intent = new Intent(this, ListaNoticiasActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    throw new Exception("Credenciales inválidas");
                }
                */

            } catch (Exception e) {
                Log.e(TAG, "Error en login: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    procesando = false;
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Iniciar Sesión");
                    showToast("Error: " + e.getMessage(), Toast.LENGTH_LONG);
                });
            }
        }).start();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Muestra un Toast de manera consistente
     */
    private void showToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra un Toast con duración personalizada
     */
    private void showToast(String mensaje, int duracion) {
        Toast.makeText(this, mensaje, duracion).show();
    }
}
