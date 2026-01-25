package com.tesistitulacion.noticiaslocales.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.adapters.NoticiaAdapter;
import com.tesistitulacion.noticiaslocales.adapters.SkeletonAdapter;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;
import com.tesistitulacion.noticiaslocales.utils.AnimationHelper;
import com.tesistitulacion.noticiaslocales.utils.FirebaseCallbackHelper;
import com.tesistitulacion.noticiaslocales.utils.LocationHelper;
import com.tesistitulacion.noticiaslocales.utils.NewsCache;
import com.tesistitulacion.noticiaslocales.utils.NotificationHelper;
import com.tesistitulacion.noticiaslocales.utils.RecyclerViewHelper;
import com.tesistitulacion.noticiaslocales.utils.TransitionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Pantalla principal: Lista de noticias locales
 * Extiende de BaseActivity para tener navegación automática
 */
public class ListaNoticiasActivity extends BaseActivity {
    private static final String TAG = "ListaNoticiasActivity";

    // Views
    private RecyclerView rvNoticias;
    private
    NoticiaAdapter adapter;
    private SkeletonAdapter skeletonAdapter;
    private TextInputEditText etBusqueda;
    private TextInputLayout tilBusqueda;
    private LinearLayout layoutBusquedaContainer;
    private TextView tvResultadosBusqueda;
    private HorizontalScrollView hsvFiltros;
    private ImageView ivBuscar;
    private ImageView ivNotificaciones;
    private LinearLayout layoutBusquedaFiltros;
    private ChipGroup chipGroupRadio;
    private Spinner spinnerParroquias;

    // Estado de búsqueda
    private boolean busquedaVisible = false;

    // Data
    private List<Noticia> noticiasOriginales;
    private List<Noticia> noticiasFiltradas;

    // Ubicación
    private Location ubicacionActual;

    // Caché de noticias
    private NewsCache newsCache;

    // Filtros activos
    private String textoBusquedaActual = "";
    private Double radioKmActual = null; // null = todas
    private String parroquiaActual = "Todas";

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
        inicializarUbicacion();
        configurarRecyclerView();
        configurarBusqueda();
        configurarFiltros();
        cargarNoticias();

        // Solicitar permiso de notificaciones (Android 13+)
        solicitarPermisoNotificaciones();
    }

    /**
     * Solicita el permiso de notificaciones para Android 13+
     * Este permiso es OBLIGATORIO para mostrar notificaciones en Android 13+
     */
    private void solicitarPermisoNotificaciones() {
        NotificationHelper notificationHelper = NotificationHelper.getInstance(this);

        // Solo solicitar si no tiene el permiso
        if (!notificationHelper.tienePermisoNotificaciones()) {
            Log.d(TAG, "Solicitando permiso de notificaciones");
            notificationHelper.solicitarPermisoNotificaciones(this);
        } else {
            Log.d(TAG, "Permiso de notificaciones ya concedido");
        }
    }

    private void inicializarVistas() {
        rvNoticias = findViewById(R.id.rv_noticias);
        etBusqueda = findViewById(R.id.et_busqueda);
        tilBusqueda = findViewById(R.id.til_busqueda);
        layoutBusquedaContainer = findViewById(R.id.layout_busqueda_container);
        tvResultadosBusqueda = findViewById(R.id.tv_resultados_busqueda);
        hsvFiltros = findViewById(R.id.hsv_filtros);
        ivBuscar = findViewById(R.id.iv_buscar);
        ivNotificaciones = findViewById(R.id.iv_notificaciones);
        layoutBusquedaFiltros = findViewById(R.id.layout_busqueda_filtros);
        chipGroupRadio = findViewById(R.id.chip_group_radio);
        spinnerParroquias = findViewById(R.id.spinner_parroquias);

        // Configurar botón de búsqueda con animación
        if (ivBuscar != null) {
            ivBuscar.setOnClickListener(v -> {
                AnimationHelper.clickEffect(v);
                toggleBusquedaFiltros();
            });
        }

        // Configurar botón de notificaciones con animación
        if (ivNotificaciones != null) {
            ivNotificaciones.setOnClickListener(v -> {
                AnimationHelper.pulse(v);
                mostrarNotificaciones();
            });
        }

        // Inicializar listas
        noticiasOriginales = new ArrayList<>();
        noticiasFiltradas = new ArrayList<>();

        // Inicializar caché
        newsCache = NewsCache.getInstance(this);
    }

    /**
     * Muestra u oculta la barra de búsqueda y filtros con animación
     */
    private void toggleBusquedaFiltros() {
        busquedaVisible = !busquedaVisible;

        if (busquedaVisible) {
            // Mostrar búsqueda y filtros con AnimationHelper
            if (layoutBusquedaContainer != null) {
                AnimationHelper.fadeInSlideDown(layoutBusquedaContainer);
            }
            if (hsvFiltros != null) {
                AnimationHelper.fadeInSlideDown(hsvFiltros);
            }
            // Actualizar contador inmediatamente
            actualizarContadorResultados();
            // Enfocar el campo de búsqueda
            if (etBusqueda != null) {
                etBusqueda.requestFocus();
                // Mostrar teclado
                android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etBusqueda, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                }
            }
        } else {
            // Ocultar búsqueda y filtros con AnimationHelper
            if (layoutBusquedaContainer != null) {
                AnimationHelper.fadeOutSlideUp(layoutBusquedaContainer, null);
            }
            if (hsvFiltros != null) {
                AnimationHelper.fadeOutSlideUp(hsvFiltros, null);
            }
            // Limpiar búsqueda y contador
            if (etBusqueda != null) {
                etBusqueda.setText("");
            }
            if (tvResultadosBusqueda != null) {
                tvResultadosBusqueda.setVisibility(View.GONE);
            }
            // Ocultar teclado
            android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void inicializarUbicacion() {
        // Usar LocationHelper para verificar/solicitar permisos de manera centralizada
        if (getLocationHelper().checkAndRequestLocationPermission(this)) {
            // Ya tenemos permisos, obtener ubicación
            obtenerUbicacionActual();
        }
        // Si no tenemos permisos, se solicitarán automáticamente
        // y onRequestPermissionsResult manejará la respuesta
    }

    private void obtenerUbicacionActual() {
        // Usar LocationHelper para obtener ubicación
        getLocationHelper().getCurrentLocation(new LocationHelper.CurrentLocationListener() {
            @Override
            public void onLocationReceived(Location location) {
                ubicacionActual = location;
                Log.d(TAG, "Ubicación obtenida: " + location.getLatitude() + ", " + location.getLongitude());
            }

            @Override
            public void onLocationError(String error) {
                Log.w(TAG, "Error al obtener ubicación: " + error);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Permiso de ubicación
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual();
            }
        }

        // Permiso de notificaciones (Android 13+)
        if (requestCode == NotificationHelper.REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso de notificaciones CONCEDIDO");
            } else {
                Log.w(TAG, "Permiso de notificaciones DENEGADO");
            }
        }
    }

    private void configurarRecyclerView() {
        // Usar RecyclerViewHelper para configuración con animación
        RecyclerViewHelper.setupWithAnimation(rvNoticias, this);

        // Crear adapter de noticias
        adapter = new NoticiaAdapter((noticia, position) -> {
            // Click en noticia → abrir detalle con animación
            if (noticia.getFirestoreId() != null) {
                Intent intent = new Intent(this, DetalleNoticiaActivity.class);
                intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticia.getFirestoreId());
                TransitionHelper.startActivitySlideUp(this, intent);
            } else {
                Log.e(TAG, "Error: ID de noticia no disponible");
            }
        });

        // Crear adapter skeleton para loading
        skeletonAdapter = new SkeletonAdapter(5);

        // Mostrar skeleton inicialmente mientras cargan los datos
        rvNoticias.setAdapter(skeletonAdapter);
    }

    /**
     * Muestra el skeleton loading
     */
    private void mostrarSkeleton() {
        if (rvNoticias != null && skeletonAdapter != null) {
            rvNoticias.setAdapter(skeletonAdapter);
        }
    }

    /**
     * Oculta el skeleton y muestra los datos reales
     */
    private void ocultarSkeleton() {
        if (rvNoticias != null && adapter != null) {
            // Cambiar al adapter real
            if (rvNoticias.getAdapter() != adapter) {
                rvNoticias.setAdapter(adapter);
                Log.d(TAG, "Skeleton ocultado, mostrando adapter de noticias");
            }
        }
    }

    private void configurarBusqueda() {
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textoBusquedaActual = s.toString().trim();
                aplicarFiltros();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void configurarFiltros() {
        // Configurar ChipGroup de radio
        chipGroupRadio.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_todas) {
                radioKmActual = null;
            } else if (checkedId == R.id.chip_2km) {
                radioKmActual = 2.0;
            } else if (checkedId == R.id.chip_5km) {
                radioKmActual = 5.0;
            } else if (checkedId == R.id.chip_10km) {
                radioKmActual = 10.0;
            } else if (checkedId == R.id.chip_20km) {
                radioKmActual = 20.0;
            }
            aplicarFiltros();
        });

        // Configurar Spinner de parroquias
        String[] parroquias = {
                "Todas",
                "El Sagrario",
                "San Francisco",
                "La Merced",
                "Alpachaca",
                "Caranqui",
                "Priorato",
                "San Antonio",
                "Guayaquil de Alpachaca"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                parroquias
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParroquias.setAdapter(spinnerAdapter);

        spinnerParroquias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parroquiaActual = parroquias[position];
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarNoticias() {
        // Mostrar skeleton mientras carga
        mostrarSkeleton();

        // Variable para saber si ya mostramos datos del caché
        boolean mostradoDesdeCache = false;

        // Intentar cargar desde caché primero (para mostrar datos inmediatamente)
        if (newsCache != null && newsCache.hayCache() && !newsCache.cacheExpirado()) {
            List<Noticia> noticiasCached = newsCache.obtenerNoticias();
            if (!noticiasCached.isEmpty()) {
                Log.i(TAG, "Mostrando " + noticiasCached.size() + " noticias desde caché (" +
                      newsCache.getAntiguedadLegible() + ")");

                noticiasOriginales.clear();
                noticiasOriginales.addAll(noticiasCached);
                ocultarSkeleton();
                aplicarFiltros();
                mostradoDesdeCache = true;
            }
        }

        // Cargar desde Firebase directamente (sin usar LoadingStateManager para evitar bloqueo)
        final boolean datosDelCache = mostradoDesdeCache;

        FirebaseManager.getInstance().getAllNoticiasRealtime(new FirebaseManager.FirestoreCallback<List<Noticia>>() {
            @Override
            public void onSuccess(List<Noticia> noticiasObtenidas) {
                if (noticiasObtenidas != null && !noticiasObtenidas.isEmpty()) {
                    // Guardar noticias originales
                    noticiasOriginales.clear();
                    noticiasOriginales.addAll(noticiasObtenidas);

                    Log.i(TAG, "Noticias actualizadas desde Firebase: " + noticiasObtenidas.size());

                    // Guardar en caché para uso offline
                    if (newsCache != null) {
                        newsCache.guardarNoticias(noticiasObtenidas);
                    }

                    // Ocultar skeleton y mostrar datos
                    ocultarSkeleton();

                    // Aplicar filtros
                    aplicarFiltros();
                } else {
                    Log.w(TAG, "No se encontraron noticias en Firebase");

                    // Si no hay datos de Firebase, intentar usar caché aunque esté expirado
                    if (noticiasOriginales.isEmpty() && newsCache != null && newsCache.hayCache()) {
                        List<Noticia> noticiasCached = newsCache.obtenerNoticias();
                        if (!noticiasCached.isEmpty()) {
                            Log.i(TAG, "Usando caché expirado: " + noticiasCached.size() + " noticias");
                            noticiasOriginales.addAll(noticiasCached);
                            aplicarFiltros();
                        }
                    }

                    // Ocultar skeleton
                    ocultarSkeleton();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error al cargar noticias: " + e.getMessage());

                // Si hay error y no tenemos datos del caché, intentar usar caché
                if (!datosDelCache && newsCache != null && newsCache.hayCache()) {
                    List<Noticia> noticiasCached = newsCache.obtenerNoticias();
                    if (!noticiasCached.isEmpty()) {
                        Log.i(TAG, "Error de red, usando caché: " + noticiasCached.size() + " noticias");
                        noticiasOriginales.clear();
                        noticiasOriginales.addAll(noticiasCached);
                        aplicarFiltros();
                    }
                }

                // Ocultar skeleton
                ocultarSkeleton();
            }
        });
    }

    /**
     * Aplica todos los filtros activos (búsqueda, radio, parroquia)
     */
    private void aplicarFiltros() {
        noticiasFiltradas.clear();

        for (Noticia noticia : noticiasOriginales) {
            boolean pasa = true;

            // Filtro 1: Búsqueda por texto
            if (!textoBusquedaActual.isEmpty()) {
                String textoMinusculas = textoBusquedaActual.toLowerCase(java.util.Locale.ROOT);
                String tituloMinusculas = noticia.getTitulo() != null ? noticia.getTitulo().toLowerCase(java.util.Locale.ROOT) : "";
                String descripcionMinusculas = noticia.getDescripcion() != null ? noticia.getDescripcion().toLowerCase(java.util.Locale.ROOT) : "";

                if (!tituloMinusculas.contains(textoMinusculas) && !descripcionMinusculas.contains(textoMinusculas)) {
                    pasa = false;
                }
            }

            // Filtro 2: Radio de búsqueda (GPS)
            if (pasa && radioKmActual != null && ubicacionActual != null) {
                if (noticia.getLatitud() != null && noticia.getLongitud() != null) {
                    // Usar LocationHelper para calcular distancia
                    double distanciaKm = LocationHelper.calcularDistanciaKm(
                            ubicacionActual.getLatitude(),
                            ubicacionActual.getLongitude(),
                            noticia.getLatitud(),
                            noticia.getLongitud()
                    );

                    noticia.setDistancia(distanciaKm); // Guardar para mostrar

                    if (distanciaKm > radioKmActual) {
                        pasa = false;
                    }
                } else {
                    // Si no tiene coordenadas, no pasa el filtro de radio
                    pasa = false;
                }
            }

            // Filtro 3: Parroquia
            if (pasa && !parroquiaActual.equals("Todas")) {
                String parroquiaNoticia = noticia.getParroquiaNombre();
                if (parroquiaNoticia == null || !parroquiaNoticia.equals(parroquiaActual)) {
                    pasa = false;
                }
            }

            if (pasa) {
                noticiasFiltradas.add(noticia);
            }
        }

        // Ordenar por distancia si hay filtro de radio activo
        if (radioKmActual != null && ubicacionActual != null) {
            Collections.sort(noticiasFiltradas, new Comparator<Noticia>() {
                @Override
                public int compare(Noticia n1, Noticia n2) {
                    Double d1 = n1.getDistancia();
                    Double d2 = n2.getDistancia();
                    if (d1 == null) d1 = Double.MAX_VALUE;
                    if (d2 == null) d2 = Double.MAX_VALUE;
                    return d1.compareTo(d2);
                }
            });
        }

        // Actualizar adaptador
        adapter.actualizarLista(noticiasFiltradas);
        Log.d(TAG, "Lista actualizada con " + noticiasFiltradas.size() + " noticias");

        // Actualizar contador de resultados
        actualizarContadorResultados();

        Log.d(TAG, "Filtros aplicados: " + noticiasFiltradas.size() + " de " + noticiasOriginales.size() + " noticias");
    }

    /**
     * Actualiza el contador de resultados de búsqueda con animación
     */
    private void actualizarContadorResultados() {
        if (tvResultadosBusqueda == null) return;

        // Siempre mostrar el contador cuando la búsqueda está visible
        if (!busquedaVisible) {
            tvResultadosBusqueda.setVisibility(View.GONE);
            return;
        }

        String mensaje;
        int total = noticiasFiltradas.size();
        int totalOriginal = noticiasOriginales.size();

        boolean hayBusqueda = !textoBusquedaActual.isEmpty();
        boolean hayFiltroRadio = radioKmActual != null;
        boolean hayFiltroParroquia = !parroquiaActual.equals("Todas");
        boolean hayFiltros = hayBusqueda || hayFiltroRadio || hayFiltroParroquia;

        int nuevoColor;

        if (total == 0) {
            mensaje = "❌ No se encontraron resultados";
            nuevoColor = 0xFFFF6B6B; // Rojo
            // Animación de shake cuando no hay resultados
            if (tvResultadosBusqueda.getVisibility() == View.VISIBLE) {
                tvResultadosBusqueda.animate()
                    .translationX(-10f)
                    .setDuration(50)
                    .withEndAction(() -> {
                        tvResultadosBusqueda.animate()
                            .translationX(10f)
                            .setDuration(50)
                            .withEndAction(() -> {
                                tvResultadosBusqueda.animate()
                                    .translationX(0f)
                                    .setDuration(50)
                                    .start();
                            })
                            .start();
                    })
                    .start();
            }
        } else if (hayFiltros) {
            // Hay filtros activos
            if (total == 1) {
                mensaje = "✓ 1 noticia encontrada de " + totalOriginal;
            } else {
                mensaje = "✓ " + total + " noticias encontradas de " + totalOriginal;
            }
            nuevoColor = 0xFF0d7ff2; // Azul primary
        } else {
            // Sin filtros, mostrar total
            if (total == 1) {
                mensaje = "1 noticia";
            } else {
                mensaje = total + " noticias";
            }
            nuevoColor = 0xFFFFFFFF; // Blanco
        }

        // Animación de pulso y fade
        tvResultadosBusqueda.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .alpha(0.5f)
            .setDuration(150)
            .withEndAction(() -> {
                tvResultadosBusqueda.setText(mensaje);
                tvResultadosBusqueda.setTextColor(nuevoColor);
                tvResultadosBusqueda.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(150)
                    .start();
            })
            .start();

        tvResultadosBusqueda.setVisibility(View.VISIBLE);

        Log.d(TAG, "Contador actualizado: " + mensaje);
    }

    /**
     * Abre la pantalla de notificaciones
     */
    private void mostrarNotificaciones() {
        Intent intent = new Intent(this, NotificacionesActivity.class);
        startActivity(intent);
        Log.d(TAG, "Abriendo NotificacionesActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar noticias al volver a la pantalla (por si hay cambios)
        // cargarNoticias(); // Descomentar si quieres recargar siempre
    }
}
