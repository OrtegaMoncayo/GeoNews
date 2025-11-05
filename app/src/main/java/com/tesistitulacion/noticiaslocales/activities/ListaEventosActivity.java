package com.tesistitulacion.noticiaslocales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.adapters.EventoAdapter;
import com.tesistitulacion.noticiaslocales.db.EventoServiceHTTP;
import com.tesistitulacion.noticiaslocales.modelo.Evento;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de eventos comunitarios
 * Extiende de BaseActivity para tener navegación automática
 */
public class ListaEventosActivity extends BaseActivity {
    private static final String TAG = "ListaEventosActivity";

    private RecyclerView rvEventos;
    private EventoAdapter adapter;
    private List<Evento> eventos;
    private FloatingActionButton fabCrearEvento;
    private boolean cargando = false;

    @Override
    protected int getNavegacionActiva() {
        return NAV_EVENTOS; // Marca esta sección como activa
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_lista_eventos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializarVistas();
        configurarRecyclerView();
        configurarFAB();
        cargarEventos();
    }

    private void inicializarVistas() {
        rvEventos = findViewById(R.id.rv_eventos);
        fabCrearEvento = findViewById(R.id.fab_crear_evento);
    }

    private void configurarRecyclerView() {
        eventos = new ArrayList<>();
        rvEventos.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventoAdapter((evento, position) -> {
            // Click en evento → mostrar detalles
            Toast.makeText(this,
                    "Evento: " + evento.getDescripcion(),
                    Toast.LENGTH_SHORT).show();

            // TODO: Implementar DetalleEventoActivity
            // Intent intent = new Intent(this, DetalleEventoActivity.class);
            // intent.putExtra("evento_id", evento.getId());
            // startActivity(intent);
        });

        rvEventos.setAdapter(adapter);
    }

    private void configurarFAB() {
        fabCrearEvento.setOnClickListener(v -> {
            // Abrir pantalla para registrar nuevo evento
            Intent intent = new Intent(this, RegistrarEventoActivity.class);
            startActivity(intent);
        });
    }

    private void cargarEventos() {
        if (cargando) {
            Log.d(TAG, "Ya hay una carga en progreso");
            return;
        }

        Log.d(TAG, "Cargando eventos...");
        cargando = true;
        Toast.makeText(this, "Cargando eventos...", Toast.LENGTH_SHORT).show();

        // Cargar en segundo plano
        new Thread(() -> {
            try {
                List<Evento> eventosObtenidos = EventoServiceHTTP.obtenerTodos();
                Log.d(TAG, "Eventos obtenidos: " +
                        (eventosObtenidos != null ? eventosObtenidos.size() : "null"));

                runOnUiThread(() -> {
                    cargando = false;

                    if (eventosObtenidos != null && !eventosObtenidos.isEmpty()) {
                        adapter.actualizarLista(eventosObtenidos);

                        Log.i(TAG, "Eventos cargados: " + eventosObtenidos.size());
                        Toast.makeText(this,
                                "✅ " + eventosObtenidos.size() + " eventos cargados",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "No se encontraron eventos");
                        Toast.makeText(this,
                                "No hay eventos registrados aún",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al cargar eventos: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    cargando = false;
                    Toast.makeText(this,
                            "Error al cargar eventos: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar eventos al volver (por si se creó uno nuevo)
        cargarEventos();
    }
}
