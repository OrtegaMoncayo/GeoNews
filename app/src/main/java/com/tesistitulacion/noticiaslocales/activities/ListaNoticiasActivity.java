package com.tesistitulacion.noticiaslocales.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.adapters.NoticiaAdapter;
import com.tesistitulacion.noticiaslocales.db.NoticiaServiceHTTP;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla principal: Lista de noticias locales
 * Extiende de BaseActivity para tener navegación automática
 */
public class ListaNoticiasActivity extends BaseActivity {
    private static final String TAG = "ListaNoticiasActivity";

    private RecyclerView rvNoticias;
    private NoticiaAdapter adapter;
    private List<Noticia> noticias;
    private boolean cargando = false;

    @Override
    protected int getNavegacionActiva() {
        return NAV_NOTICIAS; // Marca esta sección como activa
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_lista_noticias;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializarVistas();
        configurarRecyclerView();
        cargarNoticias();
    }

    private void inicializarVistas() {
        rvNoticias = findViewById(R.id.rv_noticias);
    }

    private void configurarRecyclerView() {
        noticias = new ArrayList<>();
        rvNoticias.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoticiaAdapter((noticia, position) -> {
            // Click en noticia → abrir detalle
            Toast.makeText(this,
                    "Abrir detalle: " + noticia.getTitulo(),
                    Toast.LENGTH_SHORT).show();

            // TODO: Implementar DetalleNoticiaActivity
            // Intent intent = new Intent(this, DetalleNoticiaActivity.class);
            // intent.putExtra("noticia_id", noticia.getId());
            // startActivity(intent);
        });

        rvNoticias.setAdapter(adapter);
    }

    private void cargarNoticias() {
        if (cargando) {
            Log.d(TAG, "Ya hay una carga en progreso");
            return;
        }

        Log.d(TAG, "Cargando noticias...");
        cargando = true;
        Toast.makeText(this, "Cargando noticias...", Toast.LENGTH_SHORT).show();

        // Cargar en segundo plano
        new Thread(() -> {
            try {
                List<Noticia> noticiasObtenidas = NoticiaServiceHTTP.obtenerTodas();
                Log.d(TAG, "Noticias obtenidas: " +
                        (noticiasObtenidas != null ? noticiasObtenidas.size() : "null"));

                runOnUiThread(() -> {
                    cargando = false;

                    if (noticiasObtenidas != null && !noticiasObtenidas.isEmpty()) {
                        adapter.actualizarLista(noticiasObtenidas);

                        Log.i(TAG, "Noticias cargadas: " + noticiasObtenidas.size());
                        Toast.makeText(this,
                                "✅ " + noticiasObtenidas.size() + " noticias cargadas",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "No se encontraron noticias");
                        Toast.makeText(this,
                                "No hay noticias disponibles",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al cargar noticias: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    cargando = false;
                    Toast.makeText(this,
                            "Error al cargar noticias: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar noticias al volver a la pantalla (por si hay cambios)
        // cargarNoticias(); // Descomentar si quieres recargar siempre
    }
}
