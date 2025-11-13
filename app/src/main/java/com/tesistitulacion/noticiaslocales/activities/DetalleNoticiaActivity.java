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
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;

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
    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
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
    private FloatingActionButton fabCompartir;

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

            configurarToolbar();
            Log.d(TAG, "Toolbar configurado");

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
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbar = findViewById(R.id.toolbar);
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
        fabCompartir = findViewById(R.id.fab_compartir);
    }

    private void configurarToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void configurarBotones() {
        // Botón ver en mapa
        btnVerMapa.setOnClickListener(v -> {
            if (noticia != null && noticia.getLatitud() != null && noticia.getLongitud() != null) {
                // TODO: Abrir MapaActivity con la ubicación de la noticia
                Intent intent = new Intent(this, MapaActivity.class);
                intent.putExtra("latitud", noticia.getLatitud());
                intent.putExtra("longitud", noticia.getLongitud());
                intent.putExtra("titulo", noticia.getTitulo());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Esta noticia no tiene ubicación", Toast.LENGTH_SHORT).show();
            }
        });

        // FAB compartir
        fabCompartir.setOnClickListener(v -> {
            if (noticia != null) {
                compartirNoticia();
            }
        });
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
        // Título en el toolbar
        toolbarLayout.setTitle(noticia.getTitulo());

        // Categoría
        String categoriaNombre = obtenerNombreCategoria(noticia.getCategoriaId());
        tvCategoria.setText(categoriaNombre);

        // Color de categoría
        String colorHex = noticia.getColorCategoria();
        try {
            tvCategoria.setTextColor(Color.parseColor(colorHex));
        } catch (Exception e) {
            tvCategoria.setTextColor(Color.parseColor("#1976D2"));
        }

        // Título
        tvTitulo.setText(noticia.getTitulo());

        // Fecha
        String fechaStr = noticia.getFechaPublicacion();
        if (fechaStr != null && !fechaStr.isEmpty()) {
            tvFecha.setText(formatearFecha(fechaStr));
        } else {
            tvFecha.setText("Hoy");
        }

        // Ubicación
        if (noticia.getParroquiaNombre() != null && !noticia.getParroquiaNombre().isEmpty()) {
            tvUbicacion.setText(noticia.getParroquiaNombre());
        } else if (noticia.getUbicacion() != null) {
            tvUbicacion.setText(noticia.getUbicacion());
        } else {
            tvUbicacion.setText("Ibarra");
        }

        // Descripción
        if (noticia.getDescripcion() != null && !noticia.getDescripcion().isEmpty()) {
            tvDescripcion.setText(noticia.getDescripcion());
        } else {
            tvDescripcion.setText("");
        }

        // Contenido
        if (noticia.getContenido() != null && !noticia.getContenido().isEmpty()) {
            tvContenido.setText(noticia.getContenido());
        } else {
            // Si no hay contenido, mostrar la descripción extendida
            tvContenido.setText(noticia.getDescripcion() != null ? noticia.getDescripcion() : "Sin contenido");
        }

        // Visualizaciones
        int visualizaciones = noticia.getVisualizaciones() != null ? noticia.getVisualizaciones() : 0;
        tvVisualizaciones.setText("👁 " + visualizaciones + " visualizaciones");

        // Estado
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

        // Imagen de portada (por ahora oculta, se puede implementar con Glide después)
        // Si tiene URL de imagen, cargarla con Glide o Picasso
        if (noticia.getImagenUrl() != null && !noticia.getImagenUrl().isEmpty()) {
            // TODO: Cargar imagen con Glide
            // Glide.with(this).load(noticia.getImagenUrl()).into(ivPortada);
        }

        // Botón mapa - habilitar solo si tiene coordenadas
        if (noticia.getLatitud() == null || noticia.getLongitud() == null) {
            btnVerMapa.setEnabled(false);
            btnVerMapa.setText("Ubicación no disponible");
        }

        Log.d(TAG, "Noticia mostrada: " + noticia.getTitulo());
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

    private void compartirNoticia() {
        String textoCompartir = noticia.getTitulo() + "\n\n" +
                (noticia.getDescripcion() != null ? noticia.getDescripcion() : "") +
                "\n\nNoticia de Ibarra - App Noticias Locales";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, noticia.getTitulo());
        intent.putExtra(Intent.EXTRA_TEXT, textoCompartir);

        startActivity(Intent.createChooser(intent, "Compartir noticia"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
