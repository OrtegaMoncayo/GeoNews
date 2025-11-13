package com.tesistitulacion.noticiaslocales.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.activities.ListaNoticiasActivity;

/**
 * Servicio para manejar notificaciones push de Firebase Cloud Messaging
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "noticias_ibarra_channel";
    private static final String CHANNEL_NAME = "Noticias Ibarra";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Mensaje recibido de: " + remoteMessage.getFrom());

        // Verificar si el mensaje contiene datos
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Datos del mensaje: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }

        // Verificar si el mensaje contiene notificación
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notificación: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            sendNotification(title, body);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo token FCM: " + token);

        // TODO: Enviar el token al servidor (Firestore)
        // Puedes guardar esto en la colección 'usuarios' o 'tokens'
        sendTokenToServer(token);
    }

    /**
     * Maneja mensajes de datos personalizados
     */
    private void handleDataMessage(java.util.Map<String, String> data) {
        String type = data.get("type");
        String title = data.get("title");
        String message = data.get("message");

        if (type != null) {
            switch (type) {
                case "evento_nuevo":
                    // Manejar notificación de nuevo evento
                    sendNotification(title != null ? title : "Nuevo Evento", message);
                    break;
                case "noticia_nueva":
                    // Manejar notificación de nueva noticia
                    sendNotification(title != null ? title : "Nueva Noticia", message);
                    break;
                default:
                    sendNotification(title, message);
                    break;
            }
        }
    }

    /**
     * Envía el token al servidor Firestore
     */
    private void sendTokenToServer(String token) {
        // TODO: Implementar guardado del token en Firestore
        // Ejemplo:
        // FirebaseFirestore.getInstance()
        //     .collection("tokens")
        //     .document(userId)
        //     .set(new TokenData(token, new Date()));

        Log.d(TAG, "Token guardado localmente: " + token);

        // Guardar también en SharedPreferences como respaldo
        getSharedPreferences("fcm_prefs", MODE_PRIVATE)
                .edit()
                .putString("fcm_token", token)
                .apply();
    }

    /**
     * Muestra una notificación local
     */
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, ListaNoticiasActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info) // Ícono de sistema para notificaciones
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear canal de notificación para Android O y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal para notificaciones de eventos y noticias");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    /**
     * Obtiene el token FCM actual (para usar desde otras partes de la app)
     */
    public static String getStoredToken(Context context) {
        return context.getSharedPreferences("fcm_prefs", MODE_PRIVATE)
                .getString("fcm_token", null);
    }
}
