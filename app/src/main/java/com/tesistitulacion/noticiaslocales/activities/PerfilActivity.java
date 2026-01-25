package com.tesistitulacion.noticiaslocales.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Usuario;
import com.tesistitulacion.noticiaslocales.utils.DialogHelper;
import com.tesistitulacion.noticiaslocales.utils.TransitionHelper;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;
import com.tesistitulacion.noticiaslocales.utils.ThemeManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Pantalla de perfil del usuario - Versión completa con Material Design 3
 * Incluye: foto de perfil, estadísticas, edición de datos, configuración
 */
public class PerfilActivity extends BaseActivity {
    private static final String TAG = "PerfilActivity";
    private static final int REQUEST_CODE_EDITAR_PERFIL = 1001;

    // Constantes de texto por defecto
    private static final String DEFAULT_BIO = "DEFAULT_BIO";
    private static final String DEFAULT_PHONE = "DEFAULT_PHONE";
    private static final String DEFAULT_LOCATION = "DEFAULT_LOCATION";

    // Views - Header
    private ImageView ivAvatar;
    private FloatingActionButton fabCamera;

    // Views - Estadísticas
    private TextView tvNoticiasLeidas;
    private TextView tvNoticiasGuardadas;
    private TextView tvDiasActivo;

    // Views - Información Personal
    private TextView tvNombre;
    private TextView tvEmail;
    private com.google.android.material.button.MaterialButton btnEditarNombre;

    // Views - Configuración
    private SwitchMaterial switchNotificaciones;
    private SwitchMaterial switchOcultarNoticias;
    private LinearLayout btnCambiarPassword;
    private LinearLayout btnOcultarNoticias;

    // Views - Botones
    private TextView btnCerrarSesion;
    private TextView btnSettings;
    private LinearLayout btnArticulosGuardados;
    private LinearLayout btnNotificaciones;

    // Views - Intereses
    private com.google.android.material.chip.ChipGroup chipGroupIntereses;

    // Data
    private FirebaseManager firebaseManager;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageRef;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

    @Override
    protected int getNavegacionActiva() {
        return NAV_PERFIL;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_perfil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializarFirebase();
        inicializarVistas();
        inicializarActivityLaunchers();
        cargarDatosUsuario();
        configurarListeners();
        cargarEstadisticas();
        cargarIntereses();
    }

    private void inicializarFirebase() {
        firebaseManager = FirebaseManager.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    private void inicializarVistas() {
        try {
            // Header
            ivAvatar = findViewById(R.id.iv_avatar);
            fabCamera = findViewById(R.id.fab_camera);

            // Estadísticas
            tvNoticiasLeidas = findViewById(R.id.tv_noticias_leidas);
            tvNoticiasGuardadas = findViewById(R.id.tv_noticias_guardadas);
            tvDiasActivo = findViewById(R.id.tv_dias_activo);

            // Información Personal
            tvNombre = findViewById(R.id.tv_nombre);
            tvEmail = findViewById(R.id.tv_email);
            btnEditarNombre = findViewById(R.id.btn_editar_nombre);

            Log.d(TAG, "btnEditarNombre inicializado: " + (btnEditarNombre != null));

            // Configuración
            switchNotificaciones = findViewById(R.id.switch_notificaciones);
            switchOcultarNoticias = findViewById(R.id.switch_ocultar_noticias);
            btnCambiarPassword = findViewById(R.id.btn_cambiar_password);
            btnOcultarNoticias = findViewById(R.id.btn_ocultar_noticias);

            // Botones
            btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
            btnSettings = findViewById(R.id.btn_settings);
            btnArticulosGuardados = findViewById(R.id.btn_editar_bio);
            btnNotificaciones = findViewById(R.id.btn_notificaciones);

            Log.d(TAG, "btnNotificaciones inicializado: " + (btnNotificaciones != null));

            // Intereses
            chipGroupIntereses = findViewById(R.id.chip_group_intereses);

            Log.d(TAG, "Vistas inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar vistas", e);
            showToast("Error al cargar interfaz: " + e.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void inicializarActivityLaunchers() {
        // Camera Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                subirFotoAPerfil(imageBitmap);
                            }
                        }
                    }
                }
        );

        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        getContentResolver(), imageUri);
                                subirFotoAPerfil(bitmap);
                            } catch (IOException e) {
                                Log.e(TAG, "Error al cargar imagen de galería", e);
                                showToast("Error al cargar imagen");
                            }
                        }
                    }
                }
        );

        // Camera Permission Launcher
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamara();
                    } else {
                        showToast("Permiso de cámara denegado");
                    }
                }
        );

        // Storage Permission Launcher
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirGaleria();
                    } else {
                        showToast("Permiso de almacenamiento denegado");
                    }
                }
        );
    }

    private void cargarDatosUsuario() {
        try {
            // Cargar datos locales
            String nombre = UsuarioPreferences.getNombre(this);
            String apellido = UsuarioPreferences.getApellido(this);
            String email = UsuarioPreferences.getEmail(this);
            String fotoPerfil = UsuarioPreferences.getFotoPerfil(this);

            // Mostrar en UI
            if (tvNombre != null) {
                if (nombre != null && apellido != null) {
                    tvNombre.setText(nombre + " " + apellido);
                } else if (nombre != null) {
                    tvNombre.setText(nombre);
                } else {
                    tvNombre.setText("Usuario");
                }
            }

            if (tvEmail != null) {
                tvEmail.setText(email != null ? email : "Sin email");
            }

            // Cargar foto de perfil (soporta URL y Base64)
            if (ivAvatar != null) {
                com.tesistitulacion.noticiaslocales.utils.ImageHelper.cargarAvatar(this, ivAvatar, fotoPerfil);
            }

            // Cargar configuración
            if (switchNotificaciones != null) {
                boolean notificacionesActivas = UsuarioPreferences.getNotificacionesActivas(this);
                switchNotificaciones.setChecked(notificacionesActivas);
            }

            if (switchOcultarNoticias != null) {
                boolean ocultarNoticias = UsuarioPreferences.getMostrarSoloCercanas(this);
                switchOcultarNoticias.setChecked(ocultarNoticias);
            }

            // Cargar datos completos desde Firestore
            String userId = UsuarioPreferences.getUserId(this);
            if (userId != null && !userId.isEmpty()) {
                cargarDatosDesdeFirestore(userId);
            }

            Log.d(TAG, "Datos de usuario cargados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar datos de usuario", e);
            showToast("Error al cargar datos: " + e.getMessage());
        }
    }

    private void cargarDatosDesdeFirestore(String userId) {
        firebaseManager.obtenerUsuarioPorId(userId, new FirebaseManager.FirestoreCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario) {
                runOnUiThread(() -> actualizarUIConDatosCompletos(usuario));
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al cargar datos de Firestore: " + e.getMessage());
            }
        });
    }

    private void actualizarUIConDatosCompletos(Usuario usuario) {
        // Guardar datos en preferencias
        if (usuario.getBio() != null && !usuario.getBio().isEmpty()) {
            UsuarioPreferences.guardarBio(this, usuario.getBio());
        }

        if (usuario.getTelefonocelular() != null && !usuario.getTelefonocelular().isEmpty()) {
            UsuarioPreferences.guardarTelefono(this, usuario.getTelefonocelular());
        }

        if (usuario.getUbicacion() != null && !usuario.getUbicacion().isEmpty()) {
            UsuarioPreferences.guardarUbicacion(this, usuario.getUbicacion());
        }

        // Actualizar foto de perfil (soporta URL y Base64)
        if (usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
            com.tesistitulacion.noticiaslocales.utils.ImageHelper.cargarAvatar(this, ivAvatar, usuario.getFotoPerfil());
        }

        // Actualizar estadística de noticias leídas
        if (tvNoticiasLeidas != null) {
            int noticiasLeidas = usuario.getNoticiasLeidas() != null ? usuario.getNoticiasLeidas() : 0;
            tvNoticiasLeidas.setText(String.valueOf(noticiasLeidas));
            Log.d(TAG, "Noticias leídas actualizadas: " + noticiasLeidas);
        }

        // Guardar fecha de registro si existe en el usuario
        if (usuario.getFechaRegistro() != null && usuario.getFechaRegistro() > 0) {
            long timestamp = usuario.getFechaRegistro();
            UsuarioPreferences.guardarFechaRegistro(this, timestamp);

            // Actualizar días activos
            if (tvDiasActivo != null) {
                long diasActivos = calcularDiasActivos(timestamp);
                tvDiasActivo.setText(String.valueOf(diasActivos));
                Log.d(TAG, "Días activos actualizados: " + diasActivos);
            }
        }

        // Actualizar noticias guardadas (datos locales)
        if (tvNoticiasGuardadas != null) {
            java.util.List<String> noticiasGuardadas = UsuarioPreferences.getNoticiasGuardadas(this);
            int cantidad = noticiasGuardadas != null ? noticiasGuardadas.size() : 0;
            tvNoticiasGuardadas.setText(String.valueOf(cantidad));
        }
    }

    private void cargarEstadisticas() {
        try {
            // Cargar cantidad de noticias guardadas (desde preferencias locales)
            if (tvNoticiasGuardadas != null) {
                java.util.List<String> noticiasGuardadas = UsuarioPreferences.getNoticiasGuardadas(this);
                int cantidad = noticiasGuardadas != null ? noticiasGuardadas.size() : 0;
                tvNoticiasGuardadas.setText(String.valueOf(cantidad));
                Log.d(TAG, "Noticias guardadas: " + cantidad);
            }

            // Calcular días activos desde fecha de registro
            if (tvDiasActivo != null) {
                Long fechaRegistro = UsuarioPreferences.getFechaRegistro(this);
                if (fechaRegistro != null && fechaRegistro > 0) {
                    long diasActivos = calcularDiasActivos(fechaRegistro);
                    tvDiasActivo.setText(String.valueOf(diasActivos));
                    Log.d(TAG, "Días activos: " + diasActivos);
                } else {
                    // Si no hay fecha de registro, usar fecha actual como inicio
                    long ahora = System.currentTimeMillis();
                    UsuarioPreferences.guardarFechaRegistro(this, ahora);
                    tvDiasActivo.setText("1");
                    Log.d(TAG, "Días activos: 1 (fecha de registro establecida)");
                }
            }

            // Cargar noticias leídas desde Firestore
            cargarNoticiasLeidasDesdeFirestore();

            Log.d(TAG, "Estadísticas cargadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar estadísticas", e);
        }
    }

    /**
     * Carga el contador de noticias leídas desde Firestore
     */
    private void cargarNoticiasLeidasDesdeFirestore() {
        String userId = UsuarioPreferences.getUserId(this);
        if (userId == null || userId.isEmpty()) {
            if (tvNoticiasLeidas != null) {
                tvNoticiasLeidas.setText("0");
            }
            return;
        }

        firebaseManager.obtenerUsuarioPorId(userId, new FirebaseManager.FirestoreCallback<Usuario>() {
            @Override
            public void onSuccess(Usuario usuario) {
                runOnUiThread(() -> {
                    if (tvNoticiasLeidas != null && usuario != null) {
                        int noticiasLeidas = usuario.getNoticiasLeidas() != null ? usuario.getNoticiasLeidas() : 0;
                        tvNoticiasLeidas.setText(String.valueOf(noticiasLeidas));
                        Log.d(TAG, "Noticias leídas desde Firestore: " + noticiasLeidas);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al cargar noticias leídas: " + e.getMessage());
                runOnUiThread(() -> {
                    if (tvNoticiasLeidas != null) {
                        tvNoticiasLeidas.setText("0");
                    }
                });
            }
        });
    }

    private long calcularDiasActivos(long fechaRegistroTimestamp) {
        long ahora = System.currentTimeMillis();
        long diferencia = ahora - fechaRegistroTimestamp;
        return TimeUnit.MILLISECONDS.toDays(diferencia) + 1; // +1 para contar el día actual
    }

    private void configurarListeners() {
        try {
            // FAB Camera
            if (fabCamera != null) {
                fabCamera.setOnClickListener(v -> mostrarDialogoSeleccionarFoto());
            }

            // Botones de edición
            if (btnEditarNombre != null) {
                btnEditarNombre.setOnClickListener(v -> {
                    Log.d(TAG, "Click en botón Editar Perfil - Abriendo EditarPerfilActivity");
                    // Abrir pantalla de Editar Perfil con animación
                    Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_EDITAR_PERFIL);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
                Log.d(TAG, "Listener configurado para btnEditarNombre");
            } else {
                Log.e(TAG, "ERROR: btnEditarNombre es NULL, no se pudo configurar el listener");
            }
            // Los botones de edición ahora abren EditarPerfilActivity

            // Switches
            if (switchNotificaciones != null) {
                switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    UsuarioPreferences.guardarNotificacionesActivas(this, isChecked);
                    Toast.makeText(this,
                            isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas",
                            Toast.LENGTH_SHORT).show();
                });
            }

            // Switch Modo Pokémon - Listener unificado para cambios
            if (switchOcultarNoticias != null) {
                switchOcultarNoticias.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    UsuarioPreferences.guardarMostrarSoloCercanas(this, isChecked);
                });
            }

            // LinearLayout de Modo Pokémon - Click en cualquier parte activa el switch
            if (btnOcultarNoticias != null && switchOcultarNoticias != null) {
                btnOcultarNoticias.setOnClickListener(v -> {
                    switchOcultarNoticias.toggle();
                });
            }

            // Cambiar contraseña
            if (btnCambiarPassword != null) {
                btnCambiarPassword.setOnClickListener(v -> mostrarDialogoCambiarPassword());
            }

            // Botón settings (Ajustes) con animación
            if (btnSettings != null) {
                btnSettings.setOnClickListener(v -> {
                    Intent intent = new Intent(PerfilActivity.this, AjustesActivity.class);
                    TransitionHelper.startActivitySlideRight(PerfilActivity.this, intent);
                });
            }

            // Botón Artículos Guardados con animación
            if (btnArticulosGuardados != null) {
                btnArticulosGuardados.setOnClickListener(v -> {
                    Intent intent = new Intent(PerfilActivity.this, ArticulosGuardadosActivity.class);
                    TransitionHelper.startActivitySlideRight(PerfilActivity.this, intent);
                });
            }

            // Botón Notificaciones con animación
            if (btnNotificaciones != null) {
                btnNotificaciones.setOnClickListener(v -> {
                    Log.d(TAG, "Click en botón Notificaciones - Abriendo NotificacionesActivity");
                    try {
                        Intent intent = new Intent(PerfilActivity.this, NotificacionesActivity.class);
                        TransitionHelper.startActivitySlideRight(PerfilActivity.this, intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error al abrir NotificacionesActivity: " + e.getMessage(), e);
                        showToast("Error al abrir notificaciones: " + e.getMessage());
                    }
                });
                Log.d(TAG, "Listener configurado para btnNotificaciones");
            } else {
                Log.e(TAG, "ERROR: btnNotificaciones es NULL");
            }

            // Cerrar sesión
            if (btnCerrarSesion != null) {
                btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
            }

            Log.d(TAG, "Listeners configurados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar listeners", e);
        }
    }

    // ==================== GESTIÓN DE FOTO DE PERFIL ====================

    private void mostrarDialogoSeleccionarFoto() {
        String[] opciones = {"Tomar foto", "Seleccionar de galería"};

        DialogHelper.showListDialog(this, "Foto de perfil", opciones, (dialog, which) -> {
            if (which == 0) {
                verificarPermisoYAbrirCamara();
            } else {
                verificarPermisoYAbrirGaleria();
            }
        });
    }

    private void verificarPermisoYAbrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void verificarPermisoYAbrirGaleria() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria();
        } else {
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            showToast("No hay aplicación de cámara disponible");
        }
    }

    private void abrirGaleria() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhotoIntent);
    }

    private void subirFotoAPerfil(Bitmap bitmap) {
        // Mostrar indicador de carga
        showToast("Subiendo foto...");

        // Comprimir imagen
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        // Crear referencia única en Firebase Storage
        String userId = UsuarioPreferences.getUserId(this);
        if (userId == null || userId.isEmpty()) {
            showToast("Error: Usuario no identificado");
            return;
        }

        String fileName = "profile_" + userId + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference fotoRef = storageRef.child("usuarios/fotos_perfil/" + fileName);

        // Subir imagen
        fotoRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener URL de descarga
                    fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String fotoUrl = uri.toString();
                        actualizarFotoPerfilEnFirestore(fotoUrl);
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error al obtener URL de foto", e);
                        showToast("Error al obtener URL de foto");
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al subir foto", e);
                    showToast("Error al subir foto");
                });
    }

    private void actualizarFotoPerfilEnFirestore(String fotoUrl) {
        String userId = UsuarioPreferences.getUserId(this);

        Map<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("fotoPerfil", fotoUrl);

        firebaseManager.actualizarUsuario(userId, actualizacion, new FirebaseManager.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    // Actualizar UI (soporta URL y Base64)
                    com.tesistitulacion.noticiaslocales.utils.ImageHelper.cargarAvatar(PerfilActivity.this, ivAvatar, fotoUrl);

                    // Guardar en preferencias
                    UsuarioPreferences.guardarFotoPerfil(PerfilActivity.this, fotoUrl);

                    showToast("Foto de perfil actualizada");
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al actualizar foto en Firestore: " + e.getMessage());
                showToast("Error al guardar foto");
            }
        });
    }

    // ==================== DIÁLOGOS DE CONFIGURACIÓN ====================

    private void mostrarDialogoCambiarPassword() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cambiar_password, null);
        TextInputEditText etPasswordActual = dialogView.findViewById(R.id.et_password_actual);
        TextInputEditText etPasswordNuevo = dialogView.findViewById(R.id.et_password_nuevo);
        TextInputEditText etPasswordConfirmar = dialogView.findViewById(R.id.et_password_confirmar);

        new AlertDialog.Builder(this)
                .setTitle("Cambiar contraseña")
                .setView(dialogView)
                .setPositiveButton("Cambiar", (dialog, which) -> {
                    String passwordActual = etPasswordActual.getText().toString();
                    String passwordNuevo = etPasswordNuevo.getText().toString();
                    String passwordConfirmar = etPasswordConfirmar.getText().toString();

                    if (passwordActual.isEmpty() || passwordNuevo.isEmpty() || passwordConfirmar.isEmpty()) {
                        showToast("Completa todos los campos");
                        return;
                    }

                    if (!passwordNuevo.equals(passwordConfirmar)) {
                        showToast("Las contraseñas no coinciden");
                        return;
                    }

                    if (passwordNuevo.length() < 6) {
                        showToast("La contraseña debe tener al menos 6 caracteres");
                        return;
                    }

                    cambiarPassword(passwordActual, passwordNuevo);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ==================== CAMBIO DE CONTRASEÑA ====================

    private void cambiarPassword(String passwordActual, String passwordNuevo) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user == null || user.getEmail() == null) {
            showToast("No hay usuario autenticado");
            return;
        }

        // Re-autenticar al usuario antes de cambiar la contraseña
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passwordActual);

        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Re-autenticación exitosa, ahora cambiar la contraseña
                    user.updatePassword(passwordNuevo)
                            .addOnSuccessListener(aVoid1 -> {
                                showToast("Contraseña actualizada correctamente");
                                Log.d(TAG, "Contraseña actualizada correctamente");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al actualizar contraseña", e);
                                showToast("Error al actualizar contraseña: " + e.getMessage(), Toast.LENGTH_LONG);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al reautenticar usuario", e);
                    showToast("Contraseña actual incorrecta");
                });
    }

    private void cerrarSesion() {
        DialogHelper.showConfirmationDialog(this, "Cerrar sesión",
                "¿Estás seguro que deseas cerrar sesión?",
                "Sí, cerrar sesión", "Cancelar", () -> {
                    UsuarioPreferences.cerrarSesion(this);

                    showToast("Sesión cerrada");

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    // ==================== GESTIÓN DE INTERESES ====================

    /**
     * Carga y muestra los intereses del usuario como chips
     */
    private void cargarIntereses() {
        if (chipGroupIntereses == null) return;

        try {
            chipGroupIntereses.removeAllViews();

            // Obtener intereses guardados
            java.util.List<String> intereses = UsuarioPreferences.getIntereses(this);

            // Si no hay intereses, agregar algunos por defecto
            if (intereses.isEmpty()) {
                intereses = java.util.Arrays.asList("Política", "Economía", "Cultura", "Deportes");
                UsuarioPreferences.guardarIntereses(this, intereses);
            }

            // Crear chip para cada interés
            for (String interes : intereses) {
                agregarChipInteres(interes, true);
            }

            // Agregar chip "+" para agregar nuevos intereses
            agregarChipAgregar();

            Log.d(TAG, "Intereses cargados: " + intereses.size());
        } catch (Exception e) {
            Log.e(TAG, "Error al cargar intereses", e);
        }
    }

    /**
     * Agrega un chip de interés al ChipGroup
     */
    private void agregarChipInteres(String interes, boolean seleccionado) {
        com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(this);
        chip.setText(interes);
        chip.setChecked(seleccionado);
        chip.setCheckable(true);
        chip.setTextColor(getResources().getColor(R.color.chip_text, null));
        chip.setChipBackgroundColorResource(R.color.chip_background);
        chip.setChipStrokeColorResource(R.color.stroke);
        chip.setChipStrokeWidth(1.5f);
        chip.setChipCornerRadius(20f);
        chip.setCloseIconVisible(true);
        chip.setCloseIconTintResource(R.color.icon_secondary);

        // Al hacer clic en el chip (seleccionar/deseleccionar)
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                UsuarioPreferences.agregarInteres(this, interes);
                showToast("Interés agregado: " + interes);
            } else {
                UsuarioPreferences.eliminarInteres(this, interes);
                showToast("Interés eliminado: " + interes);
            }
        });

        // Al hacer clic en la X (eliminar)
        chip.setOnCloseIconClickListener(v -> {
            chipGroupIntereses.removeView(chip);
            UsuarioPreferences.eliminarInteres(this, interes);
            showToast("Interés eliminado: " + interes);
        });

        chipGroupIntereses.addView(chip);
    }

    /**
     * Agrega el chip "+" para agregar nuevos intereses
     */
    private void agregarChipAgregar() {
        com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(this);
        chip.setText("+");
        chip.setTextColor(getResources().getColor(R.color.chip_text, null));
        chip.setTextSize(24);
        chip.setChipBackgroundColorResource(R.color.chip_background);
        chip.setChipStrokeColorResource(R.color.stroke);
        chip.setChipStrokeWidth(1.5f);
        chip.setChipCornerRadius(20f);

        chip.setOnClickListener(v -> mostrarDialogoAgregarInteres());

        chipGroupIntereses.addView(chip);
    }

    /**
     * Muestra diálogo para agregar un nuevo interés
     */
    private void mostrarDialogoAgregarInteres() {
        String[] opcionesIntereses = {
            "Política", "Economía", "Cultura", "Deportes",
            "Educación", "Salud", "Seguridad", "Medio Ambiente",
            "Turismo", "Tecnología", "Gastronomía", "Arte"
        };

        DialogHelper.showListDialog(this, "Agregar interés", opcionesIntereses, (dialog, which) -> {
            String nuevoInteres = opcionesIntereses[which];

            // Verificar si ya existe
            if (UsuarioPreferences.tieneInteres(this, nuevoInteres)) {
                showToast("Ya tienes este interés agregado");
                return;
            }

            // Agregar y recargar
            UsuarioPreferences.agregarInteres(this, nuevoInteres);
            cargarIntereses();
            showToast("Interés agregado: " + nuevoInteres);
        });
    }

    // ==================== LIFECYCLE ====================

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estadísticas cada vez que se vuelve a la pantalla
        // (por si el usuario guardó artículos en otra pantalla)
        actualizarEstadisticasLocales();
    }

    /**
     * Actualiza solo las estadísticas que se guardan localmente
     * (noticias guardadas) sin hacer llamadas a Firestore
     */
    private void actualizarEstadisticasLocales() {
        // Actualizar noticias guardadas
        if (tvNoticiasGuardadas != null) {
            java.util.List<String> noticiasGuardadas = UsuarioPreferences.getNoticiasGuardadas(this);
            int cantidad = noticiasGuardadas != null ? noticiasGuardadas.size() : 0;
            tvNoticiasGuardadas.setText(String.valueOf(cantidad));
            Log.d(TAG, "Noticias guardadas actualizadas: " + cantidad);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDITAR_PERFIL && resultCode == RESULT_OK) {
            // Recargar los datos del perfil después de editar
            Log.d(TAG, "Perfil editado, recargando datos...");
            cargarDatosUsuario();
            showToast("Perfil actualizado");
        }
    }

}
