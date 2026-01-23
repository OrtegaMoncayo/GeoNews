package com.tesistitulacion.noticiaslocales.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Helper para crear diálogos consistentes en toda la aplicación
 * Proporciona métodos para diálogos comunes con estilo uniforme
 */
public class DialogHelper {

    /**
     * Interfaz para callback de confirmación
     */
    public interface OnConfirmListener {
        void onConfirm();
    }

    /**
     * Interfaz para callback con opción de cancelar
     */
    public interface OnDialogActionListener {
        void onPositive();
        void onNegative();
    }

    /**
     * Muestra un diálogo de confirmación simple
     *
     * @param context Contexto
     * @param title Título del diálogo
     * @param message Mensaje del diálogo
     * @param listener Callback al confirmar (puede ser null)
     */
    public static void showConfirmationDialog(Context context, String title, String message, OnConfirmListener listener) {
        showConfirmationDialog(context, title, message, "Aceptar", "Cancelar", listener);
    }

    /**
     * Muestra un diálogo de confirmación con textos personalizados
     *
     * @param context Contexto
     * @param title Título del diálogo
     * @param message Mensaje del diálogo
     * @param positiveText Texto del botón positivo
     * @param negativeText Texto del botón negativo
     * @param listener Callback al confirmar
     */
    public static void showConfirmationDialog(Context context, String title, String message,
                                             String positiveText, String negativeText,
                                             OnConfirmListener listener) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText, (dialog, which) -> {
                if (listener != null) {
                    listener.onConfirm();
                }
            })
            .setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
    }

    /**
     * Muestra un diálogo de error con opción de reintentar
     *
     * @param context Contexto
     * @param errorMessage Mensaje de error
     * @param onRetry Callback para reintentar (puede ser null)
     */
    public static void showErrorWithRetry(Context context, String errorMessage, OnConfirmListener onRetry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(errorMessage)
            .setNegativeButton("Cerrar", (dialog, which) -> dialog.dismiss())
            .setCancelable(true);

        if (onRetry != null) {
            builder.setPositiveButton("Reintentar", (dialog, which) -> onRetry.onConfirm());
        }

        builder.show();
    }

    /**
     * Muestra un diálogo simple de error
     *
     * @param context Contexto
     * @param errorMessage Mensaje de error
     */
    public static void showError(Context context, String errorMessage) {
        new AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(errorMessage)
            .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
    }

    /**
     * Muestra un diálogo de éxito
     *
     * @param context Contexto
     * @param message Mensaje de éxito
     */
    public static void showSuccess(Context context, String message) {
        new AlertDialog.Builder(context)
            .setTitle("Éxito")
            .setMessage(message)
            .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
    }

    /**
     * Muestra un diálogo informativo
     *
     * @param context Contexto
     * @param title Título
     * @param message Mensaje
     */
    public static void showInfo(Context context, String title, String message) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
    }

    /**
     * Muestra un diálogo pidiendo activar GPS con opción de ir a configuración
     *
     * @param context Contexto
     */
    public static void showEnableGpsDialog(Context context) {
        new AlertDialog.Builder(context)
            .setTitle("GPS Desactivado")
            .setMessage("Esta función requiere que el GPS esté activado. ¿Deseas activarlo ahora?")
            .setPositiveButton("Activar", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            })
            .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
    }

    /**
     * Muestra un diálogo de confirmación de eliminación
     *
     * @param context Contexto
     * @param itemName Nombre del ítem a eliminar
     * @param onConfirm Callback al confirmar eliminación
     */
    public static void showDeleteConfirmation(Context context, String itemName, OnConfirmListener onConfirm) {
        new AlertDialog.Builder(context)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar \"" + itemName + "\"? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar", (dialog, which) -> {
                if (onConfirm != null) {
                    onConfirm.onConfirm();
                }
            })
            .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
    }

    /**
     * Muestra un diálogo con opciones personalizadas
     *
     * @param context Contexto
     * @param title Título
     * @param message Mensaje
     * @param listener Callback con acciones positiva/negativa
     */
    public static void showCustomDialog(Context context, String title, String message,
                                       String positiveText, String negativeText,
                                       OnDialogActionListener listener) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText, (dialog, which) -> {
                if (listener != null) {
                    listener.onPositive();
                }
            })
            .setNegativeButton(negativeText, (dialog, which) -> {
                if (listener != null) {
                    listener.onNegative();
                }
            })
            .setCancelable(true)
            .show();
    }

    /**
     * Muestra un diálogo de carga con mensaje
     *
     * @param context Contexto
     * @param message Mensaje de carga
     * @return AlertDialog para poder dismissarlo después
     */
    public static AlertDialog showLoadingDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
            .setTitle("Cargando...")
            .setMessage(message)
            .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    /**
     * Muestra un diálogo de lista de opciones
     *
     * @param context Contexto
     * @param title Título
     * @param options Array de opciones
     * @param onItemSelected Callback cuando se selecciona un ítem (recibe el índice)
     */
    public static void showListDialog(Context context, String title, String[] options,
                                     DialogInterface.OnClickListener onItemSelected) {
        new AlertDialog.Builder(context)
            .setTitle(title)
            .setItems(options, onItemSelected)
            .setCancelable(true)
            .show();
    }

    /**
     * Muestra un diálogo con input de texto
     * Nota: Para inputs más complejos, usar un DialogFragment personalizado
     *
     * @param context Contexto
     * @param title Título
     * @param hint Hint del input
     * @param onSubmit Callback con el texto ingresado
     */
    public static void showInputDialog(Context context, String title, String hint,
                                      OnInputSubmitListener onSubmit) {
        // Para simplificar, este método básico se puede extender con EditText
        // Por ahora solo muestra la estructura
        android.widget.EditText input = new android.widget.EditText(context);
        input.setHint(hint);

        new AlertDialog.Builder(context)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Aceptar", (dialog, which) -> {
                if (onSubmit != null) {
                    String text = input.getText().toString().trim();
                    onSubmit.onSubmit(text);
                }
            })
            .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
            .show();
    }

    /**
     * Interfaz para callback de input de texto
     */
    public interface OnInputSubmitListener {
        void onSubmit(String text);
    }

    /**
     * Muestra un diálogo de advertencia
     *
     * @param context Contexto
     * @param message Mensaje de advertencia
     */
    public static void showWarning(Context context, String message) {
        new AlertDialog.Builder(context)
            .setTitle("Advertencia")
            .setMessage(message)
            .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
            .setCancelable(true)
            .show();
    }
}
