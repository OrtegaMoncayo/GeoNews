package com.tesistitulacion.noticiaslocales.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Utilidad para geolocalización y cálculos geográficos
 *
 * Funcionalidades:
 * - Obtención de ubicación GPS actual (FusedLocationProviderClient)
 * - Cálculo de distancias con algoritmo de Haversine
 * - Solicitud de permisos de ubicación
 *
 * ISO/IEC 25010 - Adecuación Funcional: Geolocalización completa
 */
public class UbicacionUtils {
    private static final String TAG = "UbicacionUtils";

    // Código de request para permisos
    public static final int PERMISSION_REQUEST_CODE = 1001;

    // Radio de la Tierra en kilómetros
    private static final double RADIO_TIERRA_KM = 6371.0;

    // Coordenadas del centro de Ibarra (para fallback)
    public static final double IBARRA_LAT = 0.3476;
    public static final double IBARRA_LON = -78.1223;

    /**
     * Calcula la distancia entre dos puntos usando el algoritmo de Haversine
     *
     * @param lat1 Latitud punto 1 (grados)
     * @param lon1 Longitud punto 1 (grados)
     * @param lat2 Latitud punto 2 (grados)
     * @param lon2 Longitud punto 2 (grados)
     * @return Distancia en kilómetros
     */
    public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // Convertir grados a radianes
        double latDistancia = Math.toRadians(lat2 - lat1);
        double lonDistancia = Math.toRadians(lon2 - lon1);

        // Fórmula de Haversine
        double a = Math.sin(latDistancia / 2) * Math.sin(latDistancia / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistancia / 2) * Math.sin(lonDistancia / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distancia en kilómetros
        double distancia = RADIO_TIERRA_KM * c;

        Log.d(TAG, String.format("Distancia calculada: %.2f km (de [%.4f, %.4f] a [%.4f, %.4f])",
                distancia, lat1, lon1, lat2, lon2));

        return distancia;
    }

    /**
     * Verifica si los permisos de ubicación están concedidos
     */
    public static boolean tienePermisosUbicacion(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Solicita permisos de ubicación al usuario
     */
    public static void solicitarPermisosUbicacion(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                PERMISSION_REQUEST_CODE);
    }

    /**
     * Obtiene la ubicación actual del dispositivo usando FusedLocationProviderClient
     *
     * @param context Context de la aplicación
     * @param listener Callback con la ubicación obtenida
     */
    public static void obtenerUbicacionActual(Context context, OnUbicacionObtenidaListener listener) {
        // Verificar permisos
        if (!tienePermisosUbicacion(context)) {
            Log.w(TAG, "No hay permisos de ubicación");
            listener.onError("Permisos de ubicación no concedidos");
            return;
        }

        // Crear cliente de ubicación
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

        try {
            // Obtener última ubicación conocida
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.i(TAG, String.format("✅ Ubicación obtenida: [%.4f, %.4f]",
                                        location.getLatitude(), location.getLongitude()));

                                listener.onUbicacionObtenida(
                                        location.getLatitude(),
                                        location.getLongitude()
                                );
                            } else {
                                Log.w(TAG, "⚠️ Ubicación null, usando centro de Ibarra como fallback");
                                // Si no hay ubicación, usar centro de Ibarra
                                listener.onUbicacionObtenida(IBARRA_LAT, IBARRA_LON);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "❌ Error al obtener ubicación", e);
                        listener.onError("Error al obtener ubicación: " + e.getMessage());
                    });

        } catch (SecurityException e) {
            Log.e(TAG, "❌ Error de seguridad al obtener ubicación", e);
            listener.onError("Error de permisos: " + e.getMessage());
        }
    }

    /**
     * Formatea una distancia para mostrar en UI
     *
     * @param distanciaKm Distancia en kilómetros
     * @return String formateado (ej: "3.2 km", "850 m")
     */
    public static String formatearDistancia(double distanciaKm) {
        if (distanciaKm < 1.0) {
            // Menos de 1 km → mostrar en metros
            int metros = (int) (distanciaKm * 1000);
            return metros + " m";
        } else if (distanciaKm < 10.0) {
            // 1-10 km → 1 decimal
            return String.format(java.util.Locale.US, "%.1f km", distanciaKm);
        } else {
            // Más de 10 km → sin decimales
            return String.format(java.util.Locale.US, "%.0f km", distanciaKm);
        }
    }

    /**
     * Verifica si una coordenada está dentro de Ibarra (aproximado)
     *
     * @param lat Latitud
     * @param lon Longitud
     * @return true si está en Ibarra, false en caso contrario
     */
    public static boolean estaEnIbarra(double lat, double lon) {
        // Límites aproximados de Ibarra
        final double LAT_MIN = 0.25;
        final double LAT_MAX = 0.45;
        final double LON_MIN = -78.20;
        final double LON_MAX = -78.05;

        return lat >= LAT_MIN && lat <= LAT_MAX && lon >= LON_MIN && lon <= LON_MAX;
    }

    /**
     * Interface para callback de ubicación
     */
    public interface OnUbicacionObtenidaListener {
        void onUbicacionObtenida(double latitud, double longitud);
        void onError(String mensaje);
    }
}
