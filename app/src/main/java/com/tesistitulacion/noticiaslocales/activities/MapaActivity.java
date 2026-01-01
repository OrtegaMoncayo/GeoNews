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
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.card.MaterialCardView;

/**
 * Pantalla de mapa con noticias georreferenciadas
 * Usa Google Maps SDK con Advanced Markers
 * Extiende de BaseActivity para tener navegación automática
 */
public class MapaActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String TAG = "MapaActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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

    // Radio para modo "mostrar solo cercanas" (estilo Pokémon GO)
    private static final double RADIO_POKEMON_GO_KM = 0.5; // 500 metros (muy cercanas)

    private GoogleMap mMap;
    private List<Noticia> noticias;
    private boolean cargando = false;
    private FusedLocationProviderClient fusedLocationClient;

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
    private MaterialCardView cardListaNoticias;
    private RecyclerView rvNoticiasVisibles;
    private TextView tvContadorNoticias;
    private ImageButton btnCerrarLista;

    // Views del carrusel de noticias
    private LinearLayout layoutNoticiasCarousel;
    private RecyclerView rvNoticiasMapa;
    private com.google.android.material.floatingactionbutton.FloatingActionButton btnCerrarCarousel;
    private com.tesistitulacion.noticiaslocales.adapters.NoticiaMapaAdapter noticiaMapaAdapter;

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

        // Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicializar lista de noticias visibles
        inicializarListaNoticias();

        // Inicializar carrusel de noticias
        inicializarCarousel();

        // Inicializar botón de ubicación
        inicializarBotonUbicacion();

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

        // Listener del botón cerrar
        if (btnCerrarLista != null) {
            btnCerrarLista.setOnClickListener(v -> {
                cardListaNoticias.setVisibility(View.GONE);
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

        // Listener del botón cerrar
        if (btnCerrarCarousel != null) {
            btnCerrarCarousel.setOnClickListener(v -> {
                layoutNoticiasCarousel.setVisibility(View.GONE);
            });
        }

        // Inicialmente oculto
        layoutNoticiasCarousel.setVisibility(View.GONE);

        Log.d(TAG, "Carrusel de noticias inicializado");
    }

    /**
     * Inicializa el botón de "Mi ubicación"
     */
    private void inicializarBotonUbicacion() {
        com.google.android.material.floatingactionbutton.FloatingActionButton fabMiUbicacion =
            findViewById(R.id.fab_mi_ubicacion);

        if (fabMiUbicacion != null) {
            fabMiUbicacion.setOnClickListener(v -> {
                centrarEnMiUbicacion();
            });
        }

        Log.d(TAG, "Botón de ubicación inicializado");
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
                }
            }
        };
    }

    /**
     * Verifica y solicita permisos de ubicación
     */
    private void verificarPermisosUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, habilitar ubicación en el mapa
                if (mMap != null) {
                    habilitarMiUbicacion();
                    centrarEnMiUbicacion();
                }
            } else {
                Toast.makeText(this,
                        "Permiso de ubicación denegado. No se puede mostrar tu posición.",
                        Toast.LENGTH_LONG).show();
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

        // Configurar mapa
        configurarMapa();

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

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());

        Log.i(TAG, "✅ requestLocationUpdates() registrado con PRIORITY_HIGH_ACCURACY");

        // Obtener última ubicación conocida inmediatamente
        Log.d(TAG, "🔍 Solicitando getLastLocation()...");
        fusedLocationClient.getLastLocation()
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
     * Centra la cámara en la ubicación actual del usuario
     */
    private void centrarEnMiUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        ubicacionActual = location;
                        // Obtener ubicación actual
                        LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());

                        // Centrar cámara en mi ubicación con animación suave
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, ZOOM_LEVEL), 1500, null);

                        Log.i(TAG, "Mapa centrado en ubicación actual: " + miUbicacion);
                        Toast.makeText(this,
                                "📍 Mapa centrado en tu ubicación",
                                Toast.LENGTH_SHORT).show();

                        // Actualizar marcadores cercanos
                        if (noticias != null && !noticias.isEmpty()) {
                            agregarMarcadores(noticias);
                        }
                    } else {
                        // No hay ubicación disponible, centrar en Ibarra con zoom medio
                        Log.w(TAG, "No se pudo obtener ubicación actual");
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(IBARRA_CENTRO, 14f), 1000, null);
                        Toast.makeText(this,
                                "No se pudo obtener tu ubicación. Mostrando Ibarra.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener ubicación: " + e.getMessage());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(IBARRA_CENTRO, 14f), 1000, null);
                });
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
            Log.i(TAG, "Modo DESARROLLO: Mapa sin restricciones geográficas");
            Toast.makeText(this,
                    "⚠️ Modo desarrollo: Sin restricciones de área",
                    Toast.LENGTH_SHORT).show();

            // Solo limitar zoom extremos
            mMap.setMinZoomPreference(5f);  // Puedes alejar más
            mMap.setMaxZoomPreference(MAX_ZOOM);
        }

        // Controles de UI
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true); // Botón para centrar en mi ubicación

        // Habilitar capa de "Mi ubicación"
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "No hay permisos de ubicación: " + e.getMessage());
        }

        // Deshabilitar rotación (mantener el norte arriba)
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        // Deshabilitar inclinación (vista 3D)
        mMap.getUiSettings().setTiltGesturesEnabled(false);

        // Listener para cuando la cámara del mapa se mueva
        mMap.setOnCameraIdleListener(() -> {
            Log.d(TAG, "Cámara del mapa detenida - actualizando lista de noticias visibles");
            actualizarNoticiasVisibles();
        });

        // Listener para clicks en marcadores
        mMap.setOnMarkerClickListener(marker -> {
            // Obtener noticia asociada al marcador
            Noticia noticia = (Noticia) marker.getTag();

            if (noticia != null) {
                // Centrar cámara en el marcador y abrir detalle
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                // Abrir detalle de noticia
                if (noticia.getFirestoreId() != null) {
                    Intent intent = new Intent(this, DetalleNoticiaActivity.class);
                    intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticia.getFirestoreId());
                    startActivity(intent);
                }
            }

            return true; // Consumir el evento
        });
    }


    private void cargarNoticiasEnMapa() {
        if (cargando) {
            Log.d(TAG, "Ya hay una carga en progreso");
            return;
        }

        Log.d(TAG, "Iniciando escucha en TIEMPO REAL de noticias para el mapa desde Firebase...");
        cargando = true;

        // Cargar desde Firebase Firestore con TIEMPO REAL
        FirebaseManager.getInstance().getAllNoticiasRealtime(new FirebaseManager.FirestoreCallback<List<Noticia>>() {
            @Override
            public void onSuccess(List<Noticia> noticiasObtenidas) {
                cargando = false;
                Log.d(TAG, "Actualización en tiempo real - Noticias para mapa: " + noticiasObtenidas.size());

                if (noticiasObtenidas != null && !noticiasObtenidas.isEmpty()) {
                    noticias = noticiasObtenidas;
                    agregarMarcadores(noticias);

                    Log.i(TAG, "Noticias cargadas en mapa (tiempo real): " + noticias.size());

                    // Filtrar noticias con coordenadas
                    List<Noticia> noticiasConCoordenadas = new ArrayList<>();
                    for (Noticia n : noticias) {
                        if (n.getLatitud() != null && n.getLongitud() != null) {
                            noticiasConCoordenadas.add(n);
                        }
                    }

                    // Actualizar adaptador del carrusel
                    if (noticiaMapaAdapter != null) {
                        noticiaMapaAdapter.setUbicacionActual(ubicacionActual);
                        noticiaMapaAdapter.setNoticias(noticiasConCoordenadas);
                    }

                    // Mostrar carrusel si hay noticias
                    if (!noticiasConCoordenadas.isEmpty() && layoutNoticiasCarousel != null) {
                        layoutNoticiasCarousel.setVisibility(View.VISIBLE);
                    }

                    boolean mostrarSoloCercanas = UsuarioPreferences.getMostrarSoloCercanas(MapaActivity.this);
                    String mensaje;
                    if (ubicacionActual != null) {
                        if (mostrarSoloCercanas) {
                            mensaje = "✅ " + noticiasConCoordenadas.size() + " noticias muy cercanas (500m)";
                        } else {
                            mensaje = "✅ " + noticiasConCoordenadas.size() + " noticias";
                        }
                    } else {
                        mensaje = "✅ " + noticiasConCoordenadas.size() + " noticias en el mapa";
                    }
                    Toast.makeText(MapaActivity.this, mensaje, Toast.LENGTH_SHORT).show();

                    // Actualizar lista de noticias visibles
                    actualizarNoticiasVisibles();
                } else {
                    Log.w(TAG, "No hay noticias disponibles");
                    Toast.makeText(MapaActivity.this,
                            "No hay noticias disponibles",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Exception e) {
                cargando = false;
                Log.e(TAG, "Error al cargar noticias desde Firebase: " + e.getMessage(), e);
                Toast.makeText(MapaActivity.this,
                        "Error al cargar noticias: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void agregarMarcadores(List<Noticia> noticias) {
        if (mMap == null) {
            Log.e(TAG, "Mapa no inicializado");
            return;
        }

        // Limpiar marcadores existentes
        mMap.clear();

        List<LatLng> posiciones = new ArrayList<>();
        int marcadoresAgregados = 0;
        int noticiasFiltradas = 0;

        // Verificar si está activado el modo "mostrar solo cercanas"
        boolean mostrarSoloCercanas = UsuarioPreferences.getMostrarSoloCercanas(this);
        double radioActual = mostrarSoloCercanas ? RADIO_POKEMON_GO_KM : RADIO_BUSQUEDA_KM;

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
                    posiciones.add(posicion);
                    marcadoresAgregados++;
                }
            }
        }

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
     * Ajusta la cámara para mostrar todos los marcadores
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

            // Padding en píxeles alrededor de los marcadores
            int padding = 100;

            // Animar cámara para mostrar todos los marcadores
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

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

        // Actualizar contador
        if (tvContadorNoticias != null) {
            tvContadorNoticias.setText(String.valueOf(noticiasVisibles.size()));
        }

        // Mantener card oculto (sin adaptador)
        if (cardListaNoticias != null) {
            cardListaNoticias.setVisibility(View.GONE);
        }

        Log.d(TAG, "Lista actualizada: " + noticiasVisibles.size() + " noticias visibles de " + noticias.size() + " totales");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Detener actualización de ubicación
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
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
