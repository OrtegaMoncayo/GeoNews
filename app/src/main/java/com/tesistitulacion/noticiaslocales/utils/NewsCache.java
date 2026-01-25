package com.tesistitulacion.noticiaslocales.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para gestionar el caché local de noticias
 * Permite leer noticias sin conexión a internet
 */
public class NewsCache {

    private static final String TAG = "NewsCache";
    private static final String PREFS_NAME = "news_cache";
    private static final String KEY_NOTICIAS = "noticias_cached";
    private static final String KEY_TIMESTAMP = "cache_timestamp";
    private static final String KEY_VERSION = "cache_version";

    // Versión del caché (incrementar si cambia el modelo de Noticia)
    private static final int CACHE_VERSION = 1;

    // Tiempo máximo de validez del caché (24 horas en milisegundos)
    private static final long CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000;

    // Tiempo de caché "fresco" (5 minutos) - se usa si hay conexión
    private static final long CACHE_FRESH_MS = 5 * 60 * 1000;

    private static NewsCache instance;
    private SharedPreferences prefs;
    private Gson gson;

    /**
     * Constructor privado (Singleton)
     */
    private NewsCache(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();

        // Verificar versión del caché
        int savedVersion = prefs.getInt(KEY_VERSION, 0);
        if (savedVersion != CACHE_VERSION) {
            Log.w(TAG, "Versión de caché diferente, limpiando caché antiguo");
            limpiarCache();
            prefs.edit().putInt(KEY_VERSION, CACHE_VERSION).apply();
        }
    }

    /**
     * Obtiene la instancia singleton
     */
    public static synchronized NewsCache getInstance(Context context) {
        if (instance == null) {
            instance = new NewsCache(context);
        }
        return instance;
    }

    /**
     * Guarda una lista de noticias en caché
     * @param noticias Lista de noticias a guardar
     */
    public void guardarNoticias(List<Noticia> noticias) {
        if (noticias == null || noticias.isEmpty()) {
            Log.w(TAG, "Lista de noticias vacía, no se guarda en caché");
            return;
        }

        try {
            String json = gson.toJson(noticias);
            long timestamp = System.currentTimeMillis();

            prefs.edit()
                    .putString(KEY_NOTICIAS, json)
                    .putLong(KEY_TIMESTAMP, timestamp)
                    .apply();

            Log.i(TAG, "Caché guardado: " + noticias.size() + " noticias");
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar caché", e);
        }
    }

    /**
     * Obtiene las noticias del caché
     * @return Lista de noticias o lista vacía si no hay caché
     */
    public List<Noticia> obtenerNoticias() {
        try {
            String json = prefs.getString(KEY_NOTICIAS, null);

            if (json == null || json.isEmpty()) {
                Log.d(TAG, "No hay noticias en caché");
                return new ArrayList<>();
            }

            Type listType = new TypeToken<List<Noticia>>(){}.getType();
            List<Noticia> noticias = gson.fromJson(json, listType);

            if (noticias != null) {
                Log.i(TAG, "Caché recuperado: " + noticias.size() + " noticias");
                return noticias;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al leer caché", e);
        }

        return new ArrayList<>();
    }

    /**
     * Verifica si hay caché disponible
     */
    public boolean hayCache() {
        String json = prefs.getString(KEY_NOTICIAS, null);
        return json != null && !json.isEmpty();
    }

    /**
     * Verifica si el caché está expirado (más de 24 horas)
     */
    public boolean cacheExpirado() {
        long timestamp = prefs.getLong(KEY_TIMESTAMP, 0);
        long ahora = System.currentTimeMillis();
        return (ahora - timestamp) > CACHE_EXPIRY_MS;
    }

    /**
     * Verifica si el caché está "fresco" (menos de 5 minutos)
     * Útil para decidir si refrescar cuando hay conexión
     */
    public boolean cacheFresco() {
        long timestamp = prefs.getLong(KEY_TIMESTAMP, 0);
        long ahora = System.currentTimeMillis();
        return (ahora - timestamp) < CACHE_FRESH_MS;
    }

    /**
     * Obtiene el timestamp del último guardado
     * @return Timestamp en milisegundos o 0 si no hay caché
     */
    public long getTimestamp() {
        return prefs.getLong(KEY_TIMESTAMP, 0);
    }

    /**
     * Obtiene la antigüedad del caché en formato legible
     */
    public String getAntiguedadLegible() {
        long timestamp = getTimestamp();
        if (timestamp == 0) {
            return "Sin caché";
        }

        long diff = System.currentTimeMillis() - timestamp;
        long segundos = diff / 1000;
        long minutos = segundos / 60;
        long horas = minutos / 60;

        if (horas > 0) {
            return "Hace " + horas + " hora" + (horas > 1 ? "s" : "");
        } else if (minutos > 0) {
            return "Hace " + minutos + " minuto" + (minutos > 1 ? "s" : "");
        } else {
            return "Hace unos segundos";
        }
    }

    /**
     * Limpia todo el caché
     */
    public void limpiarCache() {
        prefs.edit()
                .remove(KEY_NOTICIAS)
                .remove(KEY_TIMESTAMP)
                .apply();
        Log.i(TAG, "Caché limpiado");
    }

    /**
     * Obtiene el tamaño aproximado del caché en bytes
     */
    public long getTamanioCache() {
        String json = prefs.getString(KEY_NOTICIAS, "");
        return json != null ? json.getBytes().length : 0;
    }

    /**
     * Obtiene el tamaño del caché en formato legible
     */
    public String getTamanioCacheLegible() {
        long bytes = getTamanioCache();
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }

    /**
     * Obtiene estadísticas del caché
     */
    public CacheStats getEstadisticas() {
        return new CacheStats(
                hayCache(),
                obtenerNoticias().size(),
                getTimestamp(),
                getTamanioCache(),
                cacheExpirado(),
                cacheFresco()
        );
    }

    /**
     * Clase para estadísticas del caché
     */
    public static class CacheStats {
        public final boolean disponible;
        public final int cantidadNoticias;
        public final long timestamp;
        public final long tamanioBytes;
        public final boolean expirado;
        public final boolean fresco;

        public CacheStats(boolean disponible, int cantidadNoticias, long timestamp,
                         long tamanioBytes, boolean expirado, boolean fresco) {
            this.disponible = disponible;
            this.cantidadNoticias = cantidadNoticias;
            this.timestamp = timestamp;
            this.tamanioBytes = tamanioBytes;
            this.expirado = expirado;
            this.fresco = fresco;
        }
    }
}
