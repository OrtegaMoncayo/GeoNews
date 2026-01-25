package com.tesistitulacion.noticiaslocales.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.utils.LocaleManager;
import com.tesistitulacion.noticiaslocales.utils.TransitionHelper;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

/**
 * Pantalla de Registro con Firebase Authentication
 * Incluye: registro real, validación en tiempo real, indicador de fortaleza de contraseña
 */
public class RegistroActivity extends AppCompatActivity {
    private static final String TAG = "RegistroActivity";

    private ImageView btnBack;
    private TextInputLayout tilNombre;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilPasswordConfirm;
    private TextInputEditText etNombre;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etPasswordConfirm;
    private MaterialButton btnRegistro;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private View passwordStrengthIndicator;
    private TextView tvPasswordStrength;

    private FirebaseManager firebaseManager;
    private boolean procesando = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        firebaseManager = FirebaseManager.getInstance();

        inicializarVistas();
        configurarValidacionEnTiempoReal();
        configurarBotones();
        animarEntrada();
    }

    private void inicializarVistas() {
        btnBack = findViewById(R.id.btn_back);
        tilNombre = findViewById(R.id.til_nombre);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilPasswordConfirm = findViewById(R.id.til_password_confirm);
        etNombre = findViewById(R.id.et_nombre);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        btnRegistro = findViewById(R.id.btn_registro);
        tvLogin = findViewById(R.id.tv_login);
        progressBar = findViewById(R.id.progress_bar);
        passwordStrengthIndicator = findViewById(R.id.password_strength_indicator);
        tvPasswordStrength = findViewById(R.id.tv_password_strength);
    }

    private void configurarValidacionEnTiempoReal() {
        // Validar nombre
        etNombre.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilNombre != null) tilNombre.setError(null);
            }
        });

        // Validar email
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilEmail != null) tilEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    tilEmail.setError("Email inválido");
                }
            }
        });

        // Validar password con indicador de fortaleza
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilPassword != null) tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                actualizarIndicadorFortaleza(s.toString());
            }
        });

        // Validar confirmación de password
        etPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilPasswordConfirm != null) tilPasswordConfirm.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = etPassword.getText().toString();
                String confirm = s.toString();
                if (!confirm.isEmpty() && !confirm.equals(password)) {
                    tilPasswordConfirm.setError("Las contraseñas no coinciden");
                }
            }
        });
    }

    private void actualizarIndicadorFortaleza(String password) {
        if (passwordStrengthIndicator == null || tvPasswordStrength == null) return;

        int fortaleza = calcularFortalezaPassword(password);

        // Mostrar/ocultar indicador
        if (password.isEmpty()) {
            passwordStrengthIndicator.setVisibility(View.GONE);
            tvPasswordStrength.setVisibility(View.GONE);
            return;
        }

        passwordStrengthIndicator.setVisibility(View.VISIBLE);
        tvPasswordStrength.setVisibility(View.VISIBLE);

        // Actualizar color y texto según fortaleza
        int color;
        String texto;

        if (fortaleza < 2) {
            color = getResources().getColor(R.color.error, null);
            texto = "Débil";
        } else if (fortaleza < 4) {
            color = getResources().getColor(R.color.warning, null);
            texto = "Media";
        } else {
            color = getResources().getColor(R.color.success, null);
            texto = "Fuerte";
        }

        passwordStrengthIndicator.setBackgroundColor(color);
        tvPasswordStrength.setText(texto);
        tvPasswordStrength.setTextColor(color);

        // Animar ancho del indicador
        float widthPercent = Math.min(1f, fortaleza / 5f);
        passwordStrengthIndicator.setScaleX(widthPercent);
    }

    private int calcularFortalezaPassword(String password) {
        int fortaleza = 0;

        if (password.length() >= 6) fortaleza++;
        if (password.length() >= 8) fortaleza++;
        if (password.matches(".*[A-Z].*")) fortaleza++; // Mayúscula
        if (password.matches(".*[0-9].*")) fortaleza++; // Número
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) fortaleza++; // Especial

        return fortaleza;
    }

    private void configurarBotones() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> TransitionHelper.finishWithSlideRight(this));
        }

        btnRegistro.setOnClickListener(v -> intentarRegistro());

        tvLogin.setOnClickListener(v -> TransitionHelper.finishWithSlideRight(this));
    }

    private void animarEntrada() {
        View[] elementos = {
            findViewById(R.id.tv_titulo),
            findViewById(R.id.tv_subtitulo),
            tilNombre,
            tilEmail,
            tilPassword,
            tilPasswordConfirm,
            btnRegistro,
            findViewById(R.id.layout_login)
        };

        for (int i = 0; i < elementos.length; i++) {
            View elemento = elementos[i];
            if (elemento != null) {
                elemento.setAlpha(0f);
                elemento.setTranslationY(30);
                elemento.animate()
                    .alpha(1f)
                    .translationY(0)
                    .setDuration(400)
                    .setStartDelay(i * 60L)
                    .start();
            }
        }
    }

    private void intentarRegistro() {
        if (procesando) return;

        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();

        // Validar campos
        if (!validarCampos(nombre, email, password, passwordConfirm)) return;

        Log.d(TAG, "Intentando registro con email: " + email);
        mostrarCargando(true);

        // Registrar con Firebase Auth
        firebaseManager.registrar(email, password, nombre, new FirebaseManager.FirestoreCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                runOnUiThread(() -> {
                    // Guardar sesión
                    UsuarioPreferences.guardarSesion(
                            RegistroActivity.this,
                            user.getUid(),
                            1,
                            user.getEmail(),
                            nombre,
                            "usuario"
                    );

                    UsuarioPreferences.guardarUserId(RegistroActivity.this, user.getUid());
                    UsuarioPreferences.guardarNombre(RegistroActivity.this, nombre);
                    UsuarioPreferences.guardarFechaRegistro(RegistroActivity.this, System.currentTimeMillis());

                    Log.i(TAG, "Registro exitoso para: " + user.getEmail());
                    showToast("¡Cuenta creada exitosamente!");

                    // Ir a pantalla principal
                    Intent intent = new Intent(RegistroActivity.this, ListaNoticiasActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    TransitionHelper.startActivityFade(RegistroActivity.this, intent);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    mostrarCargando(false);

                    String mensaje = traducirErrorFirebase(e.getMessage());

                    // Shake en el campo con error
                    if (mensaje.contains("email")) {
                        shakeView(tilEmail);
                    } else {
                        shakeView(tilPassword);
                    }

                    showToast(mensaje);
                    Log.e(TAG, "Error en registro: " + e.getMessage());
                });
            }
        });
    }

    private boolean validarCampos(String nombre, String email, String password, String passwordConfirm) {
        boolean valido = true;

        if (nombre.isEmpty()) {
            tilNombre.setError("Ingrese su nombre");
            shakeView(tilNombre);
            valido = false;
        } else if (nombre.length() < 2) {
            tilNombre.setError("Nombre muy corto");
            shakeView(tilNombre);
            valido = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError("Ingrese su email");
            shakeView(tilEmail);
            valido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email inválido");
            shakeView(tilEmail);
            valido = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Ingrese una contraseña");
            shakeView(tilPassword);
            valido = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            shakeView(tilPassword);
            valido = false;
        }

        if (!password.equals(passwordConfirm)) {
            tilPasswordConfirm.setError("Las contraseñas no coinciden");
            shakeView(tilPasswordConfirm);
            valido = false;
        }

        return valido;
    }

    private void mostrarCargando(boolean mostrar) {
        procesando = mostrar;

        if (progressBar != null) {
            progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        }

        btnRegistro.setEnabled(!mostrar);
        btnRegistro.setText(mostrar ? "Creando cuenta..." : getString(R.string.login_sign_up));

        // Deshabilitar campos mientras carga
        etNombre.setEnabled(!mostrar);
        etEmail.setEnabled(!mostrar);
        etPassword.setEnabled(!mostrar);
        etPasswordConfirm.setEnabled(!mostrar);
    }

    private void shakeView(View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        }
    }

    private String traducirErrorFirebase(String error) {
        if (error == null) return "Error desconocido";

        if (error.contains("email-already-in-use")) {
            return "Este email ya está registrado";
        } else if (error.contains("invalid-email")) {
            return "El email no es válido";
        } else if (error.contains("weak-password")) {
            return "La contraseña es muy débil";
        } else if (error.contains("network")) {
            return "Error de conexión. Verifica tu internet";
        }

        return "Error al crear la cuenta";
    }

    private void showToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TransitionHelper.applyBackTransition(this);
    }

    // Helper class para TextWatcher simple
    private abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
