package com.tesistitulacion.noticiaslocales.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.utils.NotificationHelper;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

/**
 * Activity para configurar las preferencias de notificaciones
 * Estilo GeoNews - Material Design 3
 *
 * Incluye:
 * - Verificación de permiso POST_NOTIFICATIONS (Android 13+)
 * - Configuración de tipos de notificaciones
 * - Enlace a ajustes del sistema
 */
public class NotificacionesActivity extends BaseActivity {
    private static final String TAG = "NotificacionesActivity";

    private ImageView btnVolver;
    private SwitchMaterial switchNotificaciones;
    private SwitchMaterial switchNoticias;
    private SwitchMaterial switchDestacadas;
    private SwitchMaterial switchCercanas;
    private LinearLayout layoutConfiguracion;

    // Elementos de estado del permiso
    private LinearLayout layoutPermisoRequerido;
    private TextView tvEstadoPermiso;
    private MaterialButton btnSolicitarPermiso;

    private NotificationHelper notificationHelper;

    @Override
    protected int getNavegacionActiva() {
        return -1; // No mostrar navegación inferior
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_notificaciones;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationHelper = NotificationHelper.getInstance(this);

        inicializarVistas();
        verificarPermisoNotificaciones();
        cargarPreferencias();
        configurarListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estado del permiso al volver de ajustes del sistema
        verificarPermisoNotificaciones();
    }

    private void inicializarVistas() {
        btnVolver = findViewById(R.id.btn_volver);
        switchNotificaciones = findViewById(R.id.switch_notificaciones);
        switchNoticias = findViewById(R.id.switch_noticias);
        switchDestacadas = findViewById(R.id.switch_destacadas);
        switchCercanas = findViewById(R.id.switch_cercanas);
        layoutConfiguracion = findViewById(R.id.layout_configuracion);

        // Elementos de permiso (pueden no existir si el layout no los tiene)
        layoutPermisoRequerido = findViewById(R.id.layout_permiso_requerido);
        tvEstadoPermiso = findViewById(R.id.tv_estado_permiso);
        btnSolicitarPermiso = findViewById(R.id.btn_solicitar_permiso);
    }

    /**
     * Verifica el estado del permiso de notificaciones y actualiza la UI
     */
    private void verificarPermisoNotificaciones() {
        boolean tienePermiso = notificationHelper.tienePermisoNotificaciones();
        boolean habilitadoEnSistema = notificationHelper.notificacionesHabilitadasEnSistema();

        Log.d(TAG, "Permiso notificaciones: " + tienePermiso + ", Habilitado en sistema: " + habilitadoEnSistema);

        // Actualizar UI según el estado del permiso
        if (layoutPermisoRequerido != null) {
            if (!tienePermiso || !habilitadoEnSistema) {
                layoutPermisoRequerido.setVisibility(View.VISIBLE);
                if (tvEstadoPermiso != null) {
                    if (!tienePermiso) {
                        tvEstadoPermiso.setText("Se requiere permiso para mostrar notificaciones");
                    } else {
                        tvEstadoPermiso.setText("Las notificaciones están desactivadas en el sistema");
                    }
                }
            } else {
                layoutPermisoRequerido.setVisibility(View.GONE);
            }
        }

        // Habilitar/deshabilitar switches según el permiso
        boolean switchesHabilitados = tienePermiso && habilitadoEnSistema;
        if (switchNotificaciones != null) {
            switchNotificaciones.setEnabled(switchesHabilitados);
        }
    }

    private void cargarPreferencias() {
        // Cargar el estado de las notificaciones desde SharedPreferences
        boolean notificacionesActivas = UsuarioPreferences.getNotificacionesActivas(this);
        if (switchNotificaciones != null) {
            switchNotificaciones.setChecked(notificacionesActivas);
        }

        // Mostrar/ocultar opciones según el estado
        actualizarVisibilidadOpciones(notificacionesActivas);

        // Cargar preferencias específicas de cada tipo de notificación
        if (switchNoticias != null) {
            boolean noticiasActivas = UsuarioPreferences.getNotificacionesNoticias(this);
            switchNoticias.setChecked(noticiasActivas);
        }

        if (switchDestacadas != null) {
            boolean destacadasActivas = UsuarioPreferences.getNotificacionesDestacadas(this);
            switchDestacadas.setChecked(destacadasActivas);
        }

        // Switch de noticias cercanas (si existe)
        if (switchCercanas != null) {
            // Por defecto activadas
            switchCercanas.setChecked(true);
        }
    }

    private void configurarListeners() {
        // Botón volver
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        // Botón solicitar permiso
        if (btnSolicitarPermiso != null) {
            btnSolicitarPermiso.setOnClickListener(v -> {
                if (!notificationHelper.tienePermisoNotificaciones()) {
                    // Solicitar permiso
                    notificationHelper.solicitarPermisoNotificaciones(this);
                } else {
                    // Ya tiene permiso pero está desactivado en sistema, abrir ajustes
                    notificationHelper.abrirAjustesNotificaciones(this);
                }
            });
        }

        // Switch principal de notificaciones
        if (switchNotificaciones != null) {
            switchNotificaciones.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Verificar permiso antes de activar
                if (isChecked && !notificationHelper.tienePermisoNotificaciones()) {
                    buttonView.setChecked(false);
                    notificationHelper.solicitarPermisoNotificaciones(this);
                    return;
                }

                UsuarioPreferences.guardarNotificacionesActivas(this, isChecked);
                actualizarVisibilidadOpciones(isChecked);
                showToast(isChecked ? "Notificaciones activadas" : "Notificaciones desactivadas");
            });
        }

        // Switch de noticias
        if (switchNoticias != null) {
            switchNoticias.setOnCheckedChangeListener((buttonView, isChecked) -> {
                UsuarioPreferences.guardarNotificacionesNoticias(this, isChecked);
                showToast(isChecked ? "Notificaciones de noticias activadas" : "Notificaciones de noticias desactivadas");
            });
        }

        // Switch de destacadas
        if (switchDestacadas != null) {
            switchDestacadas.setOnCheckedChangeListener((buttonView, isChecked) -> {
                UsuarioPreferences.guardarNotificacionesDestacadas(this, isChecked);
                showToast(isChecked ? "Notificaciones destacadas activadas" : "Notificaciones destacadas desactivadas");
            });
        }

        // Switch de noticias cercanas
        if (switchCercanas != null) {
            switchCercanas.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guardar preferencia (puedes agregar el método en UsuarioPreferences)
                showToast(isChecked ? "Notificaciones de noticias cercanas activadas" : "Notificaciones de noticias cercanas desactivadas");
            });
        }
    }

    /**
     * Muestra u oculta las opciones de configuración según el estado de notificaciones
     */
    private void actualizarVisibilidadOpciones(boolean notificacionesActivas) {
        if (layoutConfiguracion != null) {
            layoutConfiguracion.setVisibility(notificacionesActivas ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NotificationHelper.REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso de notificaciones CONCEDIDO");
                showToast("Permiso concedido");

                // Actualizar UI
                verificarPermisoNotificaciones();

                // Activar el switch principal automáticamente
                if (switchNotificaciones != null) {
                    switchNotificaciones.setEnabled(true);
                    switchNotificaciones.setChecked(true);
                }
            } else {
                Log.w(TAG, "Permiso de notificaciones DENEGADO");
                showToast("Permiso denegado. Puedes activarlo en Ajustes");
                verificarPermisoNotificaciones();
            }
        }
    }
}
