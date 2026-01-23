package com.tesistitulacion.noticiaslocales.workers;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.tesistitulacion.noticiaslocales.db.ApiConfig;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Worker para enviar resúmenes de noticias por email de forma periódica
 * Se ejecuta en segundo plano usando WorkManager
 */
public class EmailDigestWorker extends Worker {

    private static final String TAG = "EmailDigestWorker";

    public EmailDigestWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.i(TAG, "Iniciando envío de resumen por email");

            // Verificar si el usuario tiene email digest activado
            if (!UsuarioPreferences.getEmailDigestActivado(getApplicationContext())) {
                Log.i(TAG, "Email digest desactivado, cancelando trabajo");
                return Result.success();
            }

            // Obtener datos del usuario
            String email = UsuarioPreferences.getEmail(getApplicationContext());
            String nombre = UsuarioPreferences.getNombre(getApplicationContext());
            String token = UsuarioPreferences.getToken(getApplicationContext());
            String frecuencia = UsuarioPreferences.getEmailDigestFrecuencia(getApplicationContext());

            if (email == null || email.isEmpty()) {
                Log.e(TAG, "No se encontró email del usuario");
                return Result.failure();
            }

            // Enviar solicitud al backend
            boolean enviado = enviarEmailDigest(email, nombre, frecuencia, token);

            if (enviado) {
                // Guardar timestamp del último envío
                UsuarioPreferences.guardarEmailDigestLastSent(
                    getApplicationContext(),
                    System.currentTimeMillis()
                );
                Log.i(TAG, "Resumen por email enviado exitosamente a: " + email);
                return Result.success();
            } else {
                Log.e(TAG, "Error al enviar resumen por email");
                return Result.retry(); // Reintentar más tarde
            }

        } catch (Exception e) {
            Log.e(TAG, "Excepción al enviar email digest", e);
            return Result.retry();
        }
    }

    /**
     * Envía solicitud al backend para generar y enviar email digest
     */
    private boolean enviarEmailDigest(String email, String nombre, String frecuencia, String token) {
        HttpURLConnection connection = null;
        try {
            // Endpoint del backend
            URL url = new URL(ApiConfig.BASE_URL + "api/email-digest");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            connection.setDoOutput(true);
            connection.setConnectTimeout(30000); // 30 segundos
            connection.setReadTimeout(30000);

            // Crear JSON con datos
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("nombre", nombre);
            jsonBody.put("frecuencia", frecuencia);

            // Enviar datos
            OutputStream os = connection.getOutputStream();
            byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.close();

            // Verificar respuesta
            int responseCode = connection.getResponseCode();
            Log.i(TAG, "Response code del servidor: " + responseCode);

            return responseCode == 200 || responseCode == 201;

        } catch (Exception e) {
            Log.e(TAG, "Error en enviarEmailDigest", e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
