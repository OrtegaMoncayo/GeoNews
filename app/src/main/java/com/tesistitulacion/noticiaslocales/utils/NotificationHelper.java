package com.tesistitulacion.noticiaslocales.utils;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.tesistitulacion.noticiaslocales.R;
import com.tesistitulacion.noticiaslocales.activities.DetalleNoticiaActivity;
import com.tesistitulacion.noticiaslocales.activities.ListaNoticiasActivity;
import com.tesistitulacion.noticiaslocales.activities.SplashActivity;

/**
 * Helper para gestión centralizada de notificaciones
 * Cumple con todos los requisitos de Android 13+ (Tiramisu)
 *
 * Requisitos implementados:
 * - Permiso POST_NOTIFICATIONS (Android 13+)
 * - Canal de notificación con IMPORTANCE_HIGH
 * - SmallIcon obligatorio
 * - Soporte para mensajes notification y data
 */
public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    // Códigos de solicitud de permisos
    public static final int REQUEST_CODE_NOTIFICATIONS = 101;

    // IDs de canales de notificación
    public static final String CHANNEL_NOTICIAS = "canal_noticias";
    public static final String CHANNEL_NOTICIAS_CERCANAS = "canal_noticias_cercanas";
    public static final String CHANNEL_ALERTAS = "canal_alertas";
    public static final String CHANNEL_GENERAL = "canal_general";

    // Nombres de canales (visibles al usuario)
    private static final String CHANNEL_NOTICIAS_NAME = "Noticias";
    private static final String CHANNEL_NOTICIAS_CERCANAS_NAME = "Noticias Cercanas";
    private static final String CHANNEL_ALERTAS_NAME = "Alertas Urgentes";
    private static final String CHANNEL_GENERAL_NAME = "General";

    // Singleton
    private static NotificationHelper instance;
    private final Context context;

    private NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Obtiene la instancia singleton del helper
     */
    public static synchronized NotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationHelper(context);
        }
        return instance;
    }

    // ==================== PERMISOS ====================

    /**
     * Verifica si el permiso de notificaciones está concedido
     * @return true si tiene permiso o es Android < 13
     */
    public boolean tienePermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // En versiones anteriores no se requiere permiso explícito
    }

    /**
     * Solicita el permiso de notificaciones (Android 13+)
     * @param activity Activity desde la cual solicitar el permiso
     */
    public void solicitarPermisoNotificaciones(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!tienePermisoNotificaciones()) {
                Log.d(TAG, "Solicitando permiso POST_NOTIFICATIONS");
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATIONS
                );
            } else {
                Log.d(TAG, "Permiso de notificaciones ya concedido");
            }
        }
    }

    /**
     * Verifica si debe mostrar explicación del permiso
     */
    public boolean debeMostrarExplicacionPermiso(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, Manifest.permission.POST_NOTIFICATIONS);
        }
        return false;
    }

    // ==================== CANALES DE NOTIFICACIÓN ====================

    /**
     * Crea todos los canales de notificación necesarios
     * DEBE llamarse en onCreate de la Activity principal o Application
     */
    public void crearCanalesNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            // Canal de Noticias (prioridad alta)
            crearCanalNoticias(manager);

            // Canal de Noticias Cercanas (prioridad alta con vibración)
            crearCanalNoticiasCercanas(manager);

            // Canal de Alertas Urgentes (prioridad máxima)
            crearCanalAlertas(manager);

            // Canal General (prioridad normal)
            crearCanalGeneral(manager);

            Log.i(TAG, "Canales de notificación creados correctamente");
        }
    }

    private void crearCanalNoticias(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_NOTICIAS,
                    CHANNEL_NOTICIAS_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de nuevas noticias publicadas");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 250, 250, 250});
            channel.setShowBadge(true);

            // Sonido personalizado
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            manager.createNotificationChannel(channel);
        }
    }

    private void crearCanalNoticiasCercanas(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_NOTICIAS_CERCANAS,
                    CHANNEL_NOTICIAS_CERCANAS_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de noticias cerca de tu ubicación");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 250, 500});
            channel.setShowBadge(true);

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            manager.createNotificationChannel(channel);
        }
    }

    private void crearCanalAlertas(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ALERTAS,
                    CHANNEL_ALERTAS_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alertas urgentes e importantes");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 250, 500, 250, 500});
            channel.setShowBadge(true);
            channel.setBypassDnd(true); // Puede interrumpir No Molestar

            manager.createNotificationChannel(channel);
        }
    }

    private void crearCanalGeneral(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_GENERAL,
                    CHANNEL_GENERAL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notificaciones generales de la aplicación");
            channel.enableLights(true);
            channel.setLightColor(Color.CYAN);
            channel.setShowBadge(true);

            manager.createNotificationChannel(channel);
        }
    }

    // ==================== MOSTRAR NOTIFICACIONES ====================

    /**
     * Muestra una notificación de noticia nueva
     */
    public void mostrarNotificacionNoticia(String titulo, String mensaje, String noticiaId) {
        if (!tienePermisoNotificaciones()) {
            Log.w(TAG, "Sin permiso para mostrar notificaciones");
            return;
        }

        Intent intent;
        if (noticiaId != null && !noticiaId.isEmpty()) {
            // Abrir detalle de noticia
            intent = new Intent(context, DetalleNoticiaActivity.class);
            intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticiaId);
        } else {
            // Abrir lista de noticias
            intent = new Intent(context, ListaNoticiasActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_NOTICIAS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo != null ? titulo : "Nueva noticia")
                .setContentText(mensaje != null ? mensaje : "Hay una nueva noticia disponible")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(context.getResources().getColor(R.color.primary, context.getTheme()))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 250, 250, 250});

        mostrarNotificacion(builder);
    }

    /**
     * Muestra una notificación de noticia cercana
     */
    public void mostrarNotificacionNoticiaCercana(String titulo, String mensaje,
                                                   String noticiaId, String distancia) {
        if (!tienePermisoNotificaciones()) {
            Log.w(TAG, "Sin permiso para mostrar notificaciones");
            return;
        }

        Intent intent;
        if (noticiaId != null && !noticiaId.isEmpty()) {
            intent = new Intent(context, DetalleNoticiaActivity.class);
            intent.putExtra(DetalleNoticiaActivity.EXTRA_NOTICIA_ID, noticiaId);
        } else {
            intent = new Intent(context, ListaNoticiasActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        String textoCompleto = mensaje;
        if (distancia != null && !distancia.isEmpty()) {
            textoCompleto = mensaje + " (a " + distancia + ")";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_NOTICIAS_CERCANAS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo != null ? titulo : "Noticia cerca de ti")
                .setContentText(textoCompleto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textoCompleto))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(context.getResources().getColor(R.color.success, context.getTheme()))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0, 500, 250, 500});

        mostrarNotificacion(builder);
    }

    /**
     * Muestra una notificación de alerta urgente
     */
    public void mostrarNotificacionAlerta(String titulo, String mensaje) {
        if (!tienePermisoNotificaciones()) {
            Log.w(TAG, "Sin permiso para mostrar notificaciones");
            return;
        }

        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ALERTAS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo != null ? titulo : "Alerta")
                .setContentText(mensaje != null ? mensaje : "Alerta importante")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(context.getResources().getColor(R.color.corporate_red, context.getTheme()))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setVibrate(new long[]{0, 500, 250, 500, 250, 500})
                .setLights(Color.RED, 1000, 500);

        mostrarNotificacion(builder);
    }

    /**
     * Muestra una notificación general
     */
    public void mostrarNotificacionGeneral(String titulo, String mensaje) {
        if (!tienePermisoNotificaciones()) {
            Log.w(TAG, "Sin permiso para mostrar notificaciones");
            return;
        }

        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo != null ? titulo : "GeoNews")
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(context.getResources().getColor(R.color.primary, context.getTheme()));

        mostrarNotificacion(builder);
    }

    /**
     * Método interno para mostrar la notificación
     */
    private void mostrarNotificacion(NotificationCompat.Builder builder) {
        try {
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            int notificationId = (int) System.currentTimeMillis();

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                manager.notify(notificationId, builder.build());
                Log.d(TAG, "Notificación mostrada con ID: " + notificationId);
            } else {
                Log.w(TAG, "Sin permiso POST_NOTIFICATIONS");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Error de seguridad al mostrar notificación", e);
        }
    }

    // ==================== UTILIDADES ====================

    /**
     * Cancela todas las notificaciones de la app
     */
    public void cancelarTodasLasNotificaciones() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.cancelAll();
        Log.d(TAG, "Todas las notificaciones canceladas");
    }

    /**
     * Cancela una notificación específica por ID
     */
    public void cancelarNotificacion(int notificationId) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.cancel(notificationId);
    }

    /**
     * Verifica si las notificaciones están habilitadas en ajustes del sistema
     */
    public boolean notificacionesHabilitadasEnSistema() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        return manager.areNotificationsEnabled();
    }

    /**
     * Abre los ajustes de notificación de la app en el sistema
     */
    public void abrirAjustesNotificaciones(Activity activity) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        activity.startActivity(intent);
    }
}
