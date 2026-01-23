package com.tesistitulacion.noticiaslocales.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Usuario;
import com.tesistitulacion.noticiaslocales.utils.DialogHelper;
import com.tesistitulacion.noticiaslocales.utils.LocaleManager;
import com.tesistitulacion.noticiaslocales.utils.ThemeManager;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity para editar el perfil del usuario
 * Basado en el diseño GeoNews Edit Profile
 */
public class EditarPerfilActivity extends AppCompatActivity {

    private static final String TAG = "EditarPerfilActivity";
    private static final int MAX_BIO_LENGTH = 150;

    // Views
    private TextView btnCancelar;
    private ImageView ivAvatarEdit;
    private FloatingActionButton fabCambiarFoto;
    private TextView btnChangePhoto;
    private TextInputEditText etNombreCompleto;
    private TextInputEditText etUsername;
    private TextInputEditText etBio;
    private TextView tvBioContador;
    private TextInputEditText etUbicacion;
    private TextInputEditText etEmail;
    private MaterialButton btnGuardarCambios;

    // Data
    private FirebaseManager firebaseManager;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageRef;
    private Uri selectedImageUri;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema
        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        firebaseManager = FirebaseManager.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        inicializarVistas();
        inicializarActivityLaunchers();
        configurarListeners();
        cargarDatosUsuario();
    }

    private void inicializarVistas() {
        btnCancelar = findViewById(R.id.btn_cancelar);
        ivAvatarEdit = findViewById(R.id.iv_avatar_edit);
        fabCambiarFoto = findViewById(R.id.fab_cambiar_foto);
        btnChangePhoto = findViewById(R.id.btn_change_photo);
        etNombreCompleto = findViewById(R.id.et_nombre_completo);
        etUsername = findViewById(R.id.et_username);
        etBio = findViewById(R.id.et_bio);
        tvBioContador = findViewById(R.id.tv_bio_contador);
        etUbicacion = findViewById(R.id.et_ubicacion);
        etEmail = findViewById(R.id.et_email);
        btnGuardarCambios = findViewById(R.id.btn_guardar_cambios);
    }

    private void inicializarActivityLaunchers() {
        // Launcher para cámara
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        // Limpiar tint y mostrar imagen
                        ivAvatarEdit.setImageTintList(null);
                        ivAvatarEdit.setImageBitmap(imageBitmap);
                        subirImagenAPerfil(imageBitmap);
                    }
                });

        // Launcher para galería
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        // Limpiar tint y mostrar imagen
                        ivAvatarEdit.setImageTintList(null);
                        ivAvatarEdit.setImageURI(selectedImageUri);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), selectedImageUri);
                            subirImagenAPerfil(bitmap);
                        } catch (IOException e) {
                            Log.e(TAG, "Error al obtener imagen de galería", e);
                            showToast("Error al cargar imagen");
                        }
                    }
                });

        // Launcher para permisos de cámara
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamara();
                    } else {
                        showToast("Permiso de cámara denegado");
                    }
                });

        // Launcher para permisos de almacenamiento/galería
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirGaleria();
                    } else {
                        showToast("Permiso de galería denegado");
                    }
                });
    }

    private void configurarListeners() {
        // Botón cancelar
        btnCancelar.setOnClickListener(v -> finish());

        // Botones de cambiar foto
        fabCambiarFoto.setOnClickListener(v -> mostrarOpcionesFoto());
        btnChangePhoto.setOnClickListener(v -> mostrarOpcionesFoto());

        // Contador de caracteres de Bio
        etBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                tvBioContador.setText(length + "/" + MAX_BIO_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Botón guardar cambios
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatosUsuario() {
        // Obtener usuario actual de Firebase
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Primero obtener el userId de las preferencias
        String userId = UsuarioPreferences.getUserId(this);

        // Si no hay userId en preferencias, verificar FirebaseAuth
        if (userId == null || userId.isEmpty()) {
            if (currentUser == null) {
                Log.e(TAG, "No hay usuario autenticado en Firebase ni en preferencias");
                showToast("Usuario no autenticado");
                finish();
                return;
            }
            // Si hay usuario de Firebase pero no en preferencias, usar el UID de Firebase
            userId = currentUser.getUid();
            Log.d(TAG, "Usando UID de Firebase: " + userId);
        }

        // Cargar datos desde Firestore si tenemos userId
        final String userIdFinal = userId;
        firebaseManager.obtenerUsuarioPorId(userIdFinal, new FirebaseManager.FirestoreCallback<Usuario>() {
                @Override
                public void onSuccess(Usuario usuario) {
                    if (usuario != null) {
                        mostrarDatosUsuario(usuario);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Error al cargar datos de usuario", e);
                    showToast("Error al cargar datos");
                }
            });

        // Cargar email del FirebaseUser si existe
        if (currentUser != null && currentUser.getEmail() != null) {
            etEmail.setText(currentUser.getEmail());
        } else {
            String email = UsuarioPreferences.getEmail(this);
            if (email != null) {
                etEmail.setText(email);
            }
        }
    }

    private void mostrarDatosUsuario(Usuario usuario) {
        // Nombre completo
        if (usuario.getNombre() != null) {
            etNombreCompleto.setText(usuario.getNombre());
        }

        // Username (usar email si no hay username)
        String username = usuario.getEmail() != null ?
                usuario.getEmail().split("@")[0] : "";
        etUsername.setText(username);

        // Bio (si existe)
        String bio = UsuarioPreferences.getBio(this);
        if (bio != null && !bio.isEmpty()) {
            etBio.setText(bio);
        }

        // Ubicación
        String ubicacion = UsuarioPreferences.getUbicacion(this);
        if (ubicacion != null && !ubicacion.isEmpty()) {
            etUbicacion.setText(ubicacion);
        } else {
            etUbicacion.setText("Ibarra, Ecuador");
        }

        // Avatar (soporta URL y Base64)
        String avatarUrl = UsuarioPreferences.getFotoPerfil(this);
        com.tesistitulacion.noticiaslocales.utils.ImageHelper.cargarAvatar(this, ivAvatarEdit, avatarUrl);
    }

    private void mostrarOpcionesFoto() {
        // Mostrar diálogo para elegir entre cámara o galería
        String[] opciones = {"Tomar foto", "Elegir de galería"};

        DialogHelper.showListDialog(this, "Cambiar foto de perfil", opciones, (dialog, which) -> {
            if (which == 0) {
                // Cámara
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    abrirCamara();
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            } else {
                // Galería - verificar permisos según versión de Android
                if (tienePermisoGaleria()) {
                    abrirGaleria();
                } else {
                    solicitarPermisoGaleria();
                }
            }
        });
    }

    private boolean tienePermisoGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ usa READ_MEDIA_IMAGES
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 y anteriores usa READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void solicitarPermisoGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private void abrirGaleria() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhotoIntent);
    }

    private void subirImagenAPerfil(Bitmap bitmap) {
        // Verificar autenticación de Firebase
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = UsuarioPreferences.getUserId(this);

        if (currentUser == null) {
            Log.w(TAG, "Usuario no autenticado en Firebase, guardando imagen localmente");
            guardarImagenLocalmente(bitmap);
            return;
        }

        if (userId == null || userId.isEmpty()) {
            userId = currentUser.getUid();
        }

        // Mostrar diálogo de progreso
        mostrarProgressDialog("Subiendo imagen...");

        // Comprimir imagen (reducir calidad para subida más rápida)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();

        Log.d(TAG, "Usuario autenticado: " + currentUser.getEmail());
        Log.d(TAG, "UserId: " + userId);
        Log.d(TAG, "Tamaño de imagen a subir: " + data.length + " bytes");

        // Subir a Firebase Storage
        final String finalUserId = userId;
        StorageReference avatarRef = storageRef.child("avatars/" + userId + ".jpg");

        Log.d(TAG, "Ruta de Storage: " + avatarRef.getPath());

        avatarRef.putBytes(data)
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    if (progressDialog != null) {
                        progressDialog.setMessage("Subiendo imagen... " + (int) progress + "%");
                    }
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Imagen subida exitosamente a Storage");
                    avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String avatarUrl = uri.toString();
                        Log.d(TAG, "URL de imagen obtenida: " + avatarUrl);

                        // Guardar en preferencias locales
                        UsuarioPreferences.guardarFotoPerfil(this, avatarUrl);

                        // Actualizar en Firestore también
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("fotoPerfil", avatarUrl);
                        firebaseManager.actualizarUsuario(finalUserId, updateData, new FirebaseManager.FirestoreCallback<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ocultarProgressDialog();
                                showToast("Foto actualizada correctamente");
                            }

                            @Override
                            public void onError(Exception e) {
                                ocultarProgressDialog();
                                Log.e(TAG, "Error al actualizar foto en Firestore", e);
                                showToast("Foto guardada en Storage");
                            }
                        });
                    }).addOnFailureListener(e -> {
                        ocultarProgressDialog();
                        Log.e(TAG, "Error al obtener URL de descarga", e);
                        showToast("Error al obtener URL");
                    });
                })
                .addOnFailureListener(e -> {
                    ocultarProgressDialog();
                    Log.e(TAG, "Error al subir imagen a Storage: " + e.getClass().getName(), e);

                    String errorMsg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
                    Log.e(TAG, "Mensaje de error: " + errorMsg);

                    // Mostrar error detallado para diagnóstico
                    if (errorMsg.contains("User does not have permission") ||
                        errorMsg.contains("not authorized") ||
                        errorMsg.contains("403")) {
                        DialogHelper.showInfo(this, "Error de permisos",
                            "Las reglas de Firebase Storage no permiten subir archivos.\n\n" +
                            "Solución: Ve a Firebase Console > Storage > Rules y cambia las reglas a:\n\n" +
                            "rules_version = '2';\n" +
                            "service firebase.storage {\n" +
                            "  match /b/{bucket}/o {\n" +
                            "    match /{allPaths=**} {\n" +
                            "      allow read, write: if request.auth != null;\n" +
                            "    }\n" +
                            "  }\n" +
                            "}");
                        // Guardar localmente como alternativa
                        guardarImagenLocalmente(bitmap);
                    } else if (errorMsg.contains("network") || errorMsg.contains("Unable to resolve host")) {
                        showToast("Error de conexión. Verifica tu internet");
                    } else {
                        showToast("Error: " + errorMsg);
                    }
                });
    }

    /**
     * Guarda la imagen localmente como Base64 en SharedPreferences
     * cuando Firebase Storage no está disponible
     */
    private void guardarImagenLocalmente(Bitmap bitmap) {
        mostrarProgressDialog("Guardando imagen localmente...");

        try {
            // Comprimir más para almacenamiento local
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            // Convertir a Base64
            String base64Image = "data:image/jpeg;base64," +
                    android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);

            // Guardar en SharedPreferences
            UsuarioPreferences.guardarFotoPerfil(this, base64Image);

            ocultarProgressDialog();
            showToast("Imagen guardada localmente");
            Log.d(TAG, "Imagen guardada como Base64, tamaño: " + base64Image.length());

        } catch (Exception e) {
            ocultarProgressDialog();
            Log.e(TAG, "Error al guardar imagen localmente", e);
            showToast("Error al guardar imagen");
        }
    }

    private void mostrarProgressDialog(String mensaje) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(mensaje);
        progressDialog.show();
    }

    private void ocultarProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void guardarCambios() {
        String nombreCompleto = etNombreCompleto.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();

        // Validaciones
        if (nombreCompleto.isEmpty()) {
            etNombreCompleto.setError("El nombre es requerido");
            etNombreCompleto.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            etUsername.setError("El nombre de usuario es requerido");
            etUsername.requestFocus();
            return;
        }

        // Guardar en SharedPreferences
        UsuarioPreferences.guardarNombre(this, nombreCompleto);
        UsuarioPreferences.guardarBio(this, bio);
        UsuarioPreferences.guardarUbicacion(this, ubicacion);

        // Actualizar en Firebase
        String userId = UsuarioPreferences.getUserId(this);
        if (userId != null) {
            // Crear Map con los datos a actualizar
            Map<String, Object> datosActualizar = new HashMap<>();
            datosActualizar.put("nombre", nombreCompleto);
            datosActualizar.put("bio", bio);
            datosActualizar.put("ubicacion", ubicacion);

            firebaseManager.actualizarUsuario(userId, datosActualizar, new FirebaseManager.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showToast("Perfil actualizado correctamente");
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Error al actualizar perfil", e);
                    showToast("Error al guardar cambios");
                }
            });
        }
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
