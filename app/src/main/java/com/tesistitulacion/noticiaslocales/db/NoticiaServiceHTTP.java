package com.tesistitulacion.noticiaslocales.db;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tesistitulacion.noticiaslocales.modelo.Noticia;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Servicio HTTP para operaciones con Noticias
 * Usa OkHttp para peticiones HTTP y Gson para parsing JSON
 */
public class NoticiaServiceHTTP {
    private static final String TAG = "NoticiaServiceHTTP";

    // Usa la configuración centralizada de ApiConfig
    private static final String BASE_URL = ApiConfig.BASE_URL;
    private static final String NOTICIAS_ENDPOINT = "noticias";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static final Gson gson = new Gson();

    /**
     * Obtiene todas las noticias desde la API
     * @return Lista de noticias o lista vacía si hay error
     */
    public static List<Noticia> obtenerTodas() {
        List<Noticia> noticias = new ArrayList<>();

        try {
            String url = BASE_URL + NOTICIAS_ENDPOINT + "?limit=100";
            Log.d(TAG, "Obteniendo noticias de: " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                Log.d(TAG, "Respuesta recibida: " + jsonResponse.substring(0, Math.min(100, jsonResponse.length())));

                // La API retorna: {"success": true, "count": X, "noticias": [...]}
                NoticiasResponse wrapper = gson.fromJson(jsonResponse, NoticiasResponse.class);
                if (wrapper != null && wrapper.noticias != null) {
                    noticias = wrapper.noticias;
                    Log.i(TAG, "Noticias obtenidas: " + noticias.size());
                }
            } else {
                Log.e(TAG, "Error en respuesta: " + response.code() + " - " + response.message());
            }

            response.close();

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener noticias: " + e.getMessage(), e);
        }

        return noticias != null ? noticias : new ArrayList<>();
    }

    /**
     * Obtiene una noticia específica por ID
     * @param id ID de la noticia
     * @return Noticia o null si no se encuentra
     */
    public static Noticia obtenerPorId(int id) {
        try {
            String url = BASE_URL + NOTICIAS_ENDPOINT + id + "/";
            Log.d(TAG, "Obteniendo noticia ID " + id + " de: " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                Noticia noticia = gson.fromJson(jsonResponse, Noticia.class);
                Log.i(TAG, "Noticia obtenida: " + noticia.getTitulo());
                response.close();
                return noticia;
            } else {
                Log.e(TAG, "Error al obtener noticia: " + response.code());
            }

            response.close();

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener noticia por ID: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Obtiene noticias cercanas a una ubicación
     * @param latitud Latitud de referencia
     * @param longitud Longitud de referencia
     * @param radioKm Radio de búsqueda en kilómetros
     * @return Lista de noticias cercanas
     */
    public static List<Noticia> obtenerCercanas(double latitud, double longitud, double radioKm) {
        List<Noticia> noticias = new ArrayList<>();

        try {
            String url = BASE_URL + NOTICIAS_ENDPOINT + "/cercanas" +
                    "?latitud=" + latitud +
                    "&longitud=" + longitud +
                    "&radio=" + radioKm;

            Log.d(TAG, "Obteniendo noticias cercanas de: " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                Log.d(TAG, "Respuesta cercanas: " + jsonResponse.substring(0, Math.min(200, jsonResponse.length())));

                // La API retorna: {"success": true, "count": X, "noticias": [...]}
                // Parsear usando una clase wrapper
                NoticiasCercanasResponse wrapper = gson.fromJson(jsonResponse, NoticiasCercanasResponse.class);
                if (wrapper != null && wrapper.noticias != null) {
                    noticias = wrapper.noticias;
                    Log.i(TAG, "Noticias cercanas obtenidas: " + noticias.size());
                }
            } else {
                Log.e(TAG, "Error en respuesta cercanas: " + response.code());
            }

            response.close();

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener noticias cercanas: " + e.getMessage(), e);
        }

        return noticias != null ? noticias : new ArrayList<>();
    }

    /**
     * Obtiene noticias por categoría
     * @param categoriaId ID de la categoría (1-10)
     * @return Lista de noticias de la categoría
     */
    public static List<Noticia> obtenerPorCategoria(int categoriaId) {
        List<Noticia> todasLasNoticias = obtenerTodas();
        List<Noticia> noticiasFiltradas = new ArrayList<>();

        for (Noticia noticia : todasLasNoticias) {
            if (noticia.getCategoriaId() != null && noticia.getCategoriaId() == categoriaId) {
                noticiasFiltradas.add(noticia);
            }
        }

        Log.i(TAG, "Noticias de categoría " + categoriaId + ": " + noticiasFiltradas.size());
        return noticiasFiltradas;
    }

    /**
     * Obtiene noticias destacadas
     * @return Lista de noticias destacadas
     */
    public static List<Noticia> obtenerDestacadas() {
        List<Noticia> todasLasNoticias = obtenerTodas();
        List<Noticia> destacadas = new ArrayList<>();

        for (Noticia noticia : todasLasNoticias) {
            if (noticia.getDestacada() != null && noticia.getDestacada()) {
                destacadas.add(noticia);
            }
        }

        Log.i(TAG, "Noticias destacadas: " + destacadas.size());
        return destacadas;
    }

    /**
     * Clase auxiliar para parsear la respuesta de noticias
     */
    private static class NoticiasResponse {
        public boolean success;
        public int count;
        public List<Noticia> noticias;
    }

    /**
     * Clase auxiliar para parsear la respuesta de noticias cercanas
     */
    private static class NoticiasCercanasResponse {
        public boolean success;
        public int count;
        public List<Noticia> noticias;
    }
}
