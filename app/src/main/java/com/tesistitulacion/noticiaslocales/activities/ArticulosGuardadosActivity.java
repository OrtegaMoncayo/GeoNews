package com.tesistitulacion.noticiaslocales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.adapters.NoticiaAdapter;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity para mostrar los artículos/noticias guardados por el usuario
 */
public class ArticulosGuardadosActivity extends BaseActivity {
    private static final String TAG = "ArticulosGuardados";

    private RecyclerView rvArticulosGuardados;
    private NoticiaAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout layoutVacio;
    private ImageView btnVolver;
    private com.google.android.material.button.MaterialButton btnExplorar;

    private List<Noticia> noticiasGuardadas;
    private FirebaseManager firebaseManager;

    @Override
    protected int getNavegacionActiva() {
        return -1; // No mostrar navegación inferior
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_articulos_guardados;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseManager = FirebaseManager.getInstance();
        noticiasGuardadas = new ArrayList<>();

        inicializarVistas();
        configurarRecyclerView();
        configurarListeners();
        cargarArticulosGuardados();
    }

    private void inicializarVistas() {
        rvArticulosGuardados = findViewById(R.id.rv_articulos_guardados);
        progressBar = findViewById(R.id.progress_bar);
        layoutVacio = findViewById(R.id.layout_vacio);
        btnVolver = findViewById(R.id.btn_volver);
        btnExplorar = findViewById(R.id.btn_explorar);
    }

    private void configurarRecyclerView() {
        adapter = new NoticiaAdapter((noticia, position) -> {
            // Abrir detalle de la noticia
            if (noticia.getFirestoreId() != null) {
                Intent intent = new Intent(this, DetalleNoticiaActivity.class);
                intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticia.getFirestoreId());
                startActivity(intent);
            }
        });

        rvArticulosGuardados.setLayoutManager(new LinearLayoutManager(this));
        rvArticulosGuardados.setAdapter(adapter);
    }

    private void configurarListeners() {
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnExplorar != null) {
            btnExplorar.setOnClickListener(v -> {
                // Ir a la pantalla de noticias
                Intent intent = new Intent(this, ListaNoticiasActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private void cargarArticulosGuardados() {
        mostrarCargando(true);

        // Obtener IDs de noticias guardadas desde SharedPreferences
        String userId = UsuarioPreferences.getUserId(this);
        if (userId == null || userId.isEmpty()) {
            mostrarMensajeVacio();
            return;
        }

        // Por ahora, cargar noticias guardadas desde SharedPreferences
        // TODO: Implementar guardado real en Firestore
        List<String> noticiasGuardadasIds = UsuarioPreferences.getNoticiasGuardadas(this);

        if (noticiasGuardadasIds == null || noticiasGuardadasIds.isEmpty()) {
            mostrarMensajeVacio();
            return;
        }

        // Cargar las noticias una por una
        cargarNoticiasDesdeIds(noticiasGuardadasIds);
    }

    private void cargarNoticiasDesdeIds(List<String> ids) {
        noticiasGuardadas.clear();
        final int[] cargadas = {0};

        for (String noticiaId : ids) {
            firebaseManager.getNoticiaById(noticiaId, new FirebaseManager.FirestoreCallback<Noticia>() {
                @Override
                public void onSuccess(Noticia noticia) {
                    if (noticia != null) {
                        noticiasGuardadas.add(noticia);
                    }

                    cargadas[0]++;
                    if (cargadas[0] == ids.size()) {
                        mostrarNoticias();
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Error al cargar noticia: " + e.getMessage());
                    cargadas[0]++;
                    if (cargadas[0] == ids.size()) {
                        mostrarNoticias();
                    }
                }
            });
        }
    }

    private void mostrarNoticias() {
        mostrarCargando(false);

        if (noticiasGuardadas.isEmpty()) {
            mostrarMensajeVacio();
        } else {
            layoutVacio.setVisibility(View.GONE);
            rvArticulosGuardados.setVisibility(View.VISIBLE);

            adapter.actualizarLista(noticiasGuardadas);
        }
    }

    private void mostrarMensajeVacio() {
        mostrarCargando(false);
        rvArticulosGuardados.setVisibility(View.GONE);
        layoutVacio.setVisibility(View.VISIBLE);
    }

    private void mostrarCargando(boolean mostrar) {
        if (progressBar != null) {
            progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        }
    }
}
