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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.utils.LocaleManager;
import com.tesistitulacion.noticiaslocales.utils.TransitionHelper;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla de Login con Firebase Authentication
 * Incluye: login con email, Google Sign-In, recuperar contraseña
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private MaterialButton btnGoogle;
    private TextView tvRegistro;
    private TextView tvOlvidePassword;
    private ProgressBar progressBar;

    private FirebaseManager firebaseManager;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private boolean procesando = false;

    // Launcher para Google Sign-In
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleGoogleSignInResult(task);
            }
    );

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseManager = FirebaseManager.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        configurarGoogleSignIn();
        inicializarVistas();
        configurarValidacionEnTiempoReal();
        configurarBotones();
        animarEntrada();
    }

    private void configurarGoogleSignIn() {
        // Configurar opciones de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void inicializarVistas() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogle = findViewById(R.id.btn_google);
        tvRegistro = findViewById(R.id.tv_registro);
        tvOlvidePassword = findViewById(R.id.tv_olvide_password);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void configurarValidacionEnTiempoReal() {
        // Validar email mientras escribe
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilEmail != null) {
                    tilEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    tilEmail.setError("Email inválido");
                }
            }
        });

        // Limpiar error de password al escribir
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilPassword != null) {
                    tilPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void configurarBotones() {
        btnLogin.setOnClickListener(v -> intentarLogin());

        if (btnGoogle != null) {
            btnGoogle.setOnClickListener(v -> iniciarGoogleSignIn());
        }

        tvRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistroActivity.class);
            TransitionHelper.startActivitySlideRight(this, intent);
        });

        if (tvOlvidePassword != null) {
            tvOlvidePassword.setOnClickListener(v -> mostrarDialogoRecuperarPassword());
        }
    }

    private void animarEntrada() {
        // Animar elementos con fade in escalonado
        View[] elementos = {
            findViewById(R.id.card_logo),
            findViewById(R.id.tv_titulo),
            findViewById(R.id.tv_subtitulo),
            tilEmail,
            tilPassword,
            tvOlvidePassword,
            btnLogin,
            btnGoogle,
            findViewById(R.id.layout_registro)
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
                    .setStartDelay(i * 80L)
                    .start();
            }
        }
    }

    // ==================== LOGIN CON EMAIL ====================

    private void intentarLogin() {
        if (procesando) return;

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validar campos
        if (!validarCampos(email, password)) return;

        Log.d(TAG, "Intentando login con email: " + email);
        mostrarCargando(true);

        // Login con Firebase Auth
        firebaseManager.login(email, password, new FirebaseManager.FirestoreCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                runOnUiThread(() -> {
                    guardarSesionYNavegar(user, obtenerNombreDeEmail(user.getEmail()));
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    mostrarCargando(false);

                    String mensaje = traducirErrorFirebase(e.getMessage());

                    // Animación de shake en los campos
                    shakeView(tilEmail);
                    shakeView(tilPassword);

                    showToast(mensaje);
                    Log.e(TAG, "Error en login: " + e.getMessage());
                });
            }
        });
    }

    private boolean validarCampos(String email, String password) {
        boolean valido = true;

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
            tilPassword.setError("Ingrese su contraseña");
            shakeView(tilPassword);
            valido = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            shakeView(tilPassword);
            valido = false;
        }

        return valido;
    }

    // ==================== GOOGLE SIGN-IN ====================

    private void iniciarGoogleSignIn() {
        if (procesando) return;

        Log.d(TAG, "Iniciando Google Sign-In");
        mostrarCargando(true);

        // Cerrar sesión de Google anterior para mostrar el selector de cuentas
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "Google Sign-In exitoso: " + account.getEmail());
            autenticarConFirebase(account);
        } catch (ApiException e) {
            mostrarCargando(false);
            Log.e(TAG, "Google Sign-In falló con código: " + e.getStatusCode(), e);

            String mensaje;
            switch (e.getStatusCode()) {
                case 12501: // SIGN_IN_CANCELLED
                    mensaje = "Inicio de sesión cancelado";
                    break;
                case 12502: // SIGN_IN_CURRENTLY_IN_PROGRESS
                    mensaje = "Ya hay un inicio de sesión en progreso";
                    break;
                case 7: // NETWORK_ERROR
                    mensaje = "Error de conexión. Verifica tu internet";
                    break;
                default:
                    mensaje = "Error al iniciar sesión con Google";
            }
            showToast(mensaje);
        }
    }

    private void autenticarConFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        Log.i(TAG, "Firebase Auth exitoso: " + user.getEmail());

                        // Verificar si es usuario nuevo
                        boolean isNewUser = authResult.getAdditionalUserInfo() != null &&
                                authResult.getAdditionalUserInfo().isNewUser();

                        if (isNewUser) {
                            // Crear documento de usuario en Firestore
                            crearUsuarioEnFirestore(user, account.getDisplayName());
                        } else {
                            guardarSesionYNavegar(user, account.getDisplayName());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarCargando(false);
                    Log.e(TAG, "Error en Firebase Auth con Google", e);
                    showToast("Error al autenticar con Google");
                });
    }

    private void crearUsuarioEnFirestore(FirebaseUser user, String nombre) {
        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("nombre", nombre != null ? nombre : obtenerNombreDeEmail(user.getEmail()));
        usuarioData.put("email", user.getEmail());
        usuarioData.put("fotoPerfil", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
        usuarioData.put("fechaRegistro", System.currentTimeMillis());
        usuarioData.put("noticiasLeidas", 0);
        usuarioData.put("verificado", user.isEmailVerified());
        usuarioData.put("tipoUsuario", "usuario");
        usuarioData.put("provider", "google");

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(user.getUid())
                .set(usuarioData)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "Usuario creado en Firestore");
                    guardarSesionYNavegar(user, nombre);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al crear usuario en Firestore", e);
                    // Aún así navegamos
                    guardarSesionYNavegar(user, nombre);
                });
    }

    // ==================== SESIÓN Y NAVEGACIÓN ====================

    private void guardarSesionYNavegar(FirebaseUser user, String nombre) {
        // Guardar sesión
        UsuarioPreferences.guardarSesion(
                this,
                user.getUid(),
                1,
                user.getEmail(),
                nombre != null ? nombre : obtenerNombreDeEmail(user.getEmail()),
                "usuario"
        );

        UsuarioPreferences.guardarUserId(this, user.getUid());

        // Guardar foto de perfil si viene de Google
        if (user.getPhotoUrl() != null) {
            UsuarioPreferences.guardarFotoPerfil(this, user.getPhotoUrl().toString());
        }

        Log.i(TAG, "Login exitoso para: " + user.getEmail());
        showToast("¡Bienvenido!");

        // Ir a pantalla principal
        Intent intent = new Intent(this, ListaNoticiasActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TransitionHelper.startActivityFade(this, intent);
    }

    // ==================== RECUPERAR PASSWORD ====================

    private void mostrarDialogoRecuperarPassword() {
        // Inflar layout del diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_recuperar_password, null);
        TextInputEditText etEmailRecuperar = dialogView.findViewById(R.id.et_email_recuperar);

        // Pre-llenar con email si ya lo escribió
        String emailActual = etEmail.getText().toString().trim();
        if (!emailActual.isEmpty()) {
            etEmailRecuperar.setText(emailActual);
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Recuperar Contraseña")
                .setMessage("Te enviaremos un enlace para restablecer tu contraseña")
                .setView(dialogView)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String email = etEmailRecuperar.getText().toString().trim();
                    if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        enviarEmailRecuperacion(email);
                    } else {
                        showToast("Ingresa un email válido");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void enviarEmailRecuperacion(String email) {
        mostrarCargando(true);

        firebaseManager.recuperarPassword(email, new FirebaseManager.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    mostrarCargando(false);

                    new MaterialAlertDialogBuilder(LoginActivity.this)
                            .setTitle("Email Enviado")
                            .setMessage("Revisa tu bandeja de entrada en " + email + " y sigue las instrucciones para restablecer tu contraseña.")
                            .setPositiveButton("Entendido", null)
                            .setIcon(R.drawable.ic_email)
                            .show();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    mostrarCargando(false);
                    String mensaje = traducirErrorFirebase(e.getMessage());
                    showToast(mensaje);
                });
            }
        });
    }

    // ==================== UTILIDADES ====================

    private void mostrarCargando(boolean mostrar) {
        procesando = mostrar;

        if (progressBar != null) {
            progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        }

        btnLogin.setEnabled(!mostrar);
        if (btnGoogle != null) {
            btnGoogle.setEnabled(!mostrar);
        }
        btnLogin.setText(mostrar ? "Iniciando..." : getString(R.string.login_sign_in));

        // Deshabilitar campos mientras carga
        etEmail.setEnabled(!mostrar);
        etPassword.setEnabled(!mostrar);
    }

    private void shakeView(View view) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
        }
    }

    private String traducirErrorFirebase(String error) {
        if (error == null) return "Error desconocido";

        if (error.contains("no user record") || error.contains("user-not-found")) {
            return "No existe una cuenta con este email";
        } else if (error.contains("wrong-password") || error.contains("invalid-credential")) {
            return "Contraseña incorrecta";
        } else if (error.contains("invalid-email")) {
            return "El email no es válido";
        } else if (error.contains("too-many-requests")) {
            return "Demasiados intentos. Intenta más tarde";
        } else if (error.contains("network")) {
            return "Error de conexión. Verifica tu internet";
        } else if (error.contains("disabled")) {
            return "Esta cuenta ha sido deshabilitada";
        }

        return "Error al iniciar sesión";
    }

    private String obtenerNombreDeEmail(String email) {
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return email;
    }

    private void showToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
