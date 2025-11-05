package com.tesistitulacion.noticiaslocales.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tesistitulacion.noticiaslocales.R;

/**
 * Pantalla de Registro
 * Permite a nuevos usuarios registrarse en la aplicación
 */
public class RegistroActivity extends AppCompatActivity {
    private static final String TAG = "RegistroActivity";

    private EditText etNombre;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private Button btnRegistro;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inicializarVistas();
        configurarBotones();
    }

    private void inicializarVistas() {
        etNombre = findViewById(R.id.et_nombre);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        btnRegistro = findViewById(R.id.btn_registro);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void configurarBotones() {
        btnRegistro.setOnClickListener(v -> intentarRegistro());

        tvLogin.setOnClickListener(v -> {
            // Volver a login
            finish();
        });
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirm = etPasswordConfirm.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            etNombre.setError("Ingrese su nombre");
            etNombre.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Ingrese su email");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingrese una contraseña");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            etPasswordConfirm.setError("Las contraseñas no coinciden");
            etPasswordConfirm.requestFocus();
            return;
        }

        // TODO: Implementar registro con API
        Toast.makeText(this,
                "Funcionalidad de registro en desarrollo.\nPor ahora usa el login.",
                Toast.LENGTH_LONG).show();

        // Volver a login
        finish();
    }
}
