package com.tesistitulacion.noticiaslocales.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Usuario;
import com.tesistitulacion.noticiaslocales.utils.DialogHelper;
import com.tesistitulacion.noticiaslocales.utils.LocaleManager;
import com.tesistitulacion.noticiaslocales.utils.ThemeManager;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

/**
 * Activity de Ajustes/Settings - Versión GeoNews
 * Pantalla de configuración del usuario
 */
public class AjustesActivity extends AppCompatActivity {

    private static final String TAG = "AjustesActivity";

    // Constantes para literales
    private static final String BTN_CANCELAR = "BTN_CANCELAR";
    private static final String FRECUENCIA_DAILY = "daily";
    private static final String FRECUENCIA_WEEKLY = "weekly";

    // Views - Header
    private TextView btnDone;

    // Views - Profile Section
    private ImageView ivAvatarSettings;
    private FloatingActionButton fabEditAvatar;
    private TextView tvNombreSettings;
    private TextView tvUbicacionSettings;

    // Views - Account Section
    private LinearLayout btnEditProfile;
    private LinearLayout btnMyLocations;
    private LinearLayout btnSecurityPrivacy;

    // Views - Content Section
    private LinearLayout btnNewsCategories;
    private TextView tvCategoriesSelected;
    private LinearLayout btnLanguage;
    private TextView tvLanguageSelected;
    private LinearLayout btnTheme;
    private TextView tvThemeSelected;

    // Views - Notifications Section
    private SwitchMaterial switchPushNotifications;
    private SwitchMaterial switchEmailDigest;

    // Views - Support Section
    private LinearLayout btnHelpCenter;
    private LinearLayout btnAbout;

    // Views - Logout
    private MaterialButton btnLogoutSettings;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Aplicar idioma guardado
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema
        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        inicializarVistas();
        cargarDatosUsuario();
        configurarListeners();
    }

    private void inicializarVistas() {
        // Header
        btnDone = findViewById(R.id.btn_done);

        // Profile Section
        ivAvatarSettings = findViewById(R.id.iv_avatar_settings);
        fabEditAvatar = findViewById(R.id.fab_edit_avatar);
        tvNombreSettings = findViewById(R.id.tv_nombre_settings);
        tvUbicacionSettings = findViewById(R.id.tv_ubicacion_settings);

        // Account Section
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnMyLocations = findViewById(R.id.btn_my_locations);
        btnSecurityPrivacy = findViewById(R.id.btn_security_privacy);

        // Content Section
        btnNewsCategories = findViewById(R.id.btn_news_categories);
        tvCategoriesSelected = findViewById(R.id.tv_categories_selected);
        btnLanguage = findViewById(R.id.btn_language);
        tvLanguageSelected = findViewById(R.id.tv_language_selected);
        btnTheme = findViewById(R.id.btn_theme);
        tvThemeSelected = findViewById(R.id.tv_theme_selected);

        // Notifications Section
        switchPushNotifications = findViewById(R.id.switch_push_notifications);
        switchEmailDigest = findViewById(R.id.switch_email_digest);

        // Support Section
        btnHelpCenter = findViewById(R.id.btn_help_center);
        btnAbout = findViewById(R.id.btn_about);

        // Logout
        btnLogoutSettings = findViewById(R.id.btn_logout_settings);
    }

    private void cargarDatosUsuario() {
        // Cargar datos locales
        String nombre = UsuarioPreferences.getNombre(this);
        String apellido = UsuarioPreferences.getApellido(this);
        String email = UsuarioPreferences.getEmail(this);
        String fotoPerfil = UsuarioPreferences.getFotoPerfil(this);
        String ubicacion = UsuarioPreferences.getUbicacion(this);

        // Mostrar nombre
        if (nombre != null && apellido != null) {
            tvNombreSettings.setText(nombre + " " + apellido);
        } else if (nombre != null) {
            tvNombreSettings.setText(nombre);
        } else if (email != null) {
            tvNombreSettings.setText(email.split("@")[0]);
        }

        // Mostrar ubicación
        if (ubicacion != null && !ubicacion.isEmpty()) {
            tvUbicacionSettings.setText(ubicacion);
        } else {
            tvUbicacionSettings.setText("Ibarra, Ecuador");
        }

        // Cargar foto de perfil (soporta URL y Base64)
        com.tesistitulacion.noticiaslocales.utils.ImageHelper.cargarAvatar(this, ivAvatarSettings, fotoPerfil);

        // Cargar estados de switches
        boolean notificacionesActivas = UsuarioPreferences.getNotificacionesActivas(this);
        switchPushNotifications.setChecked(notificacionesActivas);

        // Email digest por defecto desactivado
        switchEmailDigest.setChecked(false);

        // Mostrar idioma actual
        String currentLanguageName = com.tesistitulacion.noticiaslocales.utils.LocaleManager.getCurrentLanguageName(this);
        tvLanguageSelected.setText(currentLanguageName);

        // Mostrar tema actual
        if (tvThemeSelected != null) {
            String currentTheme = ThemeManager.getCurrentThemeName(this);
            tvThemeSelected.setText(currentTheme);
        }
    }

    private void configurarListeners() {
        // Botón Done - cerrar actividad
        btnDone.setOnClickListener(v -> finish());

        // FAB editar avatar - ir a editar perfil
        fabEditAvatar.setOnClickListener(v -> abrirEditarPerfil());

        // ACCOUNT SECTION
        btnEditProfile.setOnClickListener(v -> abrirEditarPerfil());

        btnMyLocations.setOnClickListener(v -> {
            mostrarDialogoUbicaciones();
        });

        btnSecurityPrivacy.setOnClickListener(v -> {
            mostrarDialogoSeguridad();
        });

        // CONTENT SECTION
        btnNewsCategories.setOnClickListener(v -> {
            mostrarDialogoCategorias();
        });

        btnLanguage.setOnClickListener(v -> {
            mostrarDialogoIdioma();
        });

        // Theme button
        if (btnTheme != null) {
            btnTheme.setOnClickListener(v -> {
                mostrarDialogoTema();
            });
        }

        // NOTIFICATIONS SECTION
        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            UsuarioPreferences.guardarNotificacionesActivas(this, isChecked);

            if (isChecked) {
                // Suscribirse a topic general de noticias
                suscribirseANotificaciones();
                showToast("Notificaciones activadas");
            } else {
                // Desuscribirse de topics
                desuscribirseDeNotificaciones();
                showToast("Notificaciones desactivadas");
            }
        });

        switchEmailDigest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Mostrar diálogo de configuración
                mostrarDialogoConfiguracionEmailDigest();
            } else {
                // Desactivar email digest
                UsuarioPreferences.guardarEmailDigestActivado(this, false);
                cancelarEmailDigestWorker();
                showToast("Resumen por email desactivado");
            }
        });

        // SUPPORT SECTION
        btnHelpCenter.setOnClickListener(v -> {
            mostrarDialogoCentroAyuda();
        });

        btnAbout.setOnClickListener(v -> {
            mostrarDialogoAcercaDe();
        });

        // LOGOUT
        btnLogoutSettings.setOnClickListener(v -> {
            mostrarDialogoCerrarSesion();
        });
    }

    private void abrirEditarPerfil() {
        Intent intent = new Intent(this, EditarPerfilActivity.class);
        startActivity(intent);
    }

    private void mostrarDialogoIdioma() {
        String[] idiomas = LocaleManager.getAvailableLanguages();
        String[] languageCodes = LocaleManager.getAvailableLanguageCodes();
        int seleccionActual = LocaleManager.getCurrentLanguageIndex(this);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.language_select));
        builder.setSingleChoiceItems(idiomas, seleccionActual, (dialog, which) -> {
            // Cambiar idioma
            LocaleManager.changeLanguage(this, languageCodes[which]);
            dialog.dismiss();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void mostrarDialogoTema() {
        String[] temas = {
            getString(R.string.settings_theme_auto),
            getString(R.string.settings_theme_light),
            getString(R.string.settings_theme_dark)
        };

        int seleccionActual = ThemeManager.getCurrentThemeIndex(this);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.settings_theme));
        builder.setSingleChoiceItems(temas, seleccionActual, (dialog, which) -> {
            // Cambiar tema
            ThemeManager.setTheme(this, which);
            if (tvThemeSelected != null) {
                tvThemeSelected.setText(temas[which]);
            }
            dialog.dismiss();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void mostrarDialogoAcercaDe() {
        DialogHelper.showInfo(this, "Acerca de GeoNews",
                "GeoNews - Noticias Locales de Ibarra\n\n" +
                "Versión: 0.1.0\n" +
                "Build: 2024\n\n" +
                "Desarrollado como proyecto de titulación\n" +
                "Universidad Técnica del Norte\n\n" +
                "© 2024 GeoNews");
    }

    private void mostrarDialogoUbicaciones() {
        String[] opciones = {
            "Ubicación Actual",
            "Ibarra Centro",
            "San Antonio",
            "Yahuarcocha",
            "Gestionar ubicaciones favoritas"
        };

        DialogHelper.showListDialog(this, "Mis Ubicaciones", opciones, (dialog, which) -> {
            if (which == 4) {
                // Gestionar ubicaciones favoritas
                showToast("Gestionar ubicaciones favoritas");
                // Aquí se podría abrir otra actividad para gestionar ubicaciones
            } else {
                // Seleccionar ubicación
                String ubicacionSeleccionada = opciones[which];
                UsuarioPreferences.guardarUbicacion(this, ubicacionSeleccionada);
                tvUbicacionSettings.setText(ubicacionSeleccionada);
                showToast("Ubicación cambiada a: " + ubicacionSeleccionada);
            }
            dialog.dismiss();
        });
    }

    private void mostrarDialogoSeguridad() {
        String[] opciones = {
            "Cambiar Contraseña",
            "Privacidad de la Cuenta",
            "Datos y Permisos",
            "Actividad de la Cuenta"
        };

        DialogHelper.showListDialog(this, "Seguridad y Privacidad", opciones, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Cambiar contraseña
                    mostrarDialogoCambiarPassword();
                    break;
                case 1:
                    // Privacidad
                    DialogHelper.showInfo(this, "Privacidad de la Cuenta",
                            "Tu perfil es visible solo para usuarios registrados.\n\n" +
                            "Tus datos están encriptados y protegidos.\n\n" +
                            "Puedes controlar qué información compartes en Editar Perfil.");
                    break;
                case 2:
                    // Datos y permisos
                    DialogHelper.showInfo(this, "Datos y Permisos",
                            "Permisos activos:\n" +
                            "• Ubicación (para noticias locales)\n" +
                            "• Almacenamiento (para guardar artículos)\n" +
                            "• Internet (para contenido)\n\n" +
                            "Puedes gestionar permisos en la configuración del sistema.");
                    break;
                case 3:
                    // Actividad
                    DialogHelper.showInfo(this, "Actividad de la Cuenta",
                            "Última actividad: Hoy\n\n" +
                            "Dispositivos activos: 1\n\n" +
                            "No se detectó actividad inusual.");
                    break;
            }
            dialog.dismiss();
        });
    }

    private void mostrarDialogoCambiarPassword() {
        DialogHelper.showConfirmationDialog(this,
                "Cambiar Contraseña",
                "Se enviará un correo de recuperación a tu email registrado para cambiar tu contraseña de forma segura.",
                "Enviar correo",
                "BTN_CANCELAR",
                () -> {
                    // Aquí se enviaría el correo de recuperación
                    String email = UsuarioPreferences.getEmail(this);
                    if (email != null && !email.isEmpty()) {
                        showToast("Correo de recuperación enviado a " + email);
                    } else {
                        showToast("No se encontró email registrado");
                    }
                });
    }

    private void mostrarDialogoCategorias() {
        String[] categorias = {
            "Política",
            "Economía",
            "Deportes",
            "Cultura",
            "Educación",
            "Salud",
            "Seguridad",
            "Medio Ambiente",
            "Turismo",
            "Tecnología"
        };

        // Obtener categorías seleccionadas actuales
        boolean[] seleccionadas = new boolean[categorias.length];
        for (int i = 0; i < categorias.length; i++) {
            seleccionadas[i] = true; // Por defecto todas seleccionadas
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Categorías");
        builder.setMultiChoiceItems(categorias, seleccionadas, (dialog, which, isChecked) -> {
            seleccionadas[which] = isChecked;
        });

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            // Contar categorías seleccionadas
            int count = 0;
            for (boolean sel : seleccionadas) {
                if (sel) count++;
            }

            if (count == categorias.length) {
                tvCategoriesSelected.setText("Todas");
            } else if (count == 0) {
                tvCategoriesSelected.setText("Ninguna");
            } else {
                tvCategoriesSelected.setText(count + " seleccionadas");
            }

            showToast(count + " categorías seleccionadas");
        });

        builder.setNegativeButton("BTN_CANCELAR", null);
        builder.create().show();
    }

    private void mostrarDialogoCentroAyuda() {
        String[] opciones = {
            "Preguntas Frecuentes",
            "Cómo usar GeoNews",
            "Reportar un problema",
            "Contactar Soporte"
        };

        DialogHelper.showListDialog(this, "Centro de Ayuda", opciones, (dialog, which) -> {
            switch (which) {
                case 0:
                    // FAQ
                    DialogHelper.showInfo(this, "Preguntas Frecuentes",
                            "¿Cómo guardar noticias?\n" +
                            "Toca el ícono de estrella en cualquier noticia.\n\n" +
                            "¿Cómo cambiar mi ubicación?\n" +
                            "Ve a Ajustes > Mis Ubicaciones.\n\n" +
                            "¿Cómo recibir notificaciones?\n" +
                            "Activa notificaciones en Ajustes.\n\n" +
                            "¿Cómo editar mi perfil?\n" +
                            "Ve a Perfil > Editar Perfil.");
                    break;
                case 1:
                    // Cómo usar
                    DialogHelper.showInfo(this, "Cómo usar GeoNews",
                            "1. Explora noticias locales en la pestaña Noticias\n\n" +
                            "2. Visualiza noticias en el Mapa\n\n" +
                            "3. Guarda artículos de interés tocando la estrella\n\n" +
                            "4. Personaliza tu perfil y preferencias en Ajustes");
                    break;
                case 2:
                    // Reportar problema
                    DialogHelper.showConfirmationDialog(this,
                            "Reportar Problema",
                            "¿Deseas reportar un problema técnico o de contenido inapropiado?",
                            "Problema técnico",
                            "Contenido inapropiado",
                            () -> showToast("Gracias por tu reporte. Nuestro equipo lo revisará."));
                    break;
                case 3:
                    // Contactar soporte
                    DialogHelper.showInfo(this, "Contactar Soporte",
                            "Puedes contactarnos en:\n\n" +
                            "Email: soporte@geonews.ec\n\n" +
                            "Teléfono: (06) 123-4567\n\n" +
                            "Horario: Lun-Vie 9am-6pm\n\n" +
                            "Responderemos en menos de 24 horas.");
                    break;
            }
            dialog.dismiss();
        });
    }

    private void mostrarDialogoCerrarSesion() {
        DialogHelper.showConfirmationDialog(this,
                "Cerrar Sesión",
                "¿Estás seguro que deseas cerrar sesión?",
                "Sí, cerrar sesión",
                "BTN_CANCELAR",
                this::cerrarSesion);
    }

    private void cerrarSesion() {
        // Limpiar preferencias
        UsuarioPreferences.cerrarSesion(this);

        showToast("Sesión cerrada");

        // Ir a LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cuando vuelve de editar perfil
        cargarDatosUsuario();
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

    // ==================== NOTIFICACIONES FCM ====================

    /**
     * Suscribe al usuario a los topics de notificaciones
     */
    private void suscribirseANotificaciones() {
        // Topic general de noticias
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Suscrito a topic 'all'");
                    } else {
                        Log.e(TAG, "Error al suscribirse a topic 'all'", task.getException());
                    }
                });

        // Topic de noticias
        FirebaseMessaging.getInstance().subscribeToTopic("noticias")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Suscrito a topic 'noticias'");
                    }
                });

        // Topic de noticias destacadas
        FirebaseMessaging.getInstance().subscribeToTopic("destacadas")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Suscrito a topic 'destacadas'");
                    }
                });

        // Obtener y guardar el token FCM
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        UsuarioPreferences.guardarFCMToken(this, token);
                        Log.d(TAG, "Token FCM obtenido: " + token);
                    } else {
                        Log.e(TAG, "Error al obtener token FCM", task.getException());
                    }
                });
    }

    /**
     * Desuscribe al usuario de los topics de notificaciones
     */
    private void desuscribirseDeNotificaciones() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Desuscrito de topic 'all'");
                    }
                });

        FirebaseMessaging.getInstance().unsubscribeFromTopic("noticias")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Desuscrito de topic 'noticias'");
                    }
                });

        FirebaseMessaging.getInstance().unsubscribeFromTopic("destacadas")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Desuscrito de topic 'destacadas'");
                    }
                });
    }

    // ============================================================
    // EMAIL DIGEST CONFIGURATION
    // ============================================================

    /**
     * Muestra diálogo de configuración de email digest
     */
    private void mostrarDialogoConfiguracionEmailDigest() {
        String[] opciones = {"Diario (cada día)", "Semanal (cada lunes)"};
        String[] frecuencias = {FRECUENCIA_DAILY, FRECUENCIA_WEEKLY};

        // Obtener frecuencia actual
        String frecuenciaActual = UsuarioPreferences.getEmailDigestFrecuencia(this);
        int seleccionActual = frecuenciaActual.equals(FRECUENCIA_WEEKLY) ? 1 : 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Configurar resumen por email");
        builder.setMessage("Recibirás un resumen de las noticias más importantes en tu correo");

        builder.setSingleChoiceItems(opciones, seleccionActual, (dialog, which) -> {
            // Guardar configuración
            UsuarioPreferences.guardarEmailDigestActivado(this, true);
            UsuarioPreferences.guardarEmailDigestFrecuencia(this, frecuencias[which]);

            // Programar WorkManager
            programarEmailDigestWorker(frecuencias[which]);

            String mensaje = frecuencias[which].equals(FRECUENCIA_DAILY)
                ? "Recibirás un resumen diario a las 8:00 AM"
                : "Recibirás un resumen cada lunes a las 8:00 AM";

            showToast(mensaje);
            dialog.dismiss();
        });

        builder.setNegativeButton("BTN_CANCELAR", (dialog, which) -> {
            // Revertir switch si cancela
            switchEmailDigest.setChecked(false);
            dialog.dismiss();
        });

        builder.show();
    }

    /**
     * Programa el Worker de email digest según la frecuencia
     */
    private void programarEmailDigestWorker(String frecuencia) {
        try {
            // Importar WorkManager dinámicamente
            Class<?> workManagerClass = Class.forName("androidx.work.WorkManager");
            Class<?> periodicWorkRequestClass = Class.forName("androidx.work.PeriodicWorkRequest");
            Class<?> constraintsClass = Class.forName("androidx.work.Constraints");
            Class<?> existingPeriodicWorkPolicyClass = Class.forName("androidx.work.ExistingPeriodicWorkPolicy");
            Class<?> timeUnitClass = Class.forName("java.util.concurrent.TimeUnit");

            // Obtener instancia de WorkManager
            Object workManager = workManagerClass.getMethod("getInstance", Context.class).invoke(null, this);

            // Crear Constraints (requiere conexión de red)
            Object constraintsBuilder = constraintsClass.getMethod("Builder").invoke(null);
            Class<?> networkTypeClass = Class.forName("androidx.work.NetworkType");
            Object networkConnected = networkTypeClass.getField("CONNECTED").get(null);

            constraintsBuilder.getClass().getMethod("setRequiredNetworkType", networkTypeClass)
                .invoke(constraintsBuilder, networkConnected);
            Object constraints = constraintsBuilder.getClass().getMethod("build").invoke(constraintsBuilder);

            // Determinar intervalo según frecuencia
            long intervalo = frecuencia.equals(FRECUENCIA_DAILY) ? 1 : 7; // días
            Object timeUnit = timeUnitClass.getField("DAYS").get(null);

            // Crear PeriodicWorkRequest
            Object periodicWorkRequestBuilder = periodicWorkRequestClass.getMethod("Builder",
                Class.class, long.class, timeUnitClass)
                .invoke(null,
                    Class.forName("com.tesistitulacion.noticiaslocales.workers.EmailDigestWorker"),
                    intervalo,
                    timeUnit);

            periodicWorkRequestBuilder.getClass().getMethod("setConstraints", constraintsClass)
                .invoke(periodicWorkRequestBuilder, constraints);

            Object workRequest = periodicWorkRequestBuilder.getClass().getMethod("build")
                .invoke(periodicWorkRequestBuilder);

            // Obtener política REPLACE
            Object replacePolicy = existingPeriodicWorkPolicyClass.getField("REPLACE").get(null);

            // Encolar trabajo
            workManagerClass.getMethod("enqueueUniquePeriodicWork",
                String.class, existingPeriodicWorkPolicyClass, periodicWorkRequestClass)
                .invoke(workManager, "email_digest_worker", replacePolicy, workRequest);

            Log.i(TAG, "Email digest worker programado: " + frecuencia);

        } catch (Exception e) {
            Log.e(TAG, "Error al programar email digest worker", e);
            showToast("Error al programar resumen por email");
        }
    }

    /**
     * Cancela el Worker de email digest
     */
    private void cancelarEmailDigestWorker() {
        try {
            // Importar WorkManager dinámicamente
            Class<?> workManagerClass = Class.forName("androidx.work.WorkManager");

            // Obtener instancia de WorkManager
            Object workManager = workManagerClass.getMethod("getInstance", Context.class)
                .invoke(null, this);

            // BTN_CANCELAR trabajo único
            workManagerClass.getMethod("cancelUniqueWork", String.class)
                .invoke(workManager, "email_digest_worker");

            Log.i(TAG, "Email digest worker cancelado");

        } catch (Exception e) {
            Log.e(TAG, "Error al cancelar email digest worker", e);
        }
    }
}
