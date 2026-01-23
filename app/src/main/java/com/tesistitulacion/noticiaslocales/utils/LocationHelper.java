package com.tesistitulacion.noticiaslocales.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

/**
 * Helper centralizado para gestión de ubicación GPS
 * Elimina código duplicado en Activities que requieren ubicación
 * Unifica códigos de request y proporciona API consistente
 */
public class LocationHelper {

    private static final String TAG = "LocationHelper";

    // Código de request unificado para permisos de ubicación
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    // Intervalo de actualización de ubicación (5 segundos)
    private static final long UPDATE_INTERVAL = 5000;

    // Intervalo más rápido aceptable (2 segundos)
    private static final long FASTEST_INTERVAL = 2000;

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    /**
     * Interfaz para recibir actualizaciones de ubicación
     */
    public interface LocationUpdateListener {
        void onLocationChanged(Location location);
        void onLocationError(String error);
    }

    /**
     * Interfaz para recibir ubicación actual (una sola vez)
     */
    public interface CurrentLocationListener {
        void onLocationReceived(Location location);
        void onLocationError(String error);
    }

    /**
     * Constructor
     * @param context Contexto de la aplicación
     */
    public LocationHelper(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * Verifica si los permisos de ubicación están concedidos
     * @return true si los permisos están concedidos
     */
    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Solicita permisos de ubicación
     * El resultado se debe manejar en onRequestPermissionsResult de la Activity
     * @param activity Activity que solicita los permisos
     */
    public void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Verifica permisos y solicita si no están concedidos
     * @param activity Activity que solicita los permisos
     * @return true si ya tiene permisos, false si los está solicitando
     */
    public boolean checkAndRequestLocationPermission(Activity activity) {
        if (hasLocationPermission()) {
            return true;
        } else {
            requestLocationPermission(activity);
            return false;
        }
    }

    /**
     * Obtiene la ubicación actual (una sola vez)
     * @param listener Callback para recibir la ubicación
     */
    public void getCurrentLocation(final CurrentLocationListener listener) {
        if (!hasLocationPermission()) {
            if (listener != null) {
                listener.onLocationError("Permisos de ubicación no concedidos");
            }
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null && listener != null) {
                            Log.d(TAG, "Ubicación obtenida: " + location.getLatitude() + ", " + location.getLongitude());
                            listener.onLocationReceived(location);
                        } else if (listener != null) {
                            listener.onLocationError("No se pudo obtener la ubicación");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al obtener ubicación: " + e.getMessage(), e);
                        if (listener != null) {
                            listener.onLocationError(e.getMessage());
                        }
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Excepción de seguridad al obtener ubicación: " + e.getMessage(), e);
            if (listener != null) {
                listener.onLocationError("Error de permisos: " + e.getMessage());
            }
        }
    }

    /**
     * Inicia actualizaciones continuas de ubicación
     * @param listener Callback para recibir actualizaciones
     */
    public void startLocationUpdates(final LocationUpdateListener listener) {
        if (!hasLocationPermission()) {
            if (listener != null) {
                listener.onLocationError("Permisos de ubicación no concedidos");
            }
            return;
        }

        // Crear LocationRequest
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                .build();

        // Crear callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null && listener != null) {
                        Log.d(TAG, "Actualización de ubicación: " +
                              location.getLatitude() + ", " + location.getLongitude());
                        listener.onLocationChanged(location);
                    }
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, null);
            Log.d(TAG, "Actualizaciones de ubicación iniciadas");
        } catch (SecurityException e) {
            Log.e(TAG, "Error al iniciar actualizaciones: " + e.getMessage(), e);
            if (listener != null) {
                listener.onLocationError("Error al iniciar actualizaciones: " + e.getMessage());
            }
        }
    }

    /**
     * Detiene las actualizaciones de ubicación
     */
    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            Log.d(TAG, "Actualizaciones de ubicación detenidas");
            locationCallback = null;
        }
    }

    /**
     * Calcula la distancia en kilómetros entre dos puntos usando la fórmula de Haversine
     * @param lat1 Latitud del punto 1
     * @param lon1 Longitud del punto 1
     * @param lat2 Latitud del punto 2
     * @param lon2 Longitud del punto 2
     * @return Distancia en kilómetros
     */
    public static double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        final int RADIO_TIERRA_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }

    /**
     * Calcula la distancia en kilómetros entre dos objetos Location
     * @param location1 Primera ubicación
     * @param location2 Segunda ubicación
     * @return Distancia en kilómetros
     */
    public static double calcularDistanciaKm(Location location1, Location location2) {
        if (location1 == null || location2 == null) {
            return Double.MAX_VALUE;
        }
        return calcularDistanciaKm(
                location1.getLatitude(),
                location1.getLongitude(),
                location2.getLatitude(),
                location2.getLongitude()
        );
    }

    /**
     * Formatea la distancia para mostrar al usuario
     * @param distanciaKm Distancia en kilómetros
     * @return String formateado (ej: "1.5 km" o "500 m")
     */
    public static String formatearDistancia(double distanciaKm) {
        if (distanciaKm < 1.0) {
            int metros = (int) (distanciaKm * 1000);
            return metros + " m";
        } else {
            return String.format(java.util.Locale.US, "%.1f km", distanciaKm);
        }
    }

    /**
     * Obtiene el cliente FusedLocationProvider
     * Útil para casos avanzados donde se necesita acceso directo
     * @return FusedLocationProviderClient
     */
    public FusedLocationProviderClient getFusedLocationClient() {
        return fusedLocationClient;
    }
}
