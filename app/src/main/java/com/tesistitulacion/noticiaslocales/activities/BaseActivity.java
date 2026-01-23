package com.tesistitulacion.noticiaslocales.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.utils.FirebaseCallbackHelper;
import com.tesistitulacion.noticiaslocales.utils.LocaleManager;
import com.tesistitulacion.noticiaslocales.utils.LocationHelper;
import com.tesistitulacion.noticiaslocales.utils.ThemeManager;

/**
 * Activity base con navegación inferior reutilizable.
 * Todas las pantallas principales extienden de esta clase.
 *
 * Funcionalidades:
 * - Barra de navegación con 3 secciones: Noticias, Mapa, Perfil
 * - Gestión automática del estado activo/inactivo
 * - Navegación entre activities sin duplicados en stack
 */
public abstract class BaseActivity extends AppCompatActivity {

    // IDs de las secciones de navegación
    public static final int NAV_NOTICIAS = 1;
    public static final int NAV_MAPA = 3;
    public static final int NAV_PERFIL = 4;

    // Views de navegación
    private LinearLayout llNavNoticias, llNavMapa, llNavPerfil;
    private ImageView ivNavNoticias, ivNavMapa, ivNavPerfil;
    private TextView tvNavNoticias, tvNavMapa, tvNavPerfil;
    private View indicatorNoticias, indicatorMapa, indicatorPerfil;

    // Helpers reutilizables (inicialización lazy)
    private LocationHelper locationHelper;
    private FirebaseCallbackHelper.LoadingStateManager loadingStateManager;

    /**
     * Método abstracto que cada activity debe implementar
     * para indicar qué sección está activa
     */
    protected abstract int getNavegacionActiva();

    /**
     * Método para obtener el layout específico de cada activity
     */
    protected abstract int getLayoutResourceId();

    @Override
    protected void attachBaseContext(Context newBase) {
        // Aplicar idioma guardado antes de crear el contexto
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema guardado (por defecto: CLARO)
        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        // Configurar navegación si el layout lo incluye
        configurarNavegacion();
    }

    /**
     * Configura la barra de navegación inferior
     */
    private void configurarNavegacion() {
        // Buscar views de navegación
        llNavNoticias = findViewById(R.id.ll_nav_noticias);
        llNavMapa = findViewById(R.id.ll_nav_mapa);
        llNavPerfil = findViewById(R.id.ll_nav_perfil);

        // Si no hay navegación en este layout, salir
        if (llNavNoticias == null) return;

        ivNavNoticias = findViewById(R.id.iv_nav_noticias);
        ivNavMapa = findViewById(R.id.iv_nav_mapa);
        ivNavPerfil = findViewById(R.id.iv_nav_perfil);

        tvNavNoticias = findViewById(R.id.tv_nav_noticias);
        tvNavMapa = findViewById(R.id.tv_nav_mapa);
        tvNavPerfil = findViewById(R.id.tv_nav_perfil);

        // Indicadores de selección
        indicatorNoticias = findViewById(R.id.indicator_noticias);
        indicatorMapa = findViewById(R.id.indicator_mapa);
        indicatorPerfil = findViewById(R.id.indicator_perfil);

        // Configurar clicks
        llNavNoticias.setOnClickListener(v -> navegarA(NAV_NOTICIAS));
        llNavMapa.setOnClickListener(v -> navegarA(NAV_MAPA));
        llNavPerfil.setOnClickListener(v -> navegarA(NAV_PERFIL));

        // Marcar sección activa
        marcarSeccionActiva();
    }

    /**
     * Marca visualmente la sección activa
     */
    private void marcarSeccionActiva() {
        int seccionActiva = getNavegacionActiva();

        // Obtener colores del tema (se adaptan a modo claro/oscuro)
        android.util.TypedValue typedValue = new android.util.TypedValue();

        // Color para iconos/texto activo (sobre el indicador de color)
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondaryContainer, typedValue, true);
        int colorActivo = typedValue.data;

        // Color para iconos/texto inactivo
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceVariant, typedValue, true);
        int colorInactivo = typedValue.data;

        // Resetear todos a inactivo
        setEstadoNavegacion(ivNavNoticias, tvNavNoticias, indicatorNoticias, colorInactivo, false);
        setEstadoNavegacion(ivNavMapa, tvNavMapa, indicatorMapa, colorInactivo, false);
        setEstadoNavegacion(ivNavPerfil, tvNavPerfil, indicatorPerfil, colorInactivo, false);

        // Marcar activo según sección
        switch (seccionActiva) {
            case NAV_NOTICIAS:
                setEstadoNavegacion(ivNavNoticias, tvNavNoticias, indicatorNoticias, colorActivo, true);
                break;
            case NAV_MAPA:
                setEstadoNavegacion(ivNavMapa, tvNavMapa, indicatorMapa, colorActivo, true);
                break;
            case NAV_PERFIL:
                setEstadoNavegacion(ivNavPerfil, tvNavPerfil, indicatorPerfil, colorActivo, true);
                break;
            default:
                // No hay sección activa
                break;
        }
    }

    /**
     * Establece el estado visual de un botón de navegación con indicador moderno
     */
    private void setEstadoNavegacion(ImageView icon, TextView text, View indicator, int color, boolean activo) {
        if (icon != null) {
            icon.setColorFilter(color);
        }
        if (text != null) {
            text.setTextColor(color);
            text.setTypeface(null, activo ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
        if (indicator != null) {
            // Mostrar/ocultar indicador con animación suave
            if (activo) {
                indicator.setVisibility(View.VISIBLE);
                indicator.setAlpha(0f);
                indicator.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();
            } else {
                indicator.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction(() -> indicator.setVisibility(View.INVISIBLE))
                    .start();
            }
        }
    }

    /**
     * Navega a la sección seleccionada
     */
    private void navegarA(int seccion) {
        // Si ya estamos en esta sección, no hacer nada
        if (seccion == getNavegacionActiva()) {
            return;
        }

        Intent intent = null;

        switch (seccion) {
            case NAV_NOTICIAS:
                intent = new Intent(this, ListaNoticiasActivity.class);
                break;
            case NAV_MAPA:
                intent = new Intent(this, MapaActivity.class);
                break;
            case NAV_PERFIL:
                intent = new Intent(this, PerfilActivity.class);
                break;
            default:
                // Sección no válida, no navegar
                return;
        }

        if (intent != null) {
            // FLAG_ACTIVITY_CLEAR_TOP: Si la activity ya existe, la trae al frente
            // FLAG_ACTIVITY_SINGLE_TOP: No crea duplicados
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

            // Animación de transición suave
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar estado visual al volver a la activity
        marcarSeccionActiva();
    }

    // ============================================================
    // MÉTODOS HELPER PROTEGIDOS PARA SUBCLASES
    // ============================================================

    /**
     * Obtiene el LocationHelper (inicialización lazy)
     * Útil para Activities que necesitan gestión de GPS
     * @return LocationHelper compartido
     */
    protected LocationHelper getLocationHelper() {
        if (locationHelper == null) {
            locationHelper = new LocationHelper(this);
        }
        return locationHelper;
    }

    /**
     * Obtiene el LoadingStateManager (inicialización lazy)
     * Útil para Activities que cargan datos de Firebase
     * @return LoadingStateManager compartido
     */
    protected FirebaseCallbackHelper.LoadingStateManager getLoadingStateManager() {
        if (loadingStateManager == null) {
            loadingStateManager = new FirebaseCallbackHelper.LoadingStateManager();
        }
        return loadingStateManager;
    }

    /**
     * Muestra un Toast de manera consistente
     * @param mensaje Mensaje a mostrar
     * @param duracion Toast.LENGTH_SHORT o Toast.LENGTH_LONG
     */
    protected void showToast(String mensaje, int duracion) {
        Toast.makeText(this, mensaje, duracion).show();
    }

    /**
     * Muestra un Toast corto
     * @param mensaje Mensaje a mostrar
     */
    protected void showToast(String mensaje) {
        showToast(mensaje, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar recursos del LocationHelper si fue creado
        if (locationHelper != null) {
            locationHelper.stopLocationUpdates();
        }
    }
}
