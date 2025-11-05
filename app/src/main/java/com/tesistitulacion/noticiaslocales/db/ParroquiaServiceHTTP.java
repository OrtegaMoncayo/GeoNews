package com.tesistitulacion.noticiaslocales.db;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tesistitulacion.noticiaslocales.modelo.Parroquia;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Servicio HTTP para gestión de Parroquias
 * Consume la API REST de FastAPI para obtener información de las 12 parroquias de Ibarra
 *
 * Endpoints disponibles:
 * - GET /parroquias - Lista todas las parroquias
 * - GET /parroquias/{id} - Obtiene una parroquia específica
 * - GET /parroquias/urbanas - Solo parroquias urbanas (5)
 * - GET /parroquias/rurales - Solo parroquias rurales (7)
 * - GET /parroquias/{id}/noticias - Noticias de una parroquia
 * - GET /parroquias/{id}/eventos - Eventos de una parroquia
 */
public class ParroquiaServiceHTTP {
    private static final String TAG = "ParroquiaServiceHTTP";

    // Cliente HTTP con timeouts configurados
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .build();

    // Gson para serialización/deserialización JSON
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    /**
     * Obtiene todas las 12 parroquias de Ibarra
     *
     * @return Lista de parroquias o lista vacía si hay error
     */
    public static List<Parroquia> obtenerTodas() {
        List<Parroquia> parroquias = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                    .url(ApiConfig.PARROQUIAS_URL)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "Respuesta JSON: " + jsonResponse);

                    Type listType = new TypeToken<List<Parroquia>>(){}.getType();
                    parroquias = gson.fromJson(jsonResponse, listType);

                    Log.i(TAG, "✅ Obtenidas " + parroquias.size() + " parroquias");
                } else {
                    Log.e(TAG, "❌ Error en respuesta: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener parroquias", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear parroquias", e);
        }

        return parroquias;
    }

    /**
     * Obtiene una parroquia específica por su ID
     *
     * @param id ID de la parroquia (1-12)
     * @return Parroquia o null si no existe o hay error
     */
    public static Parroquia obtenerPorId(int id) {
        try {
            String url = ApiConfig.PARROQUIAS_URL + id + "/";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Parroquia parroquia = gson.fromJson(jsonResponse, Parroquia.class);

                    Log.i(TAG, "✅ Parroquia obtenida: " + parroquia.getNombre());
                    return parroquia;
                } else {
                    Log.e(TAG, "❌ Parroquia no encontrada: " + id);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener parroquia " + id, e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear parroquia " + id, e);
        }

        return null;
    }

    /**
     * Obtiene solo las parroquias urbanas (5)
     *
     * @return Lista de parroquias urbanas
     */
    public static List<Parroquia> obtenerUrbanas() {
        List<Parroquia> parroquias = new ArrayList<>();

        try {
            String url = ApiConfig.PARROQUIAS_URL + "urbanas/";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    Type listType = new TypeToken<List<Parroquia>>(){}.getType();
                    parroquias = gson.fromJson(jsonResponse, listType);

                    Log.i(TAG, "✅ " + parroquias.size() + " parroquias urbanas");
                } else {
                    Log.e(TAG, "❌ Error al obtener parroquias urbanas: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener parroquias urbanas", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear parroquias urbanas", e);
        }

        return parroquias;
    }

    /**
     * Obtiene solo las parroquias rurales (7)
     *
     * @return Lista de parroquias rurales
     */
    public static List<Parroquia> obtenerRurales() {
        List<Parroquia> parroquias = new ArrayList<>();

        try {
            String url = ApiConfig.PARROQUIAS_URL + "rurales/";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    Type listType = new TypeToken<List<Parroquia>>(){}.getType();
                    parroquias = gson.fromJson(jsonResponse, listType);

                    Log.i(TAG, "✅ " + parroquias.size() + " parroquias rurales");
                } else {
                    Log.e(TAG, "❌ Error al obtener parroquias rurales: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener parroquias rurales", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear parroquias rurales", e);
        }

        return parroquias;
    }

    /**
     * Obtiene array de nombres para Spinner (incluye "Todas")
     *
     * @return Array de strings con nombres de parroquias
     */
    public static String[] obtenerNombresParaSpinner() {
        // Opción 1: Usar datos estáticos (más rápido, no requiere red)
        return Parroquia.ParroquiasIbarra.getNombresParroquias();

        // Opción 2: Obtener desde API (más actualizado pero requiere red)
        // List<Parroquia> parroquias = obtenerTodas();
        // String[] nombres = new String[parroquias.size() + 1];
        // nombres[0] = "Todas las parroquias";
        // for (int i = 0; i < parroquias.size(); i++) {
        //     nombres[i + 1] = parroquias.get(i).getNombre();
        // }
        // return nombres;
    }

    /**
     * Obtiene estadísticas de una parroquia (noticias y eventos)
     *
     * @param id ID de la parroquia
     * @return Parroquia con contadores actualizados
     */
    public static Parroquia obtenerConEstadisticas(int id) {
        try {
            String url = ApiConfig.PARROQUIAS_URL + id + "/estadisticas/";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Parroquia parroquia = gson.fromJson(jsonResponse, Parroquia.class);

                    Log.i(TAG, "✅ Estadísticas de " + parroquia.getNombre() +
                            ": " + parroquia.getNoticiasCount() + " noticias, " +
                            parroquia.getEventosCount() + " eventos");
                    return parroquia;
                } else {
                    Log.e(TAG, "❌ Error al obtener estadísticas: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener estadísticas", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear estadísticas", e);
        }

        return null;
    }

    /**
     * FALLBACK: Obtiene parroquias desde datos estáticos
     * Útil cuando no hay conexión a internet o el backend no está disponible
     *
     * @return Lista de las 12 parroquias con datos básicos
     */
    public static List<Parroquia> obtenerDesdeDatosEstaticos() {
        List<Parroquia> parroquias = new ArrayList<>();

        Parroquia[] todasLasParroquias = Parroquia.ParroquiasIbarra.getTodasLasParroquias();
        for (Parroquia parroquia : todasLasParroquias) {
            parroquias.add(parroquia);
        }

        Log.i(TAG, "✅ Usando " + parroquias.size() + " parroquias desde datos estáticos");
        return parroquias;
    }

    /**
     * Obtiene parroquias con manejo de fallback automático
     * Intenta obtener desde API, si falla usa datos estáticos
     *
     * @return Lista de parroquias (desde API o estáticos)
     */
    public static List<Parroquia> obtenerConFallback() {
        List<Parroquia> parroquias = obtenerTodas();

        // Si la lista está vacía (error de red), usar datos estáticos
        if (parroquias.isEmpty()) {
            Log.w(TAG, "⚠️ API no disponible, usando datos estáticos");
            parroquias = obtenerDesdeDatosEstaticos();
        }

        return parroquias;
    }
}
