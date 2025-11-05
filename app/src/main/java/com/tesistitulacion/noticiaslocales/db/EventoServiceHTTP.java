package com.tesistitulacion.noticiaslocales.db;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tesistitulacion.noticiaslocales.modelo.Evento;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Servicio HTTP para gestión de Eventos
 * Consume la API REST de FastAPI para operaciones CRUD de eventos
 *
 * Endpoints disponibles:
 * - GET /eventos - Lista todos los eventos
 * - GET /eventos/{id} - Obtiene un evento específico
 * - POST /eventos - Crea un nuevo evento (envía email automático)
 * - PUT /eventos/{id} - Actualiza un evento
 * - DELETE /eventos/{id} - Elimina un evento
 * - GET /eventos/cercanos - Eventos cercanos por radio
 */
public class EventoServiceHTTP {
    private static final String TAG = "EventoServiceHTTP";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Cliente HTTP con timeouts configurados
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build();

    // Gson para serialización/deserialización JSON
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    /**
     * Obtiene todos los eventos del sistema
     *
     * @return Lista de eventos o lista vacía si hay error
     */
    public static List<Evento> obtenerTodos() {
        List<Evento> eventos = new ArrayList<>();

        try {
            Request request = new Request.Builder()
                    .url(ApiConfig.EVENTOS_URL)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "Respuesta JSON: " + jsonResponse);

                    Type listType = new TypeToken<List<Evento>>(){}.getType();
                    eventos = gson.fromJson(jsonResponse, listType);

                    Log.i(TAG, "✅ Obtenidos " + eventos.size() + " eventos");
                } else {
                    Log.e(TAG, "❌ Error en respuesta: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener eventos", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear eventos", e);
        }

        return eventos;
    }

    /**
     * Obtiene un evento específico por su ID
     *
     * @param id ID del evento
     * @return Evento o null si no existe o hay error
     */
    public static Evento obtenerPorId(int id) {
        try {
            String url = ApiConfig.EVENTOS_URL + id + "/";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Evento evento = gson.fromJson(jsonResponse, Evento.class);

                    Log.i(TAG, "✅ Evento obtenido: " + evento.getDescripcion());
                    return evento;
                } else {
                    Log.e(TAG, "❌ Evento no encontrado: " + id);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener evento " + id, e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear evento " + id, e);
        }

        return null;
    }

    /**
     * Obtiene eventos cercanos a una ubicación dentro de un radio
     *
     * @param latitud Latitud del usuario
     * @param longitud Longitud del usuario
     * @param radioKm Radio de búsqueda en kilómetros (ej: 2, 5, 10, 20)
     * @return Lista de eventos cercanos ordenados por distancia
     */
    public static List<Evento> obtenerCercanos(double latitud, double longitud, double radioKm) {
        List<Evento> eventos = new ArrayList<>();

        try {
            String url = ApiConfig.EVENTOS_URL + "cercanos/" +
                    "?latitud=" + latitud +
                    "&longitud=" + longitud +
                    "&radio=" + radioKm;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    Type listType = new TypeToken<List<Evento>>(){}.getType();
                    eventos = gson.fromJson(jsonResponse, listType);

                    Log.i(TAG, "✅ " + eventos.size() + " eventos en radio de " + radioKm + "km");
                } else {
                    Log.e(TAG, "❌ Error al obtener eventos cercanos: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al buscar eventos cercanos", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear eventos cercanos", e);
        }

        return eventos;
    }

    /**
     * Crea un nuevo evento en el sistema
     * IMPORTANTE: El backend envía automáticamente un email de notificación
     *
     * @param evento Objeto Evento con los datos a crear
     * @return Evento creado con ID asignado, o null si hay error
     */
    public static Evento crear(Evento evento) {
        try {
            String jsonEvento = gson.toJson(evento);
            Log.d(TAG, "JSON a enviar: " + jsonEvento);

            RequestBody body = RequestBody.create(jsonEvento, JSON);

            Request request = new Request.Builder()
                    .url(ApiConfig.EVENTOS_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Evento eventoCreado = gson.fromJson(jsonResponse, Evento.class);

                    Log.i(TAG, "✅ Evento creado exitosamente: ID " + eventoCreado.getId());
                    Log.i(TAG, "📧 Email de notificación enviado automáticamente");
                    return eventoCreado;
                } else {
                    Log.e(TAG, "❌ Error al crear evento: " + response.code());
                    if (response.body() != null) {
                        Log.e(TAG, "Respuesta: " + response.body().string());
                    }
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al crear evento", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al procesar creación de evento", e);
        }

        return null;
    }

    /**
     * Actualiza un evento existente
     *
     * @param id ID del evento a actualizar
     * @param evento Objeto Evento con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public static boolean actualizar(int id, Evento evento) {
        try {
            String url = ApiConfig.EVENTOS_URL + id + "/";
            String jsonEvento = gson.toJson(evento);

            RequestBody body = RequestBody.create(jsonEvento, JSON);

            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "✅ Evento actualizado: ID " + id);
                    return true;
                } else {
                    Log.e(TAG, "❌ Error al actualizar evento: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al actualizar evento", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al procesar actualización", e);
        }

        return false;
    }

    /**
     * Elimina un evento del sistema
     *
     * @param id ID del evento a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public static boolean eliminar(int id) {
        try {
            String url = ApiConfig.EVENTOS_URL + id + "/";

            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "✅ Evento eliminado: ID " + id);
                    return true;
                } else {
                    Log.e(TAG, "❌ Error al eliminar evento: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al eliminar evento", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al procesar eliminación", e);
        }

        return false;
    }

    /**
     * Obtiene eventos próximos (no finalizados ni cancelados)
     *
     * @return Lista de eventos programados o en curso
     */
    public static List<Evento> obtenerProximos() {
        List<Evento> eventos = new ArrayList<>();

        try {
            String url = ApiConfig.EVENTOS_URL + "proximos/";

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    Type listType = new TypeToken<List<Evento>>(){}.getType();
                    eventos = gson.fromJson(jsonResponse, listType);

                    Log.i(TAG, "✅ Obtenidos " + eventos.size() + " eventos próximos");
                } else {
                    Log.e(TAG, "❌ Error al obtener eventos próximos: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener eventos próximos", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear eventos próximos", e);
        }

        return eventos;
    }

    /**
     * Obtiene eventos por parroquia
     *
     * @param parroquiaId ID de la parroquia
     * @return Lista de eventos en esa parroquia
     */
    public static List<Evento> obtenerPorParroquia(int parroquiaId) {
        List<Evento> eventos = new ArrayList<>();

        try {
            String url = ApiConfig.EVENTOS_URL + "?parroquia_id=" + parroquiaId;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    Type listType = new TypeToken<List<Evento>>(){}.getType();
                    eventos = gson.fromJson(jsonResponse, listType);

                    Log.i(TAG, "✅ " + eventos.size() + " eventos en parroquia " + parroquiaId);
                } else {
                    Log.e(TAG, "❌ Error al obtener eventos por parroquia: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener eventos por parroquia", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear eventos por parroquia", e);
        }

        return eventos;
    }
}
