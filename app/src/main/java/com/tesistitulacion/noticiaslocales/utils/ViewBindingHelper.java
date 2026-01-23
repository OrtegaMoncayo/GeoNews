package com.tesistitulacion.noticiaslocales.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Helper para findViewById con null-safety y logging automático
 * Ayuda a detectar problemas de binding de views durante desarrollo
 */
public class ViewBindingHelper {

    private static final String TAG = "ViewBindingHelper";

    /**
     * findViewById con null-safety y logging de errores
     * Lanza RuntimeException si la view no se encuentra (fail-fast en desarrollo)
     *
     * @param activity Activity donde buscar la view
     * @param viewId ID del recurso de la view
     * @param viewName Nombre descriptivo de la view (para logging)
     * @param <T> Tipo de view esperado
     * @return View encontrada (nunca null)
     * @throws RuntimeException si la view no se encuentra
     */
    @NonNull
    public static <T extends View> T findViewSafe(@NonNull Activity activity, @IdRes int viewId, @NonNull String viewName) {
        T view = activity.findViewById(viewId);
        if (view == null) {
            String errorMsg = "❌ View no encontrada: " + viewName + " (ID: " + viewId + ") en " + activity.getClass().getSimpleName();
            Log.e(TAG, errorMsg);
            throw new RuntimeException(errorMsg);
        }
        Log.d(TAG, "✅ View encontrada: " + viewName + " en " + activity.getClass().getSimpleName());
        return view;
    }

    /**
     * findViewById con null-safety y logging de errores (desde una View padre)
     * Lanza RuntimeException si la view no se encuentra
     *
     * @param parentView View padre donde buscar
     * @param viewId ID del recurso de la view
     * @param viewName Nombre descriptivo de la view
     * @param <T> Tipo de view esperado
     * @return View encontrada (nunca null)
     * @throws RuntimeException si la view no se encuentra
     */
    @NonNull
    public static <T extends View> T findViewSafe(@NonNull View parentView, @IdRes int viewId, @NonNull String viewName) {
        T view = parentView.findViewById(viewId);
        if (view == null) {
            String errorMsg = "❌ View no encontrada: " + viewName + " (ID: " + viewId + ") en " + parentView.getClass().getSimpleName();
            Log.e(TAG, errorMsg);
            throw new RuntimeException(errorMsg);
        }
        Log.d(TAG, "✅ View encontrada: " + viewName);
        return view;
    }

    /**
     * findViewById opcional que retorna null si no se encuentra
     * Útil para views que pueden o no existir según el layout
     *
     * @param activity Activity donde buscar la view
     * @param viewId ID del recurso de la view
     * @param viewName Nombre descriptivo de la view (para logging)
     * @param <T> Tipo de view esperado
     * @return View encontrada o null
     */
    @Nullable
    public static <T extends View> T findViewOptional(@NonNull Activity activity, @IdRes int viewId, @NonNull String viewName) {
        T view = activity.findViewById(viewId);
        if (view == null) {
            Log.w(TAG, "⚠️ View opcional no encontrada: " + viewName + " en " + activity.getClass().getSimpleName());
        } else {
            Log.d(TAG, "✅ View opcional encontrada: " + viewName);
        }
        return view;
    }

    /**
     * findViewById opcional desde una View padre
     *
     * @param parentView View padre donde buscar
     * @param viewId ID del recurso de la view
     * @param viewName Nombre descriptivo de la view
     * @param <T> Tipo de view esperado
     * @return View encontrada o null
     */
    @Nullable
    public static <T extends View> T findViewOptional(@NonNull View parentView, @IdRes int viewId, @NonNull String viewName) {
        T view = parentView.findViewById(viewId);
        if (view == null) {
            Log.w(TAG, "⚠️ View opcional no encontrada: " + viewName);
        } else {
            Log.d(TAG, "✅ View opcional encontrada: " + viewName);
        }
        return view;
    }

    /**
     * Verifica si una view existe en el layout sin lanzar excepción
     *
     * @param activity Activity donde buscar
     * @param viewId ID del recurso de la view
     * @return true si la view existe
     */
    public static boolean viewExists(@NonNull Activity activity, @IdRes int viewId) {
        return activity.findViewById(viewId) != null;
    }

    /**
     * Verifica si una view existe en una View padre
     *
     * @param parentView View padre donde buscar
     * @param viewId ID del recurso de la view
     * @return true si la view existe
     */
    public static boolean viewExists(@NonNull View parentView, @IdRes int viewId) {
        return parentView.findViewById(viewId) != null;
    }

    /**
     * Oculta una view de manera segura (solo si existe)
     *
     * @param activity Activity donde buscar la view
     * @param viewId ID del recurso de la view
     */
    public static void hideViewSafe(@NonNull Activity activity, @IdRes int viewId) {
        View view = activity.findViewById(viewId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Muestra una view de manera segura (solo si existe)
     *
     * @param activity Activity donde buscar la view
     * @param viewId ID del recurso de la view
     */
    public static void showViewSafe(@NonNull Activity activity, @IdRes int viewId) {
        View view = activity.findViewById(viewId);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Toggle de visibilidad de manera segura
     *
     * @param activity Activity donde buscar la view
     * @param viewId ID del recurso de la view
     */
    public static void toggleVisibilitySafe(@NonNull Activity activity, @IdRes int viewId) {
        View view = activity.findViewById(viewId);
        if (view != null) {
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }
}
