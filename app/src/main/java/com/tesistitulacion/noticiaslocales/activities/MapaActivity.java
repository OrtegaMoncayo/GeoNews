package com.tesistitulacion.noticiaslocales.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;
import com.tesistitulacion.noticiaslocales.utils.FirebaseCallbackHelper;
import com.tesistitulacion.noticiaslocales.utils.LocationHelper;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.ValueAnimator;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.google.android.gms.maps.model.CameraPosition;

/**
 * Pantalla de mapa con noticias georreferenciadas
 * Usa Google Maps SDK con Advanced Markers
 * Extiende de BaseActivity para tener navegación automática
 */
public class MapaActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String TAG = "MapaActivity";

    // ============================================================
    // MODO DE DESARROLLO
    // Cambia a 'true' para pruebas fuera de Ibarra/Urcuquí
    // Cambia a 'false' para producción (solo se verá Ibarra-Urcuquí)
    // ============================================================
    private static final boolean MODO_DESARROLLO = false;  // true = sin restricciones, false = solo Ibarra-Urcuquí

    // Coordenadas de Ibarra (centro de la ciudad - El Sagrario)
    private static final LatLng IBARRA_CENTRO = new LatLng(0.3476, -78.1223);
    private static final float ZOOM_LEVEL = 17f; // Zoom muy cercano para ver solo el entorno inmediato

    // Límites geográficos de Ibarra + Urcuquí (para restringir el mapa en producción)
    // Southwest (esquina suroeste) - Límite sur y oeste
    private static final LatLng IBARRA_SW = new LatLng(0.25, -78.20);
    // Northeast (esquina noreste) - Límite norte y este (extendido hasta Urcuquí)
    private static final LatLng IBARRA_NE = new LatLng(0.51, -78.05);

    // Zoom mínimo y máximo
    private static final float MIN_ZOOM = 10.5f; // No se puede alejar más (para mantener en Ibarra-Urcuquí)
    private static final float MAX_ZOOM = 20f; // Máximo acercamiento

    // Intervalo de actualización de ubicación
    private static final long LOCATION_UPDATE_INTERVAL = 10000; // 10 segundos
    private static final long LOCATION_FASTEST_INTERVAL = 5000;  // 5 segundos mínimo

    // Radio de búsqueda para mostrar noticias cercanas (en kilómetros)
    private static final double RADIO_BUSQUEDA_KM = 2.0; // Solo 2 km a la redonda (entorno inmediato)

    // Radio para modo "ocultar noticias" (solo muy cercanas)
    private static final double RADIO_OCULTAR_NOTICIAS_KM = 0.5; // 500 metros (muy cercanas)

    // Límites para el carrusel de noticias
    private static final int MAX_NOTICIAS_CARRUSEL = 8; // Mostrar máximo 8 noticias más cercanas
    private static final double RADIO_CARRUSEL_KM = 5.0; // Solo noticias dentro de 5km

    private GoogleMap mMap;
    private List<Noticia> noticias;

    // Variables para ubicación
    private Location ubicacionActual;
    private LocationCallback locationCallback;
    private Map<Marker, Noticia> markerNoticiaMap;
    private Handler handler;

    // Variables para ubicación específica desde Intent
    private Double latitudIntent;
    private Double longitudIntent;
    private String tituloIntent;

    // Views de la lista de noticias visibles
    private CardView cardListaNoticias;
    private RecyclerView rvNoticiasVisibles;
    private TextView tvContadorNoticias;
    private ImageButton btnCerrarLista;

    // Views del carrusel de noticias
    private LinearLayout layoutNoticiasCarousel;
    private RecyclerView rvNoticiasMapa;
    private ImageButton btnCerrarCarousel;
    private com.tesistitulacion.noticiaslocales.adapters.NoticiaMapaAdapter noticiaMapaAdapter;

    // Variables para animaciones
    private Marker markerSeleccionado;
    private boolean animacionEnProgreso = false;

    @Override
    protected int getNavegacionActiva() {
        return NAV_MAPA;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_mapa;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recibir parámetros del Intent (si vienen desde detalle de noticia)
        Intent intent = getIntent();
        if (intent.hasExtra("latitud") && intent.hasExtra("longitud")) {
            latitudIntent = intent.getDoubleExtra("latitud", 0.0);
            longitudIntent = intent.getDoubleExtra("longitud", 0.0);
            tituloIntent = intent.getStringExtra("titulo");
            Log.i(TAG, "Mapa abierto con ubicación específica: " + latitudIntent + ", " + longitudIntent);
        }

        // Inicializar variables
        markerNoticiaMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());

        // Inicializar lista de noticias visibles (LocationHelper se inicializa lazy en BaseActivity)
        inicializarListaNoticias();

        // Inicializar carrusel de noticias
        inicializarCarousel();

        // Verificar y solicitar permisos de ubicación
        verificarPermisosUbicacion();

        // Inicializar callback de ubicación
        inicializarLocationCallback();

        inicializarMapa();
    }

    /**
     * Inicializa la lista de noticias visibles en el mapa
     */
    private void inicializarListaNoticias() {
        cardListaNoticias = findViewById(R.id.card_lista_noticias);
        rvNoticiasVisibles = findViewById(R.id.rv_noticias_visibles);
        tvContadorNoticias = findViewById(R.id.tv_contador_noticias);
        btnCerrarLista = findViewById(R.id.btn_cerrar_lista);

        // Listener del botón cerrar con animación
        if (btnCerrarLista != null) {
            btnCerrarLista.setOnClickListener(v -> {
                // Animación de cierre
                cardListaNoticias.animate()
                    .alpha(0f)
                    .scaleY(0.8f)
                    .setDuration(200)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        cardListaNoticias.setVisibility(View.GONE);
                        cardListaNoticias.setAlpha(1f);
                        cardListaNoticias.setScaleY(1f);
                    })
                    .start();
            });
        }

        // Inicialmente oculto
        cardListaNoticias.setVisibility(View.GONE);

        Log.d(TAG, "Lista de noticias visibles inicializada");
    }

    /**
     * Inicializa el carrusel horizontal de noticias
     */
    private void inicializarCarousel() {
        layoutNoticiasCarousel = findViewById(R.id.layout_noticias_carousel);
        rvNoticiasMapa = findViewById(R.id.rv_noticias_mapa);
        btnCerrarCarousel = findViewById(R.id.btn_cerrar_carousel);

        // Configurar RecyclerView horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvNoticiasMapa.setLayoutManager(layoutManager);

        // Crear adaptador
        noticiaMapaAdapter = new com.tesistitulacion.noticiaslocales.adapters.NoticiaMapaAdapter((noticia, position) -> {
            // Abrir detalle de noticia
            if (noticia.getFirestoreId() != null) {
                Intent intent = new Intent(this, DetalleNoticiaActivity.class);
                intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticia.getFirestoreId());
                startActivity(intent);
            }
        });
        rvNoticiasMapa.setAdapter(noticiaMapaAdapter);

        // Listener del botón cerrar con animación
        if (btnCerrarCarousel != null) {
            btnCerrarCarousel.setOnClickListener(v -> {
                ocultarCarruselConAnimacion();
            });
        }

        // Siempre visible para que el mapa y el carrusel se vean juntos
        layoutNoticiasCarousel.setVisibility(View.VISIBLE);

        Log.d(TAG, "Carrusel de noticias inicializado");
    }

    /**
     * Actualiza el carrusel con las noticias más cercanas a la ubicación actual
     * Filtra por radio de 5km y muestra máximo 8 noticias ordenadas por distancia
     */
    private void actualizarCarruselConNoticiasCercanas() {
        if (noticias == null || noticias.isEmpty()) {
            Log.d(TAG, "No hay noticias para actualizar carrusel");
            return;
        }

        List<Noticia> noticiasConCoordenadas = new ArrayList<>();
        List<Noticia> noticiasCercanas = new ArrayList<>();

        // Filtrar noticias con coordenadas
        for (Noticia n : noticias) {
            if (n.getLatitud() != null && n.getLongitud() != null) {
                noticiasConCoordenadas.add(n);
            }
        }

        // Si hay ubicación GPS, filtrar por cercanía
        if (ubicacionActual != null && !noticiasConCoordenadas.isEmpty()) {
            // Filtrar noticias dentro del radio del carrusel
            for (Noticia noticia : noticiasConCoordenadas) {
                // Usar LocationHelper para calcular distancia
                double distanciaKm = LocationHelper.calcularDistanciaKm(
                    ubicacionActual.getLatitude(),
                    ubicacionActual.getLongitude(),
                    noticia.getLatitud(),
                    noticia.getLongitud()
                );

                // Solo incluir noticias dentro del radio del carrusel
                if (distanciaKm <= RADIO_CARRUSEL_KM) {
                    noticiasCercanas.add(noticia);
                }
            }

            // Limitar a las MAX_NOTICIAS_CARRUSEL más cercanas
            if (noticiasCercanas.size() > MAX_NOTICIAS_CARRUSEL) {
                noticiasCercanas = noticiasCercanas.subList(0, MAX_NOTICIAS_CARRUSEL);
            }

            Log.i(TAG, "📍 Carrusel: " + noticiasCercanas.size() + " noticias cercanas (de " +
                  noticiasConCoordenadas.size() + " totales con coordenadas) - Radio: " + RADIO_CARRUSEL_KM + "km");
        } else {
            // Sin ubicación GPS, mostrar todas limitando a MAX_NOTICIAS_CARRUSEL
            noticiasCercanas = noticiasConCoordenadas;
            if (noticiasCercanas.size() > MAX_NOTICIAS_CARRUSEL) {
                noticiasCercanas = noticiasCercanas.subList(0, MAX_NOTICIAS_CARRUSEL);
            }
            Log.w(TAG, "📍 Carrusel: Sin GPS, mostrando " + noticiasCercanas.size() + " noticias");
        }

        // Actualizar adaptador del carrusel
        if (noticiaMapaAdapter != null) {
            noticiaMapaAdapter.setUbicacionActual(ubicacionActual);
            noticiaMapaAdapter.setNoticias(noticiasCercanas);
        }

        // Mostrar carrusel con animación si hay noticias cercanas
        if (!noticiasCercanas.isEmpty() && layoutNoticiasCarousel != null) {
            if (layoutNoticiasCarousel.getVisibility() != View.VISIBLE) {
                mostrarCarruselConAnimacion();
            }
        } else if (layoutNoticiasCarousel != null) {
            // Ocultar carrusel si no hay noticias cercanas
            if (layoutNoticiasCarousel.getVisibility() == View.VISIBLE) {
                ocultarCarruselConAnimacion();
            }
        }
    }

    /**
     * Muestra el carrusel con animación - El mapa se reduce automáticamente
     */
    private void mostrarCarruselConAnimacion() {
        if (layoutNoticiasCarousel == null) {
            return;
        }

        // Si ya está visible y con alpha 1, no hacer nada
        if (layoutNoticiasCarousel.getVisibility() == View.VISIBLE && layoutNoticiasCarousel.getAlpha() == 1f) {
            return;
        }

        // Actualizar constraint del mapa para conectar al carrusel
        actualizarConstraintMapa(true);

        // Preparar animación desde abajo
        layoutNoticiasCarousel.setAlpha(0f);
        layoutNoticiasCarousel.setTranslationY(100);
        layoutNoticiasCarousel.setVisibility(View.VISIBLE);

        // Animación de aparición suave desde abajo
        layoutNoticiasCarousel.animate()
            .alpha(1f)
            .translationY(0)
            .setDuration(300)
            .setInterpolator(new DecelerateInterpolator(1.5f))
            .start();

        Log.d(TAG, "Carrusel mostrado - Mapa reducido automáticamente");
    }

    /**
     * Oculta el carrusel con animación - El mapa se expande automáticamente
     */
    private void ocultarCarruselConAnimacion() {
        if (layoutNoticiasCarousel == null || layoutNoticiasCarousel.getVisibility() != View.VISIBLE) {
            return;
        }

        // Animación de desaparición - usar GONE para que el mapa se expanda
        layoutNoticiasCarousel.animate()
            .alpha(0f)
            .translationY(layoutNoticiasCarousel.getHeight())
            .setDuration(250)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                layoutNoticiasCarousel.setVisibility(View.GONE);
                layoutNoticiasCarousel.setTranslationY(0);
                layoutNoticiasCarousel.setAlpha(1f);

                // Actualizar constraint del mapa para expandirse hasta la navegación
                actualizarConstraintMapa(false);
            })
            .start();

        Log.d(TAG, "Carrusel ocultado - Mapa expandido");
    }

    /**
     * Actualiza las constraints del mapa según visibilidad del carrusel
     * @param carruselVisible true si el carrusel está visible
     */
    private void actualizarConstraintMapa(boolean carruselVisible) {
        View root = findViewById(android.R.id.content);
        if (root == null) return;

        ConstraintLayout constraintLayout = findViewById(R.id.map_container).getParent() instanceof ConstraintLayout
            ? (ConstraintLayout) findViewById(R.id.map_container).getParent()
            : null;

        if (constraintLayout == null) return;

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        if (carruselVisible) {
            // Mapa conectado al carrusel
            constraintSet.connect(R.id.map_container, ConstraintSet.BOTTOM,
                R.id.layout_noticias_carousel, ConstraintSet.TOP);
        } else {
            // Mapa conectado directamente a la navegación
            constraintSet.connect(R.id.map_container, ConstraintSet.BOTTOM,
                R.id.bottom_nav_container, ConstraintSet.TOP);
        }

        constraintSet.applyTo(constraintLayout);
    }


    /**
     * Inicializa el callback de ubicación para detectar cambios
     */
    private void inicializarLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.w(TAG, "⚠️ locationCallback recibió null");
                    return;
                }

                Log.d(TAG, "📍 locationCallback.onLocationResult() - " + locationResult.getLocations().size() + " ubicaciones");

                for (Location location : locationResult.getLocations()) {
                    boolean esNueva = (ubicacionActual == null);
                    ubicacionActual = location;

                    Log.i(TAG, "✅ GPS actualizado: " +
                          location.getLatitude() + ", " + location.getLongitude() +
                          " (precisión: " + location.getAccuracy() + "m)" +
                          (esNueva ? " [PRIMERA VEZ]" : ""));

                    // Actualizar marcadores cuando cambia la ubicación (pero no la primera vez)
                    if (noticias != null && !noticias.isEmpty() && !esNueva) {
                        Log.d(TAG, "🔄 Actualizando marcadores por cambio de ubicación...");
                        agregarMarcadores(noticias);
                    }

                    // Actualizar carrusel con noticias más cercanas a la nueva ubicación
                    if (noticias != null && !noticias.isEmpty()) {
                        Log.d(TAG, "🔄 Actualizando carrusel con noticias cercanas...");
                        actualizarCarruselConNoticiasCercanas();
                    }
                }
            }
        };
    }

    /**
     * Verifica y solicita permisos de ubicación
     */
    private void verificarPermisosUbicacion() {
        // Usar LocationHelper para verificar y solicitar permisos
        getLocationHelper().checkAndRequestLocationPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, habilitar ubicación en el mapa
                if (mMap != null) {
                    habilitarMiUbicacion();
                    centrarEnMiUbicacion();
                }
            } else {
                showToast("Permiso de ubicación denegado. No se puede mostrar tu posición.",
                        Toast.LENGTH_LONG);
            }
        }
    }

    private void inicializarMapa() {
        // Obtener el fragment del mapa
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_container);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "MapFragment no encontrado en layout");
            Toast.makeText(this,
                    "Error al cargar el mapa",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i(TAG, "Mapa de Google cargado correctamente");

        // ========================================
        // DIAGNÓSTICO: Detectar problemas de API Key
        // ========================================
        mMap.setOnMapLoadedCallback(() -> {
            Log.i(TAG, "✅ Callback onMapLoaded: Mapa completamente cargado");
        });

        // Verificar después de 5 segundos si las tiles cargaron
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Logs de diagnóstico
            Log.w(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            Log.w(TAG, "⚠️ DIAGNÓSTICO AUTOMÁTICO DE GOOGLE MAPS");
            Log.w(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            Log.w(TAG, "");
            Log.w(TAG, "Si ves el mapa GRIS sin calles:");
            Log.w(TAG, "");
            Log.w(TAG, "CAUSAS POSIBLES:");
            Log.w(TAG, "  1. Restricciones de API Key incorrectas");
            Log.w(TAG, "  2. SHA-1 no coincide con Google Cloud Console");
            Log.w(TAG, "  3. Paquete incorrecto en restricciones");
            Log.w(TAG, "");
            Log.w(TAG, "📋 TU CONFIGURACIÓN ACTUAL:");
            Log.w(TAG, "  Paquete: com.tesistitulacion.noticiaslocales");
            Log.w(TAG, "  SHA-1: 51:49:02:BE:1D:1B:41:5B:C7:3E:34:A6:29:52:6A:F8:A7:F7:ED:DF");
            Log.w(TAG, "");
            Log.w(TAG, "✅ SOLUCIÓN:");
            Log.w(TAG, "  1. Abre: ACCION_INMEDIATA_GOOGLE_CLOUD.txt");
            Log.w(TAG, "  2. O ejecuta: verificar-configuracion-maps.bat");
            Log.w(TAG, "  3. Sigue los 3 pasos para configurar restricciones");
            Log.w(TAG, "");
            Log.w(TAG, "🔗 Enlace directo:");
            Log.w(TAG, "  https://console.cloud.google.com/apis/credentials");
            Log.w(TAG, "");
            Log.w(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            // Mostrar Toast al usuario
            Toast.makeText(this,
                    "⚠️ Si el mapa no muestra calles:\n" +
                    "Verifica restricciones de API Key en Google Cloud Console\n" +
                    "(Ver logs con 'adb logcat | grep MapaActivity')",
                    Toast.LENGTH_LONG).show();
        }, 5000);

        // Configurar mapa con estilo mejorado
        configurarMapa();

        // Animación suave de aparición del mapa
        View mapView = getSupportFragmentManager().findFragmentById(R.id.map_container).getView();
        if (mapView != null) {
            mapView.setAlpha(0f);
            mapView.animate()
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        }

        // Si se recibieron coordenadas desde el Intent, centrar en esa ubicación
        if (latitudIntent != null && longitudIntent != null) {
            Log.i(TAG, "Centrando mapa en ubicación de la noticia: " + latitudIntent + ", " + longitudIntent);
            LatLng ubicacionNoticia = new LatLng(latitudIntent, longitudIntent);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionNoticia, ZOOM_LEVEL), 1500, null);

            // Mostrar mensaje
            String mensaje = tituloIntent != null ?
                "📍 Ubicación de: " + tituloIntent :
                "📍 Ubicación de la noticia";
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        } else {
            // Comportamiento normal: centrar en ubicación del usuario
            // Verificar si tenemos permisos de ubicación
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "✅ Permisos de ubicación concedidos - Habilitando GPS");

                // Habilitar capa de ubicación y GPS
                habilitarMiUbicacion();

                // Centrar en mi ubicación
                centrarEnMiUbicacion();
            } else {
                Log.w(TAG, "⚠️ Sin permisos de ubicación - Centrando en Ibarra");
                // Sin permisos, centrar en Ibarra con zoom medio para ver toda la ciudad
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(IBARRA_CENTRO, 14f), 1000, null);
            }
        }

        // Cargar noticias y mostrar marcadores
        cargarNoticiasEnMapa();
    }

    /**
     * Habilita la capa de "Mi ubicación" en el mapa
     */
    private void habilitarMiUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Verificar si el GPS está activado
            android.location.LocationManager locationManager =
                (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);

            boolean gpsEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

            Log.d(TAG, "Estado GPS: " + (gpsEnabled ? "✅ Activado" : "❌ Desactivado"));
            Log.d(TAG, "Estado Red: " + (networkEnabled ? "✅ Activado" : "❌ Desactivado"));

            if (!gpsEnabled && !networkEnabled) {
                Log.e(TAG, "❌ GPS y Red desactivados - No se puede obtener ubicación");
                Toast.makeText(this,
                        "⚠️ GPS desactivado. Por favor activa la ubicación en Configuración.",
                        Toast.LENGTH_LONG).show();

                // Opcional: Mostrar diálogo para ir a configuración
                mostrarDialogoActivarGPS();
            } else if (!gpsEnabled) {
                Log.w(TAG, "⚠️ GPS desactivado, solo usando ubicación por red (menos precisa)");
                Toast.makeText(this,
                        "⚠️ Para mejor precisión, activa el GPS en tu dispositivo",
                        Toast.LENGTH_LONG).show();
            }

            try {
                mMap.setMyLocationEnabled(true);
                Log.i(TAG, "✅ Capa de ubicación habilitada en el mapa");

                // Iniciar actualización continua de ubicación
                iniciarActualizacionUbicacion();
            } catch (SecurityException e) {
                Log.e(TAG, "❌ Error al habilitar ubicación: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "❌ No hay permisos de ubicación");
        }
    }

    /**
     * Inicia la actualización continua de ubicación
     */
    private void iniciarActualizacionUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "⚠️ No hay permiso de ubicación en iniciarActualizacionUbicacion");
            return;
        }

        Log.d(TAG, "📍 iniciarActualizacionUbicacion() - Configurando GPS...");

        // Configuración mejorada para mejor precisión
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(LOCATION_UPDATE_INTERVAL)        // Actualizar cada 10 segundos
                .setFastestInterval(LOCATION_FASTEST_INTERVAL) // Mínimo cada 5 segundos
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // GPS + WiFi + Celular
                .setSmallestDisplacement(5f);  // Solo actualizar si se movió al menos 5 metros

        getLocationHelper().getFusedLocationClient().requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());

        Log.i(TAG, "✅ requestLocationUpdates() registrado con PRIORITY_HIGH_ACCURACY");

        // Obtener última ubicación conocida inmediatamente
        Log.d(TAG, "🔍 Solicitando getLastLocation()...");
        getLocationHelper().getFusedLocationClient().getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        ubicacionActual = location;
                        Log.i(TAG, "✅ getLastLocation() exitoso: " +
                              location.getLatitude() + ", " + location.getLongitude());

                        // Centrar mapa en ubicación actual con animación suave
                        LatLng miPosicion = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miPosicion, ZOOM_LEVEL), 1000, null);
                        Log.d(TAG, "📍 Mapa centrado en: " + miPosicion.toString());

                        // Actualizar marcadores cercanos
                        if (noticias != null && !noticias.isEmpty()) {
                            agregarMarcadores(noticias);
                        }
                    } else {
                        Log.w(TAG, "⚠️ getLastLocation() retornó null - GPS no tiene ubicación previa");
                        Log.w(TAG, "⏳ Esperando que locationCallback reciba primera actualización GPS...");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error en getLastLocation(): " + e.getMessage());
                });
    }

    /**
     * Centra la cámara en la ubicación actual del usuario (versión simple sin animaciones)
     */
    private void centrarEnMiUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getLocationHelper().getFusedLocationClient().getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        ubicacionActual = location;
                        LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, ZOOM_LEVEL), 1500, null);

                        Log.i(TAG, "Mapa centrado en ubicación actual: " + miUbicacion);
                        Toast.makeText(this, "📍 Mapa centrado en tu ubicación", Toast.LENGTH_SHORT).show();

                        if (noticias != null && !noticias.isEmpty()) {
                            agregarMarcadores(noticias);
                        }
                    } else {
                        Log.w(TAG, "No se pudo obtener ubicación actual");
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(IBARRA_CENTRO, 14f), 1000, null);
                        Toast.makeText(this, "No se pudo obtener tu ubicación. Mostrando Ibarra.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener ubicación: " + e.getMessage());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(IBARRA_CENTRO, 14f), 1000, null);
                });
    }

    /**
     * Centra la cámara en la ubicación actual con animación mejorada (bounce effect)
     */
    private void centrarEnMiUbicacionConAnimacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (animacionEnProgreso) {
            Log.d(TAG, "Animación en progreso, ignorando click");
            return;
        }

        getLocationHelper().getFusedLocationClient().getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        ubicacionActual = location;
                        LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

                        // Animación con bounce: primero zoom out, luego zoom in con overshoot
                        animarCamaraConBounce(miUbicacion, ZOOM_LEVEL);

                        Log.i(TAG, "Mapa centrado con animación en: " + miUbicacion);
                        Toast.makeText(this, "📍 Mapa centrado en tu ubicación", Toast.LENGTH_SHORT).show();

                        // Actualizar marcadores cercanos después de la animación
                        handler.postDelayed(() -> {
                            if (noticias != null && !noticias.isEmpty()) {
                                agregarMarcadores(noticias);
                            }
                        }, 1000);
                    } else {
                        Log.w(TAG, "No se pudo obtener ubicación actual");
                        animarCamaraConBounce(IBARRA_CENTRO, 14f);
                        Toast.makeText(this, "No se pudo obtener tu ubicación. Mostrando Ibarra.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener ubicación: " + e.getMessage());
                    animarCamaraConBounce(IBARRA_CENTRO, 14f);
                });
    }

    /**
     * Anima la cámara con efecto bounce (zoom out -> zoom in con overshoot)
     */
    private void animarCamaraConBounce(LatLng destino, float zoomFinal) {
        if (mMap == null) return;

        animacionEnProgreso = true;
        float zoomActual = mMap.getCameraPosition().zoom;

        // Fase 1: Zoom out suave
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(destino, Math.max(zoomActual - 2, MIN_ZOOM)),
            400,
            new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    // Fase 2: Zoom in con overshoot
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(destino)
                        .zoom(zoomFinal)
                        .bearing(0)
                        .tilt(0)
                        .build();

                    mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        800,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                animacionEnProgreso = false;
                            }

                            @Override
                            public void onCancel() {
                                animacionEnProgreso = false;
                            }
                        }
                    );
                }

                @Override
                public void onCancel() {
                    animacionEnProgreso = false;
                }
            }
        );
    }

    private void configurarMapa() {
        // Tipo de mapa normal (calles)
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // ========================================
        // RESTRICCIÓN: SOLO IBARRA-URCUQUÍ (si está en modo producción)
        // ========================================

        if (!MODO_DESARROLLO) {
            // MODO PRODUCCIÓN: Restringir solo a Ibarra-Urcuquí
            Log.i(TAG, "Modo PRODUCCIÓN: Mapa restringido a Ibarra-Urcuquí");

            // Límites geográficos de Ibarra-Urcuquí
            LatLngBounds ibarraBounds = new LatLngBounds(IBARRA_SW, IBARRA_NE);

            // Restringir cámara a los límites de Ibarra-Urcuquí
            mMap.setLatLngBoundsForCameraTarget(ibarraBounds);

            // Limitar zoom (para que no se pueda alejar mucho y ver otras ciudades)
            mMap.setMinZoomPreference(MIN_ZOOM);
            mMap.setMaxZoomPreference(MAX_ZOOM);
        } else {
            // MODO DESARROLLO: Sin restricciones geográficas
            // Solo limitar zoom extremos
            mMap.setMinZoomPreference(5f);
            mMap.setMaxZoomPreference(MAX_ZOOM);
        }

        // ========================================
        // CONFIGURACIÓN UI MEJORADA
        // ========================================

        // Controles de zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Controles de navegación
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // Botón para centrar en mi ubicación

        // Gestos de mapa (todos habilitados para experiencia dinámica)
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);

        // Habilitar rotación para experiencia más dinámica
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        // Habilitar inclinación para vista 3D más dinámica
        mMap.getUiSettings().setTiltGesturesEnabled(true);

        // Habilitar todos los gestos (táctil, zoom, etc.)
        mMap.getUiSettings().setAllGesturesEnabled(true);

        // Habilitar barra de herramientas del mapa (compartir, navegación)
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Habilitar capa de "Mi ubicación"
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "No hay permisos de ubicación: " + e.getMessage());
        }

        // Configuración visual mejorada
        mMap.setBuildingsEnabled(true); // Mostrar edificios en 3D
        mMap.setIndoorEnabled(true);    // Mapas de interiores
        mMap.setTrafficEnabled(false);  // Sin tráfico para mantener limpio

        // ========================================
        // LISTENERS INTERACTIVOS
        // ========================================

        // Listener cuando la cámara se detiene (después de zoom o pan)
        mMap.setOnCameraIdleListener(() -> {
            Log.d(TAG, "Cámara del mapa detenida - actualizando lista de noticias visibles");
            actualizarNoticiasVisibles();
        });

        // Listener cuando la cámara se está moviendo
        mMap.setOnCameraMoveListener(() -> {
            // Feedback visual sutil durante el movimiento
            // Log.v(TAG, "Cámara en movimiento");
        });

        // Listener cuando la cámara comienza a moverse
        mMap.setOnCameraMoveStartedListener(reason -> {
            String razonTexto;
            switch (reason) {
                case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:
                    razonTexto = "Usuario movió el mapa";
                    break;
                case GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION:
                    razonTexto = "Animación programática";
                    break;
                case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                    razonTexto = "Animación de desarrollador";
                    break;
                default:
                    razonTexto = "Razón desconocida";
            }
            Log.v(TAG, "Cámara comenzó a moverse: " + razonTexto);
        });

        // Listener para clicks en el mapa (fuera de marcadores)
        mMap.setOnMapClickListener(latLng -> {
            Log.d(TAG, "Click en mapa: " + latLng.toString());
            // Deseleccionar marcador si hay uno seleccionado
            if (markerSeleccionado != null) {
                markerSeleccionado = null;
            }
        });

        // Listener para long clicks en el mapa
        mMap.setOnMapLongClickListener(latLng -> {
            Log.d(TAG, "Long click en mapa: " + latLng.toString());
            // Opcional: Mostrar coordenadas o crear marcador temporal
            Toast.makeText(this,
                String.format(java.util.Locale.US, "📍 %.4f, %.4f", latLng.latitude, latLng.longitude),
                Toast.LENGTH_SHORT).show();
        });

        // Listener para clicks en marcadores con animación
        mMap.setOnMarkerClickListener(marker -> {
            // Animar marcador con bounce
            animarMarcadorClick(marker);

            // Obtener noticia asociada al marcador
            Noticia noticia = (Noticia) marker.getTag();

            if (noticia != null) {
                // Guardar marcador seleccionado
                markerSeleccionado = marker;

                // Centrar cámara con animación suave
                LatLng posicion = marker.getPosition();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(posicion)
                    .zoom(mMap.getCameraPosition().zoom)
                    .build();

                mMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    500,
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            // Abrir detalle después de la animación
                            if (noticia.getFirestoreId() != null) {
                                Intent intent = new Intent(MapaActivity.this, DetalleNoticiaActivity.class);
                                intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticia.getFirestoreId());
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancel() {
                            // Si se cancela, abrir igual
                            if (noticia.getFirestoreId() != null) {
                                Intent intent = new Intent(MapaActivity.this, DetalleNoticiaActivity.class);
                                intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticia.getFirestoreId());
                                startActivity(intent);
                            }
                        }
                    }
                );
            }

            return true; // Consumir el evento
        });
    }

    /**
     * Anima el marcador cuando se hace click (efecto bounce)
     */
    private void animarMarcadorClick(final Marker marker) {
        if (marker == null) return;

        // Crear animación de bounce
        final Handler handlerBounce = new Handler();
        final long startTime = System.currentTimeMillis();
        final long duration = 600;

        handlerBounce.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                float t = Math.min(1.0f, (float) elapsed / duration);

                // Función de bounce
                float bounce = (float) Math.abs(Math.sin(t * Math.PI * 2) * (1 - t));

                // No podemos animar directamente el marcador, pero mostraremos el efecto
                // mediante la animación de zoom sutil
                if (t < 1.0f) {
                    handlerBounce.postDelayed(this, 16); // ~60 FPS
                }
            }
        });
    }

    /**
     * Aplica efecto de drop animation escalonado a los marcadores
     * Simula que los marcadores "caen" al mapa uno tras otro
     */
    private void aplicarDropAnimationMarcadores(final List<Marker> marcadores) {
        if (marcadores == null || marcadores.isEmpty()) return;

        // Limitar cantidad de marcadores animados para no saturar
        final int maxMarcadoresAnimados = Math.min(marcadores.size(), 20);
        final long delayEntreMarcadores = 50; // 50ms entre cada marcador

        for (int i = 0; i < maxMarcadoresAnimados; i++) {
            final int index = i;
            final Marker marker = marcadores.get(i);

            // Delay escalonado para cada marcador
            handler.postDelayed(() -> {
                if (marker != null && marker.isVisible()) {
                    // Mostrar marcador (ya están agregados, solo hacemos efecto visual)
                    // En versiones avanzadas de Google Maps se podría usar Marker.setVisible()
                    // pero aquí usamos un enfoque simulado

                    // Efecto visual: mostrar marcador con alpha
                    marker.setAlpha(0f);

                    // Animar alpha de 0 a 1 con ValueAnimator
                    ValueAnimator alphaAnimator = ValueAnimator.ofFloat(0f, 1f);
                    alphaAnimator.setDuration(300);
                    alphaAnimator.setInterpolator(new DecelerateInterpolator());
                    alphaAnimator.addUpdateListener(animation -> {
                        float alpha = (float) animation.getAnimatedValue();
                        if (marker.isVisible()) {
                            marker.setAlpha(alpha);
                        }
                    });
                    alphaAnimator.start();

                    Log.v(TAG, "Marcador " + (index + 1) + " animado (drop effect)");
                }
            }, i * delayEntreMarcadores);
        }

        // Marcadores restantes (si hay más de 20) aparecen sin animación
        if (marcadores.size() > maxMarcadoresAnimados) {
            handler.postDelayed(() -> {
                for (int i = maxMarcadoresAnimados; i < marcadores.size(); i++) {
                    Marker marker = marcadores.get(i);
                    if (marker != null) {
                        marker.setAlpha(1f);
                    }
                }
                Log.d(TAG, (marcadores.size() - maxMarcadoresAnimados) + " marcadores adicionales mostrados sin animación");
            }, maxMarcadoresAnimados * delayEntreMarcadores + 200);
        }

        Log.i(TAG, "Drop animation aplicada a " + maxMarcadoresAnimados + " marcadores");
    }


    private void cargarNoticiasEnMapa() {
        // Usar FirebaseCallbackHelper para gestión automática de estado (modo silencioso para el mapa)
        FirebaseCallbackHelper.<List<Noticia>>cargarDatosSilencioso(
            this,
            "noticias para mapa",
            getLoadingStateManager(),
            callback -> FirebaseManager.getInstance().getAllNoticiasRealtime(callback),
            noticiasObtenidas -> {
                if (noticiasObtenidas != null && !noticiasObtenidas.isEmpty()) {
                    noticias = noticiasObtenidas;
                    agregarMarcadores(noticias);

                    Log.i(TAG, "Noticias cargadas en mapa (tiempo real): " + noticias.size());

                    // Actualizar carrusel con noticias más cercanas
                    actualizarCarruselConNoticiasCercanas();

                    // Contar noticias con coordenadas para el mensaje
                    int noticiasConCoords = 0;
                    for (Noticia n : noticias) {
                        if (n.getLatitud() != null && n.getLongitud() != null) {
                            noticiasConCoords++;
                        }
                    }

                    // Actualizar lista de noticias visibles
                    actualizarNoticiasVisibles();
                } else {
                    Log.w(TAG, "No hay noticias disponibles");
                }
            }
        );
    }

    private void agregarMarcadores(List<Noticia> noticias) {
        if (mMap == null) {
            Log.e(TAG, "Mapa no inicializado");
            return;
        }

        // Limpiar marcadores existentes
        mMap.clear();

        List<LatLng> posiciones = new ArrayList<>();
        final List<Marker> marcadoresNuevos = new ArrayList<>();
        int marcadoresAgregados = 0;
        int noticiasFiltradas = 0;

        // Verificar si está activado el modo "mostrar solo cercanas"
        boolean mostrarSoloCercanas = UsuarioPreferences.getMostrarSoloCercanas(this);
        double radioActual = mostrarSoloCercanas ? RADIO_OCULTAR_NOTICIAS_KM : RADIO_BUSQUEDA_KM;

        for (Noticia noticia : noticias) {
            // Solo agregar si tiene coordenadas válidas
            if (noticia.getLatitud() != null && noticia.getLongitud() != null) {
                // Filtrar por proximidad SOLO si el modo "mostrar solo cercanas" está activo
                if (mostrarSoloCercanas) {
                    if (ubicacionActual != null) {
                        // Calcular distancia desde ubicación actual
                        float[] results = new float[1];
                        Location.distanceBetween(
                                ubicacionActual.getLatitude(),
                                ubicacionActual.getLongitude(),
                                noticia.getLatitud(),
                                noticia.getLongitud(),
                                results
                        );

                        double distanciaKm = results[0] / 1000.0;

                        // Solo agregar marcador si está dentro del radio
                        if (distanciaKm > radioActual) {
                            noticiasFiltradas++;
                            continue; // Saltar esta noticia, está muy lejos
                        }
                    } else {
                        // Si modo cercanas está activo pero no hay ubicación, no mostrar nada
                        noticiasFiltradas++;
                        continue;
                    }
                }
                // Si mostrarSoloCercanas = false, mostrar TODAS las noticias sin filtrar

                LatLng posicion = new LatLng(noticia.getLatitud(), noticia.getLongitud());

                // Crear marcador avanzado con color según categoría
                Marker marker = agregarMarcadorNoticia(noticia, posicion);

                if (marker != null) {
                    // Asociar noticia al marcador para recuperarla en el click
                    marker.setTag(noticia);
                    marcadoresNuevos.add(marker);
                    posiciones.add(posicion);
                    marcadoresAgregados++;
                }
            }
        }

        // Aplicar efecto drop animation escalonado a los marcadores
        aplicarDropAnimationMarcadores(marcadoresNuevos);

        String modoTexto = mostrarSoloCercanas ? " [MODO POKÉMON GO]" : "";

        Log.i(TAG, "Marcadores agregados: " + marcadoresAgregados +
                   " (filtradas por estar fuera de los " + radioActual + "km: " + noticiasFiltradas + ")" + modoTexto);

        // No ajustar la cámara automáticamente si ya está centrada en el usuario
        // Solo ajustar si es la primera carga y no hay ubicación del usuario
        if (!posiciones.isEmpty() && ubicacionActual == null) {
            ajustarCamaraATodosLosMarcadores(posiciones);
        } else if (posiciones.isEmpty() && ubicacionActual != null) {
            // Si no hay noticias cercanas, centrar en ubicación actual
            LatLng miPosicion = new LatLng(ubicacionActual.getLatitude(), ubicacionActual.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miPosicion, ZOOM_LEVEL), 1000, null);
            Log.i(TAG, "No hay noticias cercanas, centrando en ubicación actual");
        }
    }

    private Marker agregarMarcadorNoticia(Noticia noticia, LatLng posicion) {
        try {
            // Obtener icono personalizado según categoría
            int iconoResId = obtenerIconoCategoria(noticia.getCategoriaId());

            // Convertir vector drawable a Bitmap
            Bitmap iconoBitmap = getBitmapFromVectorDrawable(iconoResId);

            // Crear opciones de marcador con icono personalizado
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(posicion)
                    .title(noticia.getTitulo())
                    .snippet(noticia.getDescripcion())
                    .icon(BitmapDescriptorFactory.fromBitmap(iconoBitmap));

            // Agregar marcador al mapa
            Marker marker = mMap.addMarker(markerOptions);

            // Guardar relación marker-noticia para proximidad y para el tag
            if (marker != null) {
                marker.setTag(noticia);
                markerNoticiaMap.put(marker, noticia);
            }

            return marker;

        } catch (Exception e) {
            Log.e(TAG, "Error al agregar marcador: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convierte un drawable vectorial a Bitmap para usar en marcadores de mapa
     * @param drawableId ID del recurso drawable
     * @return Bitmap del icono
     */
    private Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        if (drawable == null) {
            Log.e(TAG, "No se pudo obtener drawable con ID: " + drawableId);
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Obtiene el icono personalizado según la categoría de la noticia
     * Retorna el recurso drawable correspondiente
     */
    private int obtenerIconoCategoria(Integer categoriaId) {
        if (categoriaId == null) {
            return R.drawable.ic_marker_politica; // Icono por defecto
        }

        // Iconos personalizados según categoría
        switch (categoriaId) {
            case 1: return R.drawable.ic_marker_politica;        // Política
            case 2: return R.drawable.ic_marker_economia;        // Economía
            case 3: return R.drawable.ic_marker_cultura;         // Cultura
            case 4: return R.drawable.ic_marker_deportes;        // Deportes
            case 5: return R.drawable.ic_marker_educacion;       // Educación
            case 6: return R.drawable.ic_marker_salud;           // Salud
            case 7: return R.drawable.ic_marker_seguridad;       // Seguridad
            case 8: return R.drawable.ic_marker_medio_ambiente;  // Medio Ambiente
            case 9: return R.drawable.ic_marker_turismo;         // Turismo
            case 10: return R.drawable.ic_marker_tecnologia;     // Tecnología
            default: return R.drawable.ic_marker_politica;       // Por defecto
        }
    }

    /**
     * Obtiene color según categoría de la noticia
     * Usa los colores definidos en el sistema de diseño
     */
    private int obtenerColorCategoria(Integer categoriaId) {
        if (categoriaId == null) {
            return androidx.core.content.ContextCompat.getColor(this, R.color.cat_politica);
        }

        // Colores según categoría (usando los del sistema de diseño)
        switch (categoriaId) {
            case 1: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_politica);
            case 2: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_economia);
            case 3: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_cultura);
            case 4: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_deportes);
            case 5: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_educacion);
            case 6: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_salud);
            case 7: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_seguridad);
            case 8: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_medio_ambiente);
            case 9: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_turismo);
            case 10: return androidx.core.content.ContextCompat.getColor(this, R.color.cat_tecnologia);
            default: return androidx.core.content.ContextCompat.getColor(this, R.color.md_theme_light_secondary);
        }
    }

    /**
     * Ajusta la cámara para mostrar todos los marcadores con animación suave
     */
    private void ajustarCamaraATodosLosMarcadores(List<LatLng> posiciones) {
        if (posiciones.isEmpty()) return;

        try {
            // Crear bounds que contenga todos los marcadores
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng posicion : posiciones) {
                builder.include(posicion);
            }
            LatLngBounds bounds = builder.build();

            // Padding en píxeles alrededor de los marcadores (mayor para mejor visualización)
            int padding = 120;

            // Animar cámara con duración personalizada para mostrar todos los marcadores
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, padding),
                1200, // Duración más larga para animación suave
                new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        Log.d(TAG, "Cámara ajustada a todos los marcadores");
                        // Pequeño zoom out adicional para mejor contexto
                        handler.postDelayed(() -> {
                            if (mMap != null) {
                                float zoomActual = mMap.getCameraPosition().zoom;
                                mMap.animateCamera(
                                    CameraUpdateFactory.zoomTo(Math.max(zoomActual - 0.5f, MIN_ZOOM)),
                                    300,
                                    null
                                );
                            }
                        }, 200);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Animación de cámara cancelada");
                    }
                }
            );

        } catch (Exception e) {
            Log.e(TAG, "Error al ajustar cámara: " + e.getMessage(), e);
        }
    }

    // ==================== LISTA DE NOTICIAS VISIBLES ====================

    /**
     * Actualiza la lista de noticias visibles en el área del mapa
     */
    private void actualizarNoticiasVisibles() {
        if (mMap == null || noticias == null || noticias.isEmpty()) {
            Log.d(TAG, "No se puede actualizar lista: mapa o noticias no disponibles");
            return;
        }

        // Obtener límites visibles del mapa
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        // Filtrar noticias que están dentro de los límites visibles
        List<Noticia> noticiasVisibles = new ArrayList<>();
        for (Noticia noticia : noticias) {
            if (noticia.getLatitud() != null && noticia.getLongitud() != null) {
                LatLng posicion = new LatLng(noticia.getLatitud(), noticia.getLongitud());
                if (bounds.contains(posicion)) {
                    noticiasVisibles.add(noticia);
                }
            }
        }

        // Actualizar contador con animación
        if (tvContadorNoticias != null) {
            animarContadorNoticias(tvContadorNoticias.getText().toString(), String.valueOf(noticiasVisibles.size()));
        }

        // Mantener card oculto (sin adaptador)
        if (cardListaNoticias != null) {
            cardListaNoticias.setVisibility(View.GONE);
        }

        Log.d(TAG, "Lista actualizada: " + noticiasVisibles.size() + " noticias visibles de " + noticias.size() + " totales");
    }

    /**
     * Anima el cambio del contador de noticias con efecto pulse
     */
    private void animarContadorNoticias(String valorAnterior, String nuevoValor) {
        if (tvContadorNoticias == null) return;

        // Si el valor no cambió, no animar
        if (valorAnterior.equals(nuevoValor)) return;

        // Animación de pulse y cambio de valor
        tvContadorNoticias.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .alpha(0.5f)
            .setDuration(150)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                tvContadorNoticias.setText(nuevoValor);
                tvContadorNoticias.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(150)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .start();
            })
            .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Detener actualización de ubicación (BaseActivity también maneja esto)
        if (locationCallback != null) {
            getLocationHelper().getFusedLocationClient().removeLocationUpdates(locationCallback);
        }

        // Limpiar handler
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        Log.i(TAG, "MapaActivity destruido, ubicación detenida");
    }

    /**
     * Muestra un diálogo para que el usuario active el GPS
     */
    private void mostrarDialogoActivarGPS() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("GPS Desactivado")
                .setMessage("La función de proximidad requiere que el GPS esté activado.\n\n¿Deseas ir a Configuración para activarlo?")
                .setPositiveButton("Ir a Configuración", (dialog, which) -> {
                    // Abrir configuración de ubicación
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(this,
                            "Sin GPS, las notificaciones de proximidad no funcionarán",
                            Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false)
                .show();
    }
}
