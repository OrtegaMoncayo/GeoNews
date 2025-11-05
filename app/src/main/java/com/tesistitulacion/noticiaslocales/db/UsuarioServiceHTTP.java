package com.tesistitulacion.noticiaslocales.db;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tesistitulacion.noticiaslocales.modelo.Usuario;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Servicio HTTP para gestión de Usuarios y Autenticación
 * Consume la API REST de FastAPI para operaciones de usuarios
 *
 * Endpoints disponibles:
 * - POST /auth/login - Autenticación de usuario
 * - POST /auth/register - Registro de nuevo usuario
 * - POST /auth/logout - Cierre de sesión
 * - GET /usuarios/perfil - Obtiene perfil del usuario autenticado
 * - PUT /usuarios/perfil - Actualiza perfil del usuario
 * - GET /usuarios/{id} - Obtiene usuario por ID
 */
public class UsuarioServiceHTTP {
    private static final String TAG = "UsuarioServiceHTTP";
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
     * Resultado de operaciones de autenticación
     */
    public static class AuthResult {
        public boolean exitoso;
        public String mensaje;
        public String token;
        public Usuario usuario;

        public AuthResult(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public AuthResult(boolean exitoso, String mensaje, String token, Usuario usuario) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.token = token;
            this.usuario = usuario;
        }
    }

    /**
     * Realiza login de usuario
     *
     * @param email Email del usuario
     * @param password Contraseña sin hashear (se hashea en backend)
     * @return AuthResult con token y datos del usuario si es exitoso
     */
    public static AuthResult login(String email, String password) {
        try {
            // Crear JSON con credenciales
            JsonObject credentials = new JsonObject();
            credentials.addProperty("email", email);
            credentials.addProperty("password", password);

            String jsonCredentials = gson.toJson(credentials);
            Log.d(TAG, "Intentando login con: " + email);

            RequestBody body = RequestBody.create(jsonCredentials, JSON);

            Request request = new Request.Builder()
                    .url(ApiConfig.AUTH_LOGIN_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "Respuesta login: " + jsonResponse);

                    JsonObject jsonObj = gson.fromJson(jsonResponse, JsonObject.class);

                    String token = jsonObj.get("token").getAsString();
                    JsonObject usuarioJson = jsonObj.getAsJsonObject("usuario");
                    Usuario usuario = gson.fromJson(usuarioJson, Usuario.class);

                    Log.i(TAG, "✅ Login exitoso: " + usuario.getNombreCompleto());

                    return new AuthResult(
                            true,
                            "Login exitoso",
                            token,
                            usuario
                    );

                } else if (response.code() == 401) {
                    Log.e(TAG, "❌ Credenciales inválidas");
                    return new AuthResult(false, "Email o contraseña incorrectos");

                } else {
                    Log.e(TAG, "❌ Error en login: " + response.code());
                    return new AuthResult(false, "Error en el servidor");
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red en login", e);
            return new AuthResult(false, "Error de conexión. Verifica tu internet.");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al procesar login", e);
            return new AuthResult(false, "Error inesperado");
        }
    }

    /**
     * Registra un nuevo usuario
     *
     * @param usuario Objeto Usuario con los datos del nuevo usuario
     * @return AuthResult con token y usuario creado si es exitoso
     */
    public static AuthResult registrar(Usuario usuario) {
        try {
            String jsonUsuario = gson.toJson(usuario);
            Log.d(TAG, "Registrando usuario: " + usuario.getEmail());

            RequestBody body = RequestBody.create(jsonUsuario, JSON);

            Request request = new Request.Builder()
                    .url(ApiConfig.AUTH_REGISTER_URL)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "Respuesta registro: " + jsonResponse);

                    JsonObject jsonObj = gson.fromJson(jsonResponse, JsonObject.class);

                    String token = jsonObj.get("token").getAsString();
                    JsonObject usuarioJson = jsonObj.getAsJsonObject("usuario");
                    Usuario usuarioCreado = gson.fromJson(usuarioJson, Usuario.class);

                    Log.i(TAG, "✅ Registro exitoso: " + usuarioCreado.getNombreCompleto());

                    return new AuthResult(
                            true,
                            "Cuenta creada exitosamente",
                            token,
                            usuarioCreado
                    );

                } else if (response.code() == 400) {
                    Log.e(TAG, "❌ Datos inválidos para registro");
                    return new AuthResult(false, "Datos inválidos. Verifica los campos.");

                } else if (response.code() == 409) {
                    Log.e(TAG, "❌ Email ya registrado");
                    return new AuthResult(false, "El email ya está registrado");

                } else {
                    Log.e(TAG, "❌ Error en registro: " + response.code());
                    if (response.body() != null) {
                        Log.e(TAG, "Respuesta: " + response.body().string());
                    }
                    return new AuthResult(false, "Error en el servidor");
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red en registro", e);
            return new AuthResult(false, "Error de conexión. Verifica tu internet.");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al procesar registro", e);
            return new AuthResult(false, "Error inesperado");
        }
    }

    /**
     * Cierra sesión del usuario
     *
     * @param token Token de autenticación del usuario
     * @return true si se cerró sesión correctamente
     */
    public static boolean logout(String token) {
        try {
            Request request = new Request.Builder()
                    .url(ApiConfig.AUTH_LOGOUT_URL)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(RequestBody.create("", JSON))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "✅ Sesión cerrada correctamente");
                    return true;
                } else {
                    Log.e(TAG, "❌ Error al cerrar sesión: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red en logout", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al procesar logout", e);
        }

        return false;
    }

    /**
     * Obtiene el perfil del usuario autenticado
     *
     * @param token Token de autenticación
     * @return Usuario con datos completos o null si hay error
     */
    public static Usuario obtenerPerfil(String token) {
        try {
            Request request = new Request.Builder()
                    .url(ApiConfig.USUARIOS_PERFIL_URL)
                    .addHeader("Authorization", "Bearer " + token)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Usuario usuario = gson.fromJson(jsonResponse, Usuario.class);

                    Log.i(TAG, "✅ Perfil obtenido: " + usuario.getNombreCompleto());
                    return usuario;

                } else if (response.code() == 401) {
                    Log.e(TAG, "❌ Token inválido o expirado");
                } else {
                    Log.e(TAG, "❌ Error al obtener perfil: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener perfil", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear perfil", e);
        }

        return null;
    }

    /**
     * Actualiza el perfil del usuario autenticado
     *
     * @param token Token de autenticación
     * @param usuario Objeto Usuario con datos actualizados
     * @return true si se actualizó correctamente
     */
    public static boolean actualizarPerfil(String token, Usuario usuario) {
        try {
            String jsonUsuario = gson.toJson(usuario);

            RequestBody body = RequestBody.create(jsonUsuario, JSON);

            Request request = new Request.Builder()
                    .url(ApiConfig.USUARIOS_PERFIL_URL)
                    .addHeader("Authorization", "Bearer " + token)
                    .put(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "✅ Perfil actualizado");
                    return true;
                } else {
                    Log.e(TAG, "❌ Error al actualizar perfil: " + response.code());
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al actualizar perfil", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al procesar actualización", e);
        }

        return false;
    }

    /**
     * Obtiene un usuario por su ID
     *
     * @param id ID del usuario
     * @return Usuario o null si no existe o hay error
     */
    public static Usuario obtenerPorId(int id) {
        try {
            String url = ApiConfig.getUsuarioUrl(id);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Usuario usuario = gson.fromJson(jsonResponse, Usuario.class);

                    Log.i(TAG, "✅ Usuario obtenido: " + usuario.getNombreCompleto());
                    return usuario;
                } else {
                    Log.e(TAG, "❌ Usuario no encontrado: " + id);
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "❌ Error de red al obtener usuario " + id, e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al parsear usuario " + id, e);
        }

        return null;
    }
}
