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
import com.tesistitulacion.noticiaslocales.modelo.Evento;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity que muestra el detalle completo de un evento
 */
public class DetalleEventoActivity extends AppCompatActivity {

    private static final String TAG = "DetalleEventoActivity";
    public static final String EXTRA_EVENTO_ID = "evento_id";

    private String eventoId;
    private Evento evento;

    // Views
    private CollapsingToolbarLayout toolbarLayout;
    private Toolbar toolbar;
    private ImageView ivPortada;
    private TextView tvCategoria;
    private TextView tvDescripcion;
    private TextView tvFecha;
    private TextView tvEstado;
    private TextView tvUbicacion;
    private TextView tvCupoDisponible;
    private TextView tvCupoMaximo;
    private TextView tvCosto;
    private TextView tvTelefono;
    private TextView tvEmail;
    private MaterialButton btnVerMapa;
    private MaterialButton btnInscribirse;
    private FloatingActionButton fabCompartir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate iniciado");

        try {
            setContentView(R.layout.activity_detalle_evento);
            Log.d(TAG, "Layout inflado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al inflar layout", e);
            Toast.makeText(this, "Error al cargar la vista: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Obtener ID del evento
        eventoId = getIntent().getStringExtra(EXTRA_EVENTO_ID);
        Log.d(TAG, "Evento ID recibido: " + eventoId);

        if (eventoId == null) {
            Toast.makeText(this, "Error: No se especificó el evento", Toast.LENGTH_SHORT).show();
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

            cargarEvento();
            Log.d(TAG, "Carga de evento iniciada");
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
        tvDescripcion = findViewById(R.id.tv_descripcion);
        tvFecha = findViewById(R.id.tv_fecha);
        tvEstado = findViewById(R.id.tv_estado);
        tvUbicacion = findViewById(R.id.tv_ubicacion);
        tvCupoDisponible = findViewById(R.id.tv_cupo_disponible);
        tvCupoMaximo = findViewById(R.id.tv_cupo_maximo);
        tvCosto = findViewById(R.id.tv_costo);
        tvTelefono = findViewById(R.id.tv_telefono);
        tvEmail = findViewById(R.id.tv_email);
        btnVerMapa = findViewById(R.id.btn_ver_mapa);
        btnInscribirse = findViewById(R.id.btn_inscribirse);
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
            if (evento != null && evento.getLatitud() != null && evento.getLongitud() != null) {
                Intent intent = new Intent(this, MapaActivity.class);
                intent.putExtra("latitud", evento.getLatitud());
                intent.putExtra("longitud", evento.getLongitud());
                intent.putExtra("titulo", evento.getDescripcion());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Este evento no tiene ubicación", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón inscribirse
        btnInscribirse.setOnClickListener(v -> {
            if (evento != null) {
                // TODO: Implementar inscripción al evento
                Toast.makeText(this, "Funcionalidad de inscripción próximamente", Toast.LENGTH_SHORT).show();
            }
        });

        // FAB compartir
        fabCompartir.setOnClickListener(v -> {
            if (evento != null) {
                compartirEvento();
            }
        });
    }

    private void cargarEvento() {
        Log.d(TAG, "Cargando evento con ID: " + eventoId);

        FirebaseManager.getInstance().getEventoById(eventoId, new FirebaseManager.FirestoreCallback<Evento>() {
            @Override
            public void onSuccess(Evento eventoObtenido) {
                if (eventoObtenido != null) {
                    evento = eventoObtenido;
                    mostrarEvento();
                } else {
                    Toast.makeText(DetalleEventoActivity.this,
                            "No se encontró el evento",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al cargar evento", e);
                Toast.makeText(DetalleEventoActivity.this,
                        "Error al cargar el evento: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void mostrarEvento() {
        // Título en el toolbar
        toolbarLayout.setTitle(evento.getDescripcion());

        // Categoría
        String categoriaTexto = obtenerTextoCategoria(evento.getCategoriaEvento());
        tvCategoria.setText(categoriaTexto);

        // Color según categoría
        String color = obtenerColorCategoria(evento.getCategoriaEvento());
        try {
            tvCategoria.setTextColor(Color.parseColor(color));
        } catch (Exception e) {
            tvCategoria.setTextColor(Color.parseColor("#1976D2"));
        }

        // Descripción
        tvDescripcion.setText(evento.getDescripcion());

        // Fecha
        if (evento.getFecha() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm", new Locale("es", "ES"));
            String fechaStr = sdf.format(new Date(evento.getFecha()));
            tvFecha.setText(fechaStr);
        } else {
            tvFecha.setText("Fecha por confirmar");
        }

        // Estado
        mostrarEstado(evento.getEstado());

        // Ubicación
        if (evento.getUbicacion() != null && !evento.getUbicacion().isEmpty()) {
            tvUbicacion.setText(evento.getUbicacion());
        } else if (evento.getParroquiaNombre() != null) {
            tvUbicacion.setText(evento.getParroquiaNombre());
        } else {
            tvUbicacion.setText("Ibarra");
        }

        // Cupos
        int cupoDisponible = 0;
        if (evento.getCupoMaximo() != null && evento.getCupoActual() != null) {
            cupoDisponible = evento.getCupoMaximo() - evento.getCupoActual();
            tvCupoDisponible.setText(String.valueOf(cupoDisponible));
            tvCupoMaximo.setText(String.valueOf(evento.getCupoMaximo()));
        } else {
            tvCupoDisponible.setText("∞");
            tvCupoMaximo.setText("Ilimitado");
        }

        // Costo
        if (evento.getCosto() != null && evento.getCosto() > 0) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
            tvCosto.setText(currencyFormat.format(evento.getCosto()));
            tvCosto.setTextColor(Color.parseColor("#FF9800"));
        } else {
            tvCosto.setText("GRATIS");
            tvCosto.setTextColor(Color.parseColor("#4CAF50"));
        }

        // Contacto
        if (evento.getContactoTelefono() != null && !evento.getContactoTelefono().isEmpty()) {
            tvTelefono.setText("📱 " + evento.getContactoTelefono());
        } else {
            tvTelefono.setText("📱 No disponible");
        }

        if (evento.getContactoEmail() != null && !evento.getContactoEmail().isEmpty()) {
            tvEmail.setText("✉️ " + evento.getContactoEmail());
        } else {
            tvEmail.setText("✉️ No disponible");
        }

        // Botón mapa - habilitar solo si tiene coordenadas
        if (evento.getLatitud() == null || evento.getLongitud() == null) {
            btnVerMapa.setEnabled(false);
            btnVerMapa.setText("Ubicación no disponible");
        }

        // Botón inscribirse - habilitar según cupos y estado
        if (cupoDisponible <= 0 && evento.getCupoMaximo() != null) {
            btnInscribirse.setEnabled(false);
            btnInscribirse.setText("Sin cupos disponibles");
        } else if ("finalizado".equals(evento.getEstado()) || "cancelado".equals(evento.getEstado())) {
            btnInscribirse.setEnabled(false);
            btnInscribirse.setText("Evento " + evento.getEstado());
        }

        Log.d(TAG, "Evento mostrado: " + evento.getDescripcion());
    }

    private void mostrarEstado(String estado) {
        if (estado == null) estado = "programado";

        String textoEstado;
        int colorEstado;

        switch (estado.toLowerCase()) {
            case "programado":
                textoEstado = "PROGRAMADO";
                colorEstado = Color.parseColor("#4CAF50");
                break;
            case "en_curso":
                textoEstado = "EN CURSO";
                colorEstado = Color.parseColor("#FF9800");
                break;
            case "finalizado":
                textoEstado = "FINALIZADO";
                colorEstado = Color.parseColor("#9E9E9E");
                break;
            case "cancelado":
                textoEstado = "CANCELADO";
                colorEstado = Color.parseColor("#F44336");
                break;
            default:
                textoEstado = estado.toUpperCase();
                colorEstado = Color.parseColor("#1976D2");
        }

        tvEstado.setText(textoEstado);
        tvEstado.setTextColor(colorEstado);
    }

    private String obtenerTextoCategoria(String categoria) {
        if (categoria == null) return "OTRO";

        switch (categoria.toLowerCase()) {
            case "cultural":
                return "CULTURAL";
            case "deportivo":
                return "DEPORTIVO";
            case "educativo":
                return "EDUCATIVO";
            case "comunitario":
                return "COMUNITARIO";
            default:
                return "OTRO";
        }
    }

    private String obtenerColorCategoria(String categoria) {
        if (categoria == null) return "#1976D2";

        switch (categoria.toLowerCase()) {
            case "cultural":
                return "#9C27B0"; // Púrpura
            case "deportivo":
                return "#4CAF50"; // Verde
            case "educativo":
                return "#2196F3"; // Azul
            case "comunitario":
                return "#FF9800"; // Naranja
            default:
                return "#1976D2"; // Azul oscuro
        }
    }

    private void compartirEvento() {
        String fechaStr = "";
        if (evento.getFecha() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            fechaStr = sdf.format(new Date(evento.getFecha()));
        }

        String costoStr = "Gratis";
        if (evento.getCosto() != null && evento.getCosto() > 0) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
            costoStr = currencyFormat.format(evento.getCosto());
        }

        String textoCompartir = "📅 " + evento.getDescripcion() + "\n\n" +
                "📍 " + (evento.getUbicacion() != null ? evento.getUbicacion() : "Ibarra") + "\n" +
                "🕒 " + fechaStr + "\n" +
                "💵 " + costoStr + "\n\n" +
                "¡Te esperamos! - App Noticias Ibarra";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, evento.getDescripcion());
        intent.putExtra(Intent.EXTRA_TEXT, textoCompartir);

        startActivity(Intent.createChooser(intent, "Compartir evento"));
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
