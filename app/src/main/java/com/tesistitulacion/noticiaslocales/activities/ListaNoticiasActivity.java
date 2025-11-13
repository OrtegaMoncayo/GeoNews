package com.tesistitulacion.noticiaslocales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.adapters.NoticiaAdapter;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
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
            if (noticia.getFirestoreId() != null) {
                Intent intent = new Intent(this, DetalleNoticiaActivity.class);
                intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticia.getFirestoreId());
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        "Error: ID de noticia no disponible",
                        Toast.LENGTH_SHORT).show();
            }
        });

        rvNoticias.setAdapter(adapter);
    }

    private void cargarNoticias() {
        if (cargando) {
            Log.d(TAG, "Ya hay una carga en progreso");
            return;
        }

        Log.d(TAG, "Cargando noticias desde Firebase...");
        cargando = true;
        Toast.makeText(this, "Cargando noticias...", Toast.LENGTH_SHORT).show();

        // Cargar desde Firebase Firestore
        FirebaseManager.getInstance().getAllNoticias(new FirebaseManager.FirestoreCallback<List<Noticia>>() {
            @Override
            public void onSuccess(List<Noticia> noticiasObtenidas) {
                cargando = false;
                Log.d(TAG, "Noticias obtenidas de Firebase: " + noticiasObtenidas.size());

                if (!noticiasObtenidas.isEmpty()) {
                    adapter.actualizarLista(noticiasObtenidas);

                    Log.i(TAG, "Noticias cargadas: " + noticiasObtenidas.size());
                    Toast.makeText(ListaNoticiasActivity.this,
                            "✅ " + noticiasObtenidas.size() + " noticias cargadas",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "No se encontraron noticias");
                    Toast.makeText(ListaNoticiasActivity.this,
                            "No hay noticias disponibles",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                cargando = false;
                Log.e(TAG, "Error al cargar noticias desde Firebase: " + e.getMessage(), e);
                Toast.makeText(ListaNoticiasActivity.this,
                        "Error al cargar noticias: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar noticias al volver a la pantalla (por si hay cambios)
        // cargarNoticias(); // Descomentar si quieres recargar siempre
    }
}
