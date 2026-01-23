package com.tesistitulacion.noticiaslocales.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;
import com.tesistitulacion.noticiaslocales.utils.AnimationHelper;
import com.tesistitulacion.noticiaslocales.utils.LocaleManager;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity que muestra el detalle completo de una noticia
 * Incluye mini-mapa para mostrar la ubicación de la noticia
 */
public class DetalleNoticiaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DetalleNoticiaActivity";
    public static final String EXTRA_NOTICIA_ID = "noticia_id";
    private static final float ZOOM_MAPA_DETALLE = 15f;

    private String noticiaId;
    private Noticia noticia;

    // Views
    private ImageView ivPortada;
    private TextView tvCategoria;
    private TextView tvTitulo;
    private TextView tvFecha;
    private TextView tvUbicacion;
    private TextView tvDescripcion;
    private TextView tvContenido;
    private TextView tvVisualizaciones;
    private TextView tvEstado;
    private TextView tvAutor;
    private MaterialButton btnVerMapa;

    // Botones flotantes
    private ImageView btnBack;
    private ImageView btnBookmark;
    private ImageView btnShare;

    // Views para contenido enriquecido
    private LinearLayout layoutCitaDestacada;
    private TextView tvCitaDestacada;
    private LinearLayout layoutImpactoComunitario;
    private TextView tvImpactoComunitario;
    private TextView tvHashtags;

    // Views para mini-mapa
    private LinearLayout layoutUbicacionMapa;
    private TextView tvDireccionMapa;
    private ImageView btnExpandirMapa;
    private GoogleMap miniMapa;
    private SupportMapFragment mapFragment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate iniciado");

        try {
            setContentView(R.layout.activity_detalle_noticia);
            Log.d(TAG, "Layout inflado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inflar layout", e);
            showToast("Error al cargar la vista: " + e.getMessage(), Toast.LENGTH_LONG);
            finish();
            return;
        }

        // Obtener ID de la noticia
        noticiaId = getIntent().getStringExtra(EXTRA_NOTICIA_ID);
        Log.d(TAG, "Noticia ID recibido: " + noticiaId);

        if (noticiaId == null) {
            showToast("Error: No se especificó la noticia");
            finish();
            return;
        }

        try {
            inicializarVistas();
            Log.d(TAG, "Vistas inicializadas");

            configurarBotones();
            Log.d(TAG, "Botones configurados");

            cargarNoticia();
            Log.d(TAG, "Carga de noticia iniciada");
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate", e);
            showToast("Error: " + e.getMessage(), Toast.LENGTH_LONG);
            finish();
        }
    }

    private void inicializarVistas() {
        ivPortada = findViewById(R.id.iv_portada);
        tvCategoria = findViewById(R.id.tv_categoria);
        tvTitulo = findViewById(R.id.tv_titulo);
        tvFecha = findViewById(R.id.tv_fecha);
        tvUbicacion = findViewById(R.id.tv_ubicacion);
        tvDescripcion = findViewById(R.id.tv_descripcion);
        tvContenido = findViewById(R.id.tv_contenido);
        tvVisualizaciones = findViewById(R.id.tv_visualizaciones);
        tvEstado = findViewById(R.id.tv_estado);
        tvAutor = findViewById(R.id.tv_autor);
        btnVerMapa = findViewById(R.id.btn_ver_mapa);

        // Botones flotantes
        btnBack = findViewById(R.id.btn_back);
        btnBookmark = findViewById(R.id.btn_bookmark);
        btnShare = findViewById(R.id.btn_share);

        // Views para contenido enriquecido
        layoutCitaDestacada = findViewById(R.id.layout_cita_destacada);
        tvCitaDestacada = findViewById(R.id.tv_cita_destacada);
        layoutImpactoComunitario = findViewById(R.id.layout_impacto_comunitario);
        tvImpactoComunitario = findViewById(R.id.tv_impacto_comunitario);
        tvHashtags = findViewById(R.id.tv_hashtags);

        // Views para mini-mapa
        layoutUbicacionMapa = findViewById(R.id.layout_ubicacion_mapa);
        tvDireccionMapa = findViewById(R.id.tv_direccion_mapa);
        btnExpandirMapa = findViewById(R.id.btn_expandir_mapa);
    }

    private void configurarBotones() {
        // Botón volver
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }

        // Botón bookmark/guardar
        if (btnBookmark != null) {
            btnBookmark.setOnClickListener(v -> toggleGuardarNoticia());
        }

        // Botón compartir
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                if (noticia != null) {
                    compartirNoticia();
                }
            });
        }

        // Botón ver en mapa (oculto por defecto)
        btnVerMapa.setOnClickListener(v -> abrirMapaCompleto());

        // Ocultar botón expandir - el mapa solo muestra la ubicación
        if (btnExpandirMapa != null) {
            btnExpandirMapa.setVisibility(android.view.View.GONE);
        }
    }

    /**
     * Abre el mapa completo centrándose en la ubicación de la noticia
     */
    private void abrirMapaCompleto() {
        if (noticia != null && noticia.getLatitud() != null && noticia.getLongitud() != null) {
            Intent intent = new Intent(this, MapaActivity.class);
            intent.putExtra("latitud", noticia.getLatitud());
            intent.putExtra("longitud", noticia.getLongitud());
            intent.putExtra("titulo", noticia.getTitulo());
            startActivity(intent);
        } else {
            showToast("Esta noticia no tiene ubicación");
        }
    }

    /**
     * Comparte la noticia usando el Intent de compartir nativo de Android
     */
    private void compartirNoticia() {
        String textoCompartir = noticia.getTitulo() + "\n\n" + noticia.getDescripcion();

        if (noticia.getImagenUrl() != null && !noticia.getImagenUrl().isEmpty()) {
            textoCompartir += "\n\n" + noticia.getImagenUrl();
        }

        Intent intentCompartir = new Intent(Intent.ACTION_SEND);
        intentCompartir.setType("text/plain");
        intentCompartir.putExtra(Intent.EXTRA_SUBJECT, "GeoNews: " + noticia.getTitulo());
        intentCompartir.putExtra(Intent.EXTRA_TEXT, textoCompartir);

        try {
            startActivity(Intent.createChooser(intentCompartir, "Compartir noticia"));
        } catch (android.content.ActivityNotFoundException e) {
            showToast("No se encontró ninguna app para compartir");
        }
    }

    private void cargarNoticia() {
        Log.d(TAG, "Cargando noticia con ID: " + noticiaId);

        FirebaseManager.getInstance().getNoticiaById(noticiaId, new FirebaseManager.FirestoreCallback<Noticia>() {
            @Override
            public void onSuccess(Noticia noticiaObtenida) {
                if (noticiaObtenida != null) {
                    noticia = noticiaObtenida;
                    mostrarNoticia();
                } else {
                    showToast("No se encontró la noticia");
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al cargar noticia", e);
                showToast("Error al cargar la noticia: " + e.getMessage(), Toast.LENGTH_LONG);
                finish();
            }
        });
    }

    private void mostrarNoticia() {
        mostrarImagenPortada();
        mostrarInformacionBasicaNoticia();
        mostrarAutorNoticia();
        mostrarFechaNoticia();
        mostrarUbicacionNoticia();
        mostrarContenidoNoticia();
        mostrarContenidoEnriquecido();
        mostrarVisualizacionesNoticia();
        mostrarEstadoNoticia();
        configurarBotonesNoticia();
        actualizarIconoBookmark();
        incrementarVisualizaciones();

        Log.d(TAG, "Noticia mostrada: " + noticia.getTitulo());
    }

    private void mostrarImagenPortada() {
        if (noticia.getImagenUrl() != null && !noticia.getImagenUrl().isEmpty()) {
            Picasso.get()
                    .load(noticia.getImagenUrl())
                    .placeholder(R.color.blue_50)
                    .error(R.color.blue_50)
                    .fit()
                    .centerCrop()
                    .into(ivPortada);
            Log.d(TAG, "Imagen cargada: " + noticia.getImagenUrl());
        } else {
            ivPortada.setImageResource(R.color.blue_50);
            Log.d(TAG, "No hay imagen para esta noticia");
        }
    }

    private void incrementarVisualizaciones() {
        if (noticiaId != null) {
            FirebaseManager.getInstance().incrementarVisualizaciones(noticiaId);
            Log.d(TAG, "Visualizaciones incrementadas para noticia: " + noticiaId);
        }

        // Incrementar contador de noticias leídas del usuario
        String userId = UsuarioPreferences.getUserId(this);
        if (userId != null && !userId.isEmpty()) {
            FirebaseManager.getInstance().incrementarNoticiasLeidas(userId);
            Log.d(TAG, "Noticias leídas incrementadas para usuario: " + userId);
        }
    }

    private void mostrarInformacionBasicaNoticia() {
        String categoriaNombre = obtenerNombreCategoria(noticia.getCategoriaId());
        tvCategoria.setText(categoriaNombre);

        String colorHex = noticia.getColorCategoria();
        try {
            tvCategoria.setTextColor(Color.parseColor(colorHex));
        } catch (Exception e) {
            tvCategoria.setTextColor(Color.parseColor("#1976D2"));
        }

        tvTitulo.setText(noticia.getTitulo());
    }

    private void mostrarAutorNoticia() {
        if (tvAutor != null) {
            String autorNombre = noticia.getAutorNombre();
            if (autorNombre != null && !autorNombre.isEmpty()) {
                tvAutor.setText(autorNombre);
            } else {
                tvAutor.setText("Redacción GeoNews");
            }
        }
    }

    private void mostrarFechaNoticia() {
        String fechaStr = noticia.getFechaPublicacion();
        if (fechaStr != null && !fechaStr.isEmpty()) {
            tvFecha.setText(formatearFecha(fechaStr));
        } else {
            tvFecha.setText("Hoy");
        }
    }

    private void mostrarUbicacionNoticia() {
        if (noticia.getParroquiaNombre() != null && !noticia.getParroquiaNombre().isEmpty()) {
            tvUbicacion.setText(noticia.getParroquiaNombre());
        } else if (noticia.getUbicacion() != null) {
            tvUbicacion.setText(noticia.getUbicacion());
        } else {
            tvUbicacion.setText("Ibarra");
        }
    }

    private void mostrarContenidoNoticia() {
        if (noticia.getDescripcion() != null && !noticia.getDescripcion().isEmpty()) {
            tvDescripcion.setText(noticia.getDescripcion());
        } else {
            tvDescripcion.setText("");
        }

        if (noticia.getContenido() != null && !noticia.getContenido().isEmpty()) {
            tvContenido.setText(noticia.getContenido());
        } else {
            tvContenido.setText(noticia.getDescripcion() != null ? noticia.getDescripcion() : "Sin contenido");
        }
    }

    private void mostrarContenidoEnriquecido() {
        // Mostrar cita destacada si existe
        if (noticia.getCitaDestacada() != null && !noticia.getCitaDestacada().isEmpty()) {
            tvCitaDestacada.setText(noticia.getCitaDestacada());
            layoutCitaDestacada.setVisibility(android.view.View.VISIBLE);
        } else {
            layoutCitaDestacada.setVisibility(android.view.View.GONE);
        }

        // Mostrar impacto comunitario si existe
        if (noticia.getImpactoComunitario() != null && !noticia.getImpactoComunitario().isEmpty()) {
            tvImpactoComunitario.setText(noticia.getImpactoComunitario());
            layoutImpactoComunitario.setVisibility(android.view.View.VISIBLE);
        } else {
            layoutImpactoComunitario.setVisibility(android.view.View.GONE);
        }

        // Mostrar hashtags si existen
        if (noticia.getHashtags() != null && !noticia.getHashtags().isEmpty()) {
            // Formatear hashtags (agregar # si no lo tiene)
            String hashtagsFormateados = noticia.getHashtags();
            if (!hashtagsFormateados.startsWith("#")) {
                hashtagsFormateados = "#" + hashtagsFormateados.replace(",", " #").replace(", ", " #");
            }
            tvHashtags.setText(hashtagsFormateados);
            tvHashtags.setVisibility(android.view.View.VISIBLE);
        } else {
            tvHashtags.setVisibility(android.view.View.GONE);
        }
    }

    private void mostrarVisualizacionesNoticia() {
        int visualizaciones = noticia.getVisualizaciones() != null ? noticia.getVisualizaciones() : 0;
        tvVisualizaciones.setText("👁 " + visualizaciones + " visualizaciones");
    }

    private void mostrarEstadoNoticia() {
        String estado = noticia.getEstado();
        if (estado != null) {
            String estadoTexto;
            switch (estado) {
                case "published":
                    estadoTexto = "📰 Publicado";
                    break;
                case "draft":
                    estadoTexto = "✏️ Borrador";
                    break;
                case "archived":
                    estadoTexto = "📦 Archivado";
                    break;
                default:
                    estadoTexto = "📰 " + estado;
            }
            tvEstado.setText(estadoTexto);
        }
    }

    private void configurarBotonesNoticia() {
        // Verificar si la noticia tiene ubicación
        if (noticia.getLatitud() != null && noticia.getLongitud() != null) {
            // Mostrar mini-mapa y ocultar botón
            layoutUbicacionMapa.setVisibility(android.view.View.VISIBLE);
            btnVerMapa.setVisibility(android.view.View.GONE);

            // Inicializar mini-mapa
            inicializarMiniMapa();

            // Mostrar dirección
            String direccion = noticia.getParroquiaNombre() != null ?
                noticia.getParroquiaNombre() + ", Ibarra" : "Ibarra, Ecuador";
            tvDireccionMapa.setText(direccion);
        } else {
            // No hay ubicación, ocultar mapa y mostrar botón deshabilitado
            layoutUbicacionMapa.setVisibility(android.view.View.GONE);
            btnVerMapa.setVisibility(android.view.View.VISIBLE);
            btnVerMapa.setEnabled(false);
            btnVerMapa.setText("Ubicación no disponible");
        }
    }

    /**
     * Inicializa el mini-mapa de la noticia
     */
    private void inicializarMiniMapa() {
        // Crear SupportMapFragment dinámicamente
        mapFragment = SupportMapFragment.newInstance();

        // Agregar el fragment al contenedor
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.map_container_detalle, mapFragment)
            .commit();

        // Obtener el mapa de forma asíncrona
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        miniMapa = googleMap;

        // Configurar mapa (modo lite para mejor rendimiento)
        miniMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        miniMapa.getUiSettings().setZoomControlsEnabled(false);
        miniMapa.getUiSettings().setAllGesturesEnabled(false); // Deshabilitar gestos en mini-mapa
        miniMapa.getUiSettings().setMapToolbarEnabled(false);

        // Mostrar ubicación de la noticia
        if (noticia != null && noticia.getLatitud() != null && noticia.getLongitud() != null) {
            LatLng ubicacionNoticia = new LatLng(noticia.getLatitud(), noticia.getLongitud());

            // Agregar marcador
            miniMapa.addMarker(new MarkerOptions()
                .position(ubicacionNoticia)
                .title(noticia.getTitulo()));

            // Centrar cámara
            miniMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionNoticia, ZOOM_MAPA_DETALLE));

            // Deshabilitar interacción - solo mostrar ubicación
            miniMapa.getUiSettings().setAllGesturesEnabled(false);
            miniMapa.getUiSettings().setMapToolbarEnabled(false);

            Log.d(TAG, "Mini-mapa configurado en: " + ubicacionNoticia);
        }
    }

    private String obtenerNombreCategoria(Integer categoriaId) {
        if (categoriaId == null) return "General";

        switch (categoriaId) {
            case 1: return "Política";
            case 2: return "Economía";
            case 3: return "Cultura";
            case 4: return "Deportes";
            case 5: return "Educación";
            case 6: return "Salud";
            case 7: return "Seguridad";
            case 8: return "Medio Ambiente";
            case 9: return "Turismo";
            case 10: return "Tecnología";
            default: return "General";
        }
    }

    private String formatearFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty()) {
            return "Hoy";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

            Date fecha = inputFormat.parse(fechaStr);
            if (fecha != null) {
                return outputFormat.format(fecha);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al formatear fecha", e);
        }

        return fechaStr;
    }

    // ==================== GUARDAR/BOOKMARK ====================

    /**
     * Alterna entre guardar y eliminar la noticia de favoritos
     * Incluye animaciones visuales para feedback del usuario
     */
    private void toggleGuardarNoticia() {
        if (noticia == null || noticiaId == null) {
            showToast("Error: No se puede guardar la noticia");
            return;
        }

        boolean estaGuardada = UsuarioPreferences.isNoticiaGuardada(this, noticiaId);

        if (estaGuardada) {
            // Eliminar de guardados con animación
            AnimationHelper.removeAnimation(btnBookmark, () -> {
                UsuarioPreferences.eliminarNoticiaGuardada(this, noticiaId);
                btnBookmark.setImageResource(R.drawable.ic_bookmark_outline);
                btnBookmark.setImageTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.icon_primary, getTheme())));
            });
            showToast("Artículo eliminado de guardados");
            Log.d(TAG, "Noticia eliminada de guardados: " + noticiaId);
        } else {
            // Guardar con animación especial
            AnimationHelper.bookmarkToggle(btnBookmark, true, () -> {
                UsuarioPreferences.guardarNoticia(this, noticiaId);
                btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                // Quitar tint para mostrar el color dorado del icono
                btnBookmark.setImageTintList(null);
            });
            showToast("Artículo guardado");
            Log.d(TAG, "Noticia guardada: " + noticiaId);
        }
    }

    /**
     * Actualiza el ícono del bookmark según el estado guardado
     * Se llama al cargar la noticia para mostrar el estado correcto
     */
    private void actualizarIconoBookmark() {
        if (btnBookmark != null && noticiaId != null) {
            boolean estaGuardada = UsuarioPreferences.isNoticiaGuardada(this, noticiaId);
            if (estaGuardada) {
                btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                // Quitar tint para mostrar el color dorado
                btnBookmark.setImageTintList(null);
            } else {
                btnBookmark.setImageResource(R.drawable.ic_bookmark_outline);
                btnBookmark.setImageTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.icon_primary, getTheme())));
            }
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
