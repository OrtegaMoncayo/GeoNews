package com.tesistitulacion.noticiaslocales.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tesistitulacion.noticiaslocales.firebase.FirebaseManager;

/**
 * Helper para gestionar callbacks de Firebase de manera centralizada
 * Reduce código duplicado en Activities y proporciona manejo consistente de errores
 */
public class FirebaseCallbackHelper {

    private static final String TAG = "FirebaseCallbackHelper";

    /**
     * Gestiona el estado de carga para evitar múltiples cargas simultáneas
     */
    public static class LoadingStateManager {
        private boolean cargando = false;

        /**
         * Intenta iniciar una carga
         * @return true si se puede iniciar, false si ya hay una carga en progreso
         */
        public synchronized boolean startLoading() {
            if (cargando) {
                Log.d(TAG, "Ya hay una carga en progreso");
                return false;
            }
            cargando = true;
            return true;
        }

        /**
         * Finaliza el estado de carga
         */
        public synchronized void stopLoading() {
            cargando = false;
        }

        /**
         * Verifica si hay una carga en progreso
         * @return true si está cargando
         */
        public synchronized boolean isLoading() {
            return cargando;
        }
    }

    /**
     * Interfaz funcional para ejecutar la carga real desde Firebase
     * @param <T> Tipo de datos que retorna la carga
     */
    public interface FirestoreLoader<T> {
        void load(FirebaseManager.FirestoreCallback<T> callback);
    }

    /**
     * Interfaz para manejar el éxito de la carga
     * @param <T> Tipo de datos cargados
     */
    public interface OnSuccessListener<T> {
        void onSuccess(T data);
    }

    /**
     * Carga datos de Firebase con manejo automático de estado, logging y feedback
     *
     * @param context Contexto de la aplicación
     * @param nombreRecurso Nombre del recurso que se está cargando (para mensajes)
     * @param loadingManager Gestor de estado de carga
     * @param loader Función que ejecuta la carga real
     * @param onSuccess Callback cuando la carga es exitosa
     * @param <T> Tipo de datos a cargar
     */
    public static <T> void cargarDatosConFeedback(
            final Context context,
            final String nombreRecurso,
            final LoadingStateManager loadingManager,
            final FirestoreLoader<T> loader,
            final OnSuccessListener<T> onSuccess) {

        // Verificar si ya hay una carga en progreso
        if (!loadingManager.startLoading()) {
            return;
        }

        // Log y feedback al usuario
        Log.d(TAG, "Iniciando carga de " + nombreRecurso + " desde Firebase...");
        Toast.makeText(context, "Cargando " + nombreRecurso + "...", Toast.LENGTH_SHORT).show();

        // Ejecutar la carga
        loader.load(new FirebaseManager.FirestoreCallback<T>() {
            @Override
            public void onSuccess(T data) {
                loadingManager.stopLoading();
                Log.d(TAG, nombreRecurso + " cargados exitosamente");

                // Llamar al callback de éxito
                if (onSuccess != null) {
                    onSuccess.onSuccess(data);
                }
            }

            @Override
            public void onError(Exception e) {
                loadingManager.stopLoading();
                Log.e(TAG, "Error al cargar " + nombreRecurso + ": " + e.getMessage(), e);
                Toast.makeText(context,
                        "Error al cargar " + nombreRecurso + ": " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Versión simplificada sin mostrar Toast de "Cargando..."
     * Útil para recargas silenciosas
     */
    public static <T> void cargarDatosSilencioso(
            final Context context,
            final String nombreRecurso,
            final LoadingStateManager loadingManager,
            final FirestoreLoader<T> loader,
            final OnSuccessListener<T> onSuccess) {

        if (!loadingManager.startLoading()) {
            return;
        }

        Log.d(TAG, "Carga silenciosa de " + nombreRecurso + "...");

        loader.load(new FirebaseManager.FirestoreCallback<T>() {
            @Override
            public void onSuccess(T data) {
                loadingManager.stopLoading();
                Log.d(TAG, nombreRecurso + " actualizados");

                if (onSuccess != null) {
                    onSuccess.onSuccess(data);
                }
            }

            @Override
            public void onError(Exception e) {
                loadingManager.stopLoading();
                Log.e(TAG, "Error al actualizar " + nombreRecurso + ": " + e.getMessage(), e);
                // No mostrar Toast en modo silencioso, solo log
            }
        });
    }
}
