package com.tesistitulacion.noticiaslocales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.tesistitulacion.noticiaslocales.R;

/**
 * Activity base con navegación inferior reutilizable.
 * Todas las pantallas principales extienden de esta clase.
 *
 * Funcionalidades:
 * - Barra de navegación con 4 secciones: Noticias, Eventos, Mapa, Perfil
 * - Gestión automática del estado activo/inactivo
 * - Navegación entre activities sin duplicados en stack
 */
public abstract class BaseActivity extends AppCompatActivity {

    // IDs de las secciones de navegación
    public static final int NAV_NOTICIAS = 1;
    public static final int NAV_EVENTOS = 2;
    public static final int NAV_MAPA = 3;
    public static final int NAV_PERFIL = 4;

    // Views de navegación
    private LinearLayout llNavNoticias, llNavEventos, llNavMapa, llNavPerfil;
    private ImageView ivNavNoticias, ivNavEventos, ivNavMapa, ivNavPerfil;
    private TextView tvNavNoticias, tvNavEventos, tvNavMapa, tvNavPerfil;

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
    protected void onCreate(Bundle savedInstanceState) {
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
        llNavEventos = findViewById(R.id.ll_nav_eventos);
        llNavMapa = findViewById(R.id.ll_nav_mapa);
        llNavPerfil = findViewById(R.id.ll_nav_perfil);

        // Si no hay navegación en este layout, salir
        if (llNavNoticias == null) return;

        ivNavNoticias = findViewById(R.id.iv_nav_noticias);
        ivNavEventos = findViewById(R.id.iv_nav_eventos);
        ivNavMapa = findViewById(R.id.iv_nav_mapa);
        ivNavPerfil = findViewById(R.id.iv_nav_perfil);

        tvNavNoticias = findViewById(R.id.tv_nav_noticias);
        tvNavEventos = findViewById(R.id.tv_nav_eventos);
        tvNavMapa = findViewById(R.id.tv_nav_mapa);
        tvNavPerfil = findViewById(R.id.tv_nav_perfil);

        // Configurar clicks
        llNavNoticias.setOnClickListener(v -> navegarA(NAV_NOTICIAS));
        llNavEventos.setOnClickListener(v -> navegarA(NAV_EVENTOS));
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

        // Color primario para activo, textColorSecondary para inactivo (adaptable a modo oscuro)
        int colorPrimary = ContextCompat.getColor(this, R.color.primary);

        // Obtener color de texto secundario del tema (se adapta a modo claro/oscuro)
        android.util.TypedValue typedValue = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.textColorSecondary, typedValue, true);
        int colorInactivo = typedValue.data;

        // Resetear todos a inactivo
        setEstadoNavegacion(ivNavNoticias, tvNavNoticias, colorInactivo, false);
        setEstadoNavegacion(ivNavEventos, tvNavEventos, colorInactivo, false);
        setEstadoNavegacion(ivNavMapa, tvNavMapa, colorInactivo, false);
        setEstadoNavegacion(ivNavPerfil, tvNavPerfil, colorInactivo, false);

        // Marcar activo según sección
        switch (seccionActiva) {
            case NAV_NOTICIAS:
                setEstadoNavegacion(ivNavNoticias, tvNavNoticias, colorPrimary, true);
                break;
            case NAV_EVENTOS:
                setEstadoNavegacion(ivNavEventos, tvNavEventos, colorPrimary, true);
                break;
            case NAV_MAPA:
                setEstadoNavegacion(ivNavMapa, tvNavMapa, colorPrimary, true);
                break;
            case NAV_PERFIL:
                setEstadoNavegacion(ivNavPerfil, tvNavPerfil, colorPrimary, true);
                break;
        }
    }

    /**
     * Establece el estado visual de un botón de navegación
     */
    private void setEstadoNavegacion(ImageView icon, TextView text, int color, boolean activo) {
        if (icon != null) {
            icon.setColorFilter(color);
        }
        if (text != null) {
            text.setTextColor(color);
            text.setTypeface(null, activo ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
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
            case NAV_EVENTOS:
                intent = new Intent(this, ListaEventosActivity.class);
                break;
            case NAV_MAPA:
                intent = new Intent(this, MapaActivity.class);
                break;
            case NAV_PERFIL:
                intent = new Intent(this, PerfilActivity.class);
                break;
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
}
