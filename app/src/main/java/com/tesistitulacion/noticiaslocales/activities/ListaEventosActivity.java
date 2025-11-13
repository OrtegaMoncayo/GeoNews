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
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
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
            // Click en evento → abrir detalle
            if (evento.getFirestoreId() != null) {
                Intent intent = new Intent(this, DetalleEventoActivity.class);
                intent.putExtra(DetalleEventoActivity.EXTRA_EVENTO_ID, evento.getFirestoreId());
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "Error: ID de evento no disponible",
                        Toast.LENGTH_SHORT).show();
            }
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

        Log.d(TAG, "Cargando eventos desde Firebase...");
        cargando = true;
        Toast.makeText(this, "Cargando eventos...", Toast.LENGTH_SHORT).show();

        // Cargar desde Firebase Firestore
        FirebaseManager.getInstance().getEventosFuturos(new FirebaseManager.FirestoreCallback<List<Evento>>() {
            @Override
            public void onSuccess(List<Evento> eventosObtenidos) {
                cargando = false;
                Log.d(TAG, "Eventos obtenidos de Firebase: " + eventosObtenidos.size());

                if (!eventosObtenidos.isEmpty()) {
                    adapter.actualizarLista(eventosObtenidos);

                    Log.i(TAG, "Eventos cargados: " + eventosObtenidos.size());
                    Toast.makeText(ListaEventosActivity.this,
                            "✅ " + eventosObtenidos.size() + " eventos cargados",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "No se encontraron eventos");
                    Toast.makeText(ListaEventosActivity.this,
                            "No hay eventos registrados aún",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                cargando = false;
                Log.e(TAG, "Error al cargar eventos desde Firebase: " + e.getMessage(), e);
                Toast.makeText(ListaEventosActivity.this,
                        "Error al cargar eventos: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar eventos al volver (por si se creó uno nuevo)
        cargarEventos();
    }
}
