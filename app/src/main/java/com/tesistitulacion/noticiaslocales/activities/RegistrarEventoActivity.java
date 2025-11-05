package com.tesistitulacion.noticiaslocales.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.db.EventoServiceHTTP;
import com.tesistitulacion.noticiaslocales.modelo.Evento;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity para registrar/crear nuevos eventos comunitarios
 *
 * Funcionalidades:
 * - Formulario completo con validaciones
 * - Selectores de fecha y hora
 * - Categorías de eventos
 * - Cupos y costos
 * - Envío a backend FastAPI
 * - Notificación automática por email
 */
public class RegistrarEventoActivity extends AppCompatActivity {
    private static final String TAG = "RegistrarEvento";

    // Campos del formulario
    private EditText etDescripcion;
    private EditText etFecha;
    private EditText etHora;
    private EditText etUbicacion;
    private Spinner spCategoria;
    private EditText etCupoMaximo;
    private EditText etCosto;
    private Button btnCrearEvento;
    private ProgressBar progressBar;

    // Datos del formulario
    private Calendar calendarioSeleccionado;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm", Locale.getDefault());

    // Categorías de eventos
    private final String[] CATEGORIAS_EVENTO = {
        "Cultural",
        "Deportivo",
        "Educativo",
        "Comunitario",
        "Otro"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_evento);

        inicializarVistas();
        configurarToolbar();
        configurarSelectores();
        configurarBotonCrear();
    }

    /**
     * Inicializa todas las vistas del formulario
     */
    private void inicializarVistas() {
        etDescripcion = findViewById(R.id.et_descripcion);
        etFecha = findViewById(R.id.et_fecha);
        etHora = findViewById(R.id.et_hora);
        etUbicacion = findViewById(R.id.et_ubicacion);
        spCategoria = findViewById(R.id.sp_categoria);
        etCupoMaximo = findViewById(R.id.et_cupo_maximo);
        etCosto = findViewById(R.id.et_costo);
        btnCrearEvento = findViewById(R.id.btn_crear_evento);
        progressBar = findViewById(R.id.progress_bar);

        // Inicializar calendario en fecha/hora actual
        calendarioSeleccionado = Calendar.getInstance();
    }

    /**
     * Configura el toolbar con botón de retroceso
     */
    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Registrar Evento");
        }
    }

    /**
     * Configura los selectores de fecha, hora y categoría
     */
    private void configurarSelectores() {
        // Selector de fecha
        etFecha.setOnClickListener(v -> mostrarSelectorFecha());
        etFecha.setFocusable(false);
        etFecha.setClickable(true);

        // Selector de hora
        etHora.setOnClickListener(v -> mostrarSelectorHora());
        etHora.setFocusable(false);
        etHora.setClickable(true);

        // Spinner de categorías
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            CATEGORIAS_EVENTO
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapter);
    }

    /**
     * Muestra el selector de fecha
     */
    private void mostrarSelectorFecha() {
        int anio = calendarioSeleccionado.get(Calendar.YEAR);
        int mes = calendarioSeleccionado.get(Calendar.MONTH);
        int dia = calendarioSeleccionado.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendarioSeleccionado.set(Calendar.YEAR, year);
                calendarioSeleccionado.set(Calendar.MONTH, month);
                calendarioSeleccionado.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                etFecha.setText(formatoFecha.format(calendarioSeleccionado.getTime()));
            },
            anio, mes, dia
        );

        // No permitir fechas en el pasado
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    /**
     * Muestra el selector de hora
     */
    private void mostrarSelectorHora() {
        int hora = calendarioSeleccionado.get(Calendar.HOUR_OF_DAY);
        int minuto = calendarioSeleccionado.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                calendarioSeleccionado.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarioSeleccionado.set(Calendar.MINUTE, minute);

                etHora.setText(formatoHora.format(calendarioSeleccionado.getTime()));
            },
            hora, minuto,
            true // Formato 24 horas
        );

        timePickerDialog.show();
    }

    /**
     * Configura el botón de crear evento
     */
    private void configurarBotonCrear() {
        btnCrearEvento.setOnClickListener(v -> validarYCrearEvento());
    }

    /**
     * Valida los campos y crea el evento
     */
    private void validarYCrearEvento() {
        // Obtener valores
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaTexto = etFecha.getText().toString().trim();
        String horaTexto = etHora.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String categoria = CATEGORIAS_EVENTO[spCategoria.getSelectedItemPosition()];
        String cupoTexto = etCupoMaximo.getText().toString().trim();
        String costoTexto = etCosto.getText().toString().trim();

        // Validaciones
        if (descripcion.isEmpty()) {
            etDescripcion.setError("La descripción es requerida");
            etDescripcion.requestFocus();
            return;
        }

        if (descripcion.length() < 10) {
            etDescripcion.setError("La descripción debe tener al menos 10 caracteres");
            etDescripcion.requestFocus();
            return;
        }

        if (fechaTexto.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horaTexto.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar una hora", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ubicacion.isEmpty()) {
            etUbicacion.setError("La ubicación es requerida");
            etUbicacion.requestFocus();
            return;
        }

        // Validar cupo (opcional pero debe ser número si se ingresa)
        Integer cupoMaximo = null;
        if (!cupoTexto.isEmpty()) {
            try {
                cupoMaximo = Integer.parseInt(cupoTexto);
                if (cupoMaximo <= 0) {
                    etCupoMaximo.setError("El cupo debe ser mayor a 0");
                    etCupoMaximo.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etCupoMaximo.setError("Debe ser un número válido");
                etCupoMaximo.requestFocus();
                return;
            }
        }

        // Validar costo (opcional pero debe ser número si se ingresa)
        Double costo = 0.0;
        if (!costoTexto.isEmpty()) {
            try {
                costo = Double.parseDouble(costoTexto);
                if (costo < 0) {
                    etCosto.setError("El costo no puede ser negativo");
                    etCosto.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etCosto.setError("Debe ser un número válido");
                etCosto.requestFocus();
                return;
            }
        }

        // Crear evento
        crearEvento(descripcion, ubicacion, categoria, cupoMaximo, costo);
    }

    /**
     * Crea el evento y lo envía al backend
     */
    private void crearEvento(String descripcion, String ubicacion, String categoria,
                            Integer cupoMaximo, Double costo) {

        // Mostrar loading
        progressBar.setVisibility(View.VISIBLE);
        btnCrearEvento.setEnabled(false);

        // Crear objeto Evento
        Evento evento = new Evento();
        evento.setDescripcion(descripcion);
        evento.setFecha(calendarioSeleccionado.getTimeInMillis());
        evento.setUbicacion(ubicacion);
        evento.setCategoriaEvento(categoria.toLowerCase());
        evento.setCupoMaximo(cupoMaximo);
        evento.setCupoActual(0);
        evento.setCosto(costo);
        evento.setEstado("programado");

        // Obtener datos del usuario (si está logueado)
        int creadorId = UsuarioPreferences.getUsuarioId(this);
        if (creadorId > 0) {
            evento.setCreadorId(creadorId);
        }

        Log.d(TAG, "Creando evento: " + descripcion);

        // Enviar al backend en hilo separado
        new Thread(() -> {
            Evento eventoCreado = EventoServiceHTTP.crear(evento);

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnCrearEvento.setEnabled(true);

                if (eventoCreado != null) {
                    Log.i(TAG, "✅ Evento creado exitosamente con ID: " + eventoCreado.getId());

                    Toast.makeText(this,
                        "✅ Evento creado exitosamente\n📧 Notificación enviada por email",
                        Toast.LENGTH_LONG).show();

                    // Volver a la lista de eventos
                    finish();
                } else {
                    Log.e(TAG, "❌ Error al crear evento");
                    Toast.makeText(this,
                        "❌ Error al crear el evento. Verifica tu conexión.",
                        Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
