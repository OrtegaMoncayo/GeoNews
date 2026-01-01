package com.tesistitulacion.noticiaslocales.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity que muestra el detalle completo de una noticia
 */
public class DetalleNoticiaActivity extends AppCompatActivity {

    private static final String TAG = "DetalleNoticiaActivity";
    public static final String EXTRA_NOTICIA_ID = "noticia_id";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate iniciado");

        try {
            setContentView(R.layout.activity_detalle_noticia);
            Log.d(TAG, "Layout inflado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inflar layout", e);
            Toast.makeText(this, "Error al cargar la vista: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Obtener ID de la noticia
        noticiaId = getIntent().getStringExtra(EXTRA_NOTICIA_ID);
        Log.d(TAG, "Noticia ID recibido: " + noticiaId);

        if (noticiaId == null) {
            Toast.makeText(this, "Error: No se especificó la noticia", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            btnBookmark.setOnClickListener(v -> {
                if (noticia != null) {
                    // TODO: Implementar funcionalidad de guardar favoritos
                    Toast.makeText(this, "Noticia guardada en favoritos", Toast.LENGTH_SHORT).show();
                    // Cambiar icono a estrella llena
                    btnBookmark.setImageResource(android.R.drawable.star_big_on);
                }
            });
        }

        // Botón compartir
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                if (noticia != null) {
                    compartirNoticia();
                }
            });
        }

        // Botón ver en mapa
        btnVerMapa.setOnClickListener(v -> {
            if (noticia != null && noticia.getLatitud() != null && noticia.getLongitud() != null) {
                Intent intent = new Intent(this, MapaActivity.class);
                intent.putExtra("latitud", noticia.getLatitud());
                intent.putExtra("longitud", noticia.getLongitud());
                intent.putExtra("titulo", noticia.getTitulo());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Esta noticia no tiene ubicación", Toast.LENGTH_SHORT).show();
            }
        });
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
            Toast.makeText(this, "No se encontró ninguna app para compartir", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(DetalleNoticiaActivity.this,
                            "No se encontró la noticia",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al cargar noticia", e);
                Toast.makeText(DetalleNoticiaActivity.this,
                        "Error al cargar la noticia: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void mostrarNoticia() {
        mostrarImagenPortada();
        mostrarInformacionBasicaNoticia();
        mostrarFechaNoticia();
        mostrarUbicacionNoticia();
        mostrarContenidoNoticia();
        mostrarContenidoEnriquecido();
        mostrarVisualizacionesNoticia();
        mostrarEstadoNoticia();
        configurarBotonesNoticia();
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
        if (noticia.getLatitud() == null || noticia.getLongitud() == null) {
            btnVerMapa.setEnabled(false);
            btnVerMapa.setText("Ubicación no disponible");
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

}
