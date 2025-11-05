package com.tesistitulacion.noticiaslocales.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PinConfig;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.db.NoticiaServiceHTTP;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de mapa con noticias georreferenciadas
 * Usa Google Maps SDK con Advanced Markers
 * Extiende de BaseActivity para tener navegación automática
 */
public class MapaActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String TAG = "MapaActivity";

    // Coordenadas de Ibarra (centro de la ciudad - El Sagrario)
    private static final LatLng IBARRA_CENTRO = new LatLng(0.3476, -78.1223);
    private static final float ZOOM_LEVEL = 12f; // Zoom para ver toda la ciudad

    // Límites geográficos de Ibarra (para restringir el mapa)
    // Southwest (esquina suroeste) - Límite sur y oeste
    private static final LatLng IBARRA_SW = new LatLng(0.25, -78.20);
    // Northeast (esquina noreste) - Límite norte y este
    private static final LatLng IBARRA_NE = new LatLng(0.45, -78.05);

    // Zoom mínimo y máximo
    private static final float MIN_ZOOM = 11f; // No se puede alejar más (para mantener en Ibarra)
    private static final float MAX_ZOOM = 18f; // Máximo acercamiento

    private GoogleMap mMap;
    private List<Noticia> noticias;
    private boolean cargando = false;

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

        inicializarMapa();
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

        // Mover cámara a Ibarra
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(IBARRA_CENTRO, ZOOM_LEVEL));

        // Cargar noticias y mostrar marcadores
        cargarNoticiasEnMapa();
    }

    private void configurarMapa() {
        // Tipo de mapa normal (calles)
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // ========================================
        // RESTRICCIÓN: SOLO IBARRA
        // ========================================

        // Límites geográficos de Ibarra
        LatLngBounds ibarraBounds = new LatLngBounds(IBARRA_SW, IBARRA_NE);

        // Restringir cámara a los límites de Ibarra
        mMap.setLatLngBoundsForCameraTarget(ibarraBounds);

        // Limitar zoom (para que no se pueda alejar mucho y ver otras ciudades)
        mMap.setMinZoomPreference(MIN_ZOOM);
        mMap.setMaxZoomPreference(MAX_ZOOM);

        // Controles de UI
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // Por ahora sin GPS

        // Deshabilitar rotación (mantener el norte arriba)
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        // Deshabilitar inclinación (vista 3D)
        mMap.getUiSettings().setTiltGesturesEnabled(false);

        // Listener para clicks en marcadores
        mMap.setOnMarkerClickListener(marker -> {
            // Obtener noticia asociada al marcador
            Noticia noticia = (Noticia) marker.getTag();

            if (noticia != null) {
                // Mostrar info básica
                Toast.makeText(this,
                        noticia.getTitulo(),
                        Toast.LENGTH_SHORT).show();

                // TODO: Abrir DetalleNoticiaActivity
                /*
                Intent intent = new Intent(this, DetalleNoticiaActivity.class);
                intent.putExtra("noticia_id", noticia.getId());
                startActivity(intent);
                */
            }

            return true; // Consumir el evento
        });

        // Listener para InfoWindow (ventana emergente del marcador)
        mMap.setOnInfoWindowClickListener(marker -> {
            Noticia noticia = (Noticia) marker.getTag();

            if (noticia != null) {
                Toast.makeText(this,
                        "Abrir detalle: " + noticia.getTitulo(),
                        Toast.LENGTH_LONG).show();

                // TODO: Implementar navegación a detalle
            }
        });
    }

    private void cargarNoticiasEnMapa() {
        if (cargando) {
            Log.d(TAG, "Ya hay una carga en progreso");
            return;
        }

        Log.d(TAG, "Cargando noticias para el mapa...");
        cargando = true;

        // Cargar en segundo plano
        new Thread(() -> {
            try {
                List<Noticia> noticiasObtenidas = NoticiaServiceHTTP.obtenerTodas();
                Log.d(TAG, "Noticias obtenidas: " +
                        (noticiasObtenidas != null ? noticiasObtenidas.size() : "null"));

                runOnUiThread(() -> {
                    cargando = false;

                    if (noticiasObtenidas != null && !noticiasObtenidas.isEmpty()) {
                        noticias = noticiasObtenidas;
                        agregarMarcadores(noticias);

                        Log.i(TAG, "Noticias cargadas en mapa: " + noticias.size());
                        Toast.makeText(this,
                                "✅ " + noticias.size() + " noticias en el mapa",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "No hay noticias con coordenadas");
                        Toast.makeText(this,
                                "No hay noticias georreferenciadas",
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

    private void agregarMarcadores(List<Noticia> noticias) {
        if (mMap == null) {
            Log.e(TAG, "Mapa no inicializado");
            return;
        }

        // Limpiar marcadores existentes
        mMap.clear();

        List<LatLng> posiciones = new ArrayList<>();
        int marcadoresAgregados = 0;

        for (Noticia noticia : noticias) {
            // Solo agregar si tiene coordenadas válidas
            if (noticia.getLatitud() != null && noticia.getLongitud() != null) {
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

        Log.i(TAG, "Marcadores agregados: " + marcadoresAgregados);

        // Ajustar cámara para mostrar todos los marcadores
        if (!posiciones.isEmpty()) {
            ajustarCamaraATodosLosMarcadores(posiciones);
        }
    }

    private Marker agregarMarcadorNoticia(Noticia noticia, LatLng posicion) {
        try {
            // Configurar pin con color según categoría
            PinConfig.Builder pinConfigBuilder = PinConfig.builder();

            // Color del marcador según categoría (puedes personalizar)
            int colorMarcador = obtenerColorCategoria(noticia.getCategoriaId());
            pinConfigBuilder.setBackgroundColor(colorMarcador);

            // Glifo con primera letra del título
            String primeraLetra = noticia.getTitulo().substring(0, 1).toUpperCase();
            PinConfig.Glyph glyph = new PinConfig.Glyph(primeraLetra, Color.WHITE);
            pinConfigBuilder.setGlyph(glyph);

            PinConfig pinConfig = pinConfigBuilder.build();

            // Crear opciones de marcador avanzado
            AdvancedMarkerOptions markerOptions = new AdvancedMarkerOptions()
                    .position(posicion)
                    .title(noticia.getTitulo())
                    .snippet(noticia.getDescripcion())
                    .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig));

            // Agregar marcador al mapa
            return mMap.addMarker(markerOptions);

        } catch (Exception e) {
            Log.e(TAG, "Error al agregar marcador: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Obtiene color según categoría de la noticia
     */
    private int obtenerColorCategoria(Integer categoriaId) {
        if (categoriaId == null) {
            return Color.parseColor("#1976D2"); // Azul por defecto
        }

        // Colores según categoría (mismo esquema que en la app)
        switch (categoriaId) {
            case 1: return Color.parseColor("#FF6B35"); // Política - Naranja
            case 2: return Color.parseColor("#004E89"); // Economía - Azul oscuro
            case 3: return Color.parseColor("#9B59B6"); // Cultura - Púrpura
            case 4: return Color.parseColor("#27AE60"); // Deportes - Verde
            case 5: return Color.parseColor("#F39C12"); // Educación - Amarillo
            case 6: return Color.parseColor("#E74C3C"); // Salud - Rojo
            case 7: return Color.parseColor("#34495E"); // Seguridad - Gris oscuro
            case 8: return Color.parseColor("#16A085"); // Medio Ambiente - Verde agua
            case 9: return Color.parseColor("#8E44AD"); // Turismo - Púrpura oscuro
            case 10: return Color.parseColor("#3498DB"); // Tecnología - Azul claro
            default: return Color.parseColor("#1976D2"); // Azul por defecto
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
}
