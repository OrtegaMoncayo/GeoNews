package com.tesistitulacion.noticiaslocales.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tesistitulacion.noticiaslocales.utils.NotificationHelper;
import com.tesistitulacion.noticiaslocales.utils.UsuarioPreferences;

import java.util.Map;

/**
 * Servicio de Firebase Cloud Messaging
 * Recibe y procesa notificaciones push
 *
 * REQUISITOS IMPLEMENTADOS:
 * - Maneja mensajes tipo "notification" (aparecen automáticamente cuando app cerrada)
 * - Maneja mensajes tipo "data" (procesados siempre por este servicio)
 * - Usa NotificationHelper para gestión centralizada
 * - SmallIcon obligatorio
 * - Canal con IMPORTANCE_HIGH
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "========== MENSAJE FCM RECIBIDO ==========");
        Log.d(TAG, "De: " + remoteMessage.getFrom());
        Log.d(TAG, "Message ID: " + remoteMessage.getMessageId());

        // Verificar si el usuario tiene notificaciones activadas
        if (!UsuarioPreferences.getNotificacionesActivas(this)) {
            Log.d(TAG, "Notificaciones desactivadas por el usuario - ignorando");
            return;
        }

        // Obtener el helper de notificaciones
        NotificationHelper notificationHelper = NotificationHelper.getInstance(this);

        // Verificar si tiene permiso de notificaciones
        if (!notificationHelper.tienePermisoNotificaciones()) {
            Log.w(TAG, "Sin permiso POST_NOTIFICATIONS - no se puede mostrar notificación");
            return;
        }

        // Procesar mensaje de datos (siempre se recibe, app abierta o cerrada)
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Datos del mensaje: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData(), notificationHelper);
        }

        // Procesar notificación (solo se recibe si la app está en primer plano)
        // Si la app está cerrada/background, el sistema Android la muestra automáticamente
        if (remoteMessage.getNotification() != null) {
            String titulo = remoteMessage.getNotification().getTitle();
            String mensaje = remoteMessage.getNotification().getBody();

            Log.d(TAG, "Notificación recibida - Título: " + titulo);
            Log.d(TAG, "Notificación recibida - Mensaje: " + mensaje);

            // Obtener noticia_id de los datos si existe
            String noticiaId = remoteMessage.getData().get("noticia_id");

            // Mostrar la notificación
            notificationHelper.mostrarNotificacionNoticia(titulo, mensaje, noticiaId);
        }

        Log.d(TAG, "==========================================");
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo token FCM generado: " + token);

        // Guardar el token en preferencias locales
        UsuarioPreferences.guardarFCMToken(this, token);

        // Enviar el token al servidor (si el usuario está logueado)
        sendTokenToServer(token);
    }

    /**
     * Procesa mensajes con datos personalizados (payload "data")
     * Este método se ejecuta siempre, independientemente del estado de la app
     */
    private void handleDataMessage(Map<String, String> data, NotificationHelper notificationHelper) {
        String tipo = data.get("type");
        String noticiaId = data.get("noticia_id");
        String titulo = data.get("titulo");
        String mensaje = data.get("mensaje");
        String distancia = data.get("distancia");

        Log.d(TAG, "Procesando mensaje de datos - Tipo: " + tipo);

        if (tipo == null) {
            // Si no hay tipo pero hay título y mensaje, mostrar notificación genérica
            if (titulo != null || mensaje != null) {
                notificationHelper.mostrarNotificacionNoticia(titulo, mensaje, noticiaId);
            }
            return;
        }

        switch (tipo) {
            case "noticia_nueva":
                // Verificar preferencia de notificaciones de noticias
                if (UsuarioPreferences.getNotificacionesNoticias(this)) {
                    notificationHelper.mostrarNotificacionNoticia(
                            titulo != null ? titulo : "Nueva noticia",
                            mensaje != null ? mensaje : "Se publicó una nueva noticia local",
                            noticiaId
                    );
                }
                break;

            case "noticia_cercana":
                // Notificación de noticia cercana a la ubicación del usuario
                if (UsuarioPreferences.getNotificacionesNoticias(this)) {
                    notificationHelper.mostrarNotificacionNoticiaCercana(
                            titulo != null ? titulo : "Noticia cerca de ti",
                            mensaje != null ? mensaje : "Hay una noticia cerca de tu ubicación",
                            noticiaId,
                            distancia
                    );
                }
                break;

            case "noticia_destacada":
                // Verificar preferencia de notificaciones destacadas
                if (UsuarioPreferences.getNotificacionesDestacadas(this)) {
                    notificationHelper.mostrarNotificacionNoticia(
                            "⭐ " + (titulo != null ? titulo : "Noticia destacada"),
                            mensaje,
                            noticiaId
                    );
                }
                break;

            case "alerta":
            case "urgente":
                // Las alertas siempre se muestran (son importantes)
                notificationHelper.mostrarNotificacionAlerta(
                        titulo != null ? titulo : "Alerta",
                        mensaje != null ? mensaje : "Alerta importante"
                );
                break;

            case "general":
            default:
                // Notificación general
                notificationHelper.mostrarNotificacionGeneral(titulo, mensaje);
                break;
        }
    }

    /**
     * Envía el token FCM al servidor backend
     * Esto permite enviar notificaciones dirigidas a este dispositivo
     */
    private void sendTokenToServer(String token) {
        String userId = UsuarioPreferences.getUserId(this);

        if (userId != null && !userId.isEmpty()) {
            // Aquí podrías implementar el envío al backend
            // FirebaseManager.getInstance().actualizarTokenFCM(userId, token);
            Log.d(TAG, "Token FCM listo para sincronizar con backend para usuario: " + userId);
        } else {
            Log.d(TAG, "Usuario no logueado - token guardado localmente");
        }
    }

    /**
     * Obtiene el token FCM guardado localmente
     */
    public static String getStoredToken(Context context) {
        return UsuarioPreferences.getFCMToken(context);
    }

    /**
     * Verifica si hay un token FCM guardado
     */
    public static boolean hasStoredToken(Context context) {
        String token = getStoredToken(context);
        return token != null && !token.isEmpty();
    }
}

