package com.tesistitulacion.noticiaslocales.utils;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;

/**
 * Helper para configurar RecyclerView de manera consistente
 * Proporciona setup estándar con animaciones y layout manager
 */
public class RecyclerViewHelper {

    /**
     * Configura un RecyclerView con LinearLayoutManager y animación de entrada
     * @param recyclerView RecyclerView a configurar
     * @param context Contexto de la aplicación
     */
    public static void setupWithAnimation(RecyclerView recyclerView, Context context) {
        setupWithAnimation(recyclerView, context, true);
    }

    /**
     * Configura un RecyclerView con LinearLayoutManager y opción de animación
     * @param recyclerView RecyclerView a configurar
     * @param context Contexto de la aplicación
     * @param withAnimation Si se debe aplicar animación de entrada
     */
    public static void setupWithAnimation(RecyclerView recyclerView, Context context, boolean withAnimation) {
        // Configurar LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        // Optimización de rendimiento: indicar que el tamaño no cambiará
        recyclerView.setHasFixedSize(true);

        // Configurar caché de views para scroll suave
        recyclerView.setItemViewCacheSize(20);

        if (withAnimation) {
            // Aplicar animación inicial de aparición
            recyclerView.setAlpha(0f);
            recyclerView.setTranslationY(50f);
            recyclerView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(200)
                .start();
        }
    }

    /**
     * Configura un RecyclerView con orientación horizontal
     * @param recyclerView RecyclerView a configurar
     * @param context Contexto de la aplicación
     */
    public static void setupHorizontal(RecyclerView recyclerView, Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Aplica una animación de fade a un RecyclerView cuando se actualizan datos
     * Útil para mostrar visualmente que los datos cambiaron
     * @param recyclerView RecyclerView a animar
     */
    public static void updateDataWithFade(RecyclerView recyclerView) {
        recyclerView.animate()
            .alpha(0.3f)
            .setDuration(150)
            .withEndAction(() -> {
                recyclerView.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start();
            })
            .start();
    }

    /**
     * Desactiva animaciones de cambio de items
     * Útil cuando hay actualizaciones frecuentes en tiempo real
     * @param recyclerView RecyclerView a configurar
     */
    public static void disableChangeAnimations(RecyclerView recyclerView) {
        if (recyclerView.getItemAnimator() != null) {
            recyclerView.getItemAnimator().setChangeDuration(0);
        }
    }

    /**
     * Scroll suave al inicio del RecyclerView
     * @param recyclerView RecyclerView a scrollear
     */
    public static void smoothScrollToTop(RecyclerView recyclerView) {
        recyclerView.smoothScrollToPosition(0);
    }

    /**
     * Scroll instantáneo al inicio del RecyclerView
     * @param recyclerView RecyclerView a scrollear
     */
    public static void scrollToTop(RecyclerView recyclerView) {
        recyclerView.scrollToPosition(0);
    }

    /**
     * Verifica si el RecyclerView está en la parte superior
     * @param recyclerView RecyclerView a verificar
     * @return true si está en el tope
     */
    public static boolean isAtTop(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(-1);
    }

    /**
     * Verifica si el RecyclerView está en la parte inferior
     * @param recyclerView RecyclerView a verificar
     * @return true si está en el fondo
     */
    public static boolean isAtBottom(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }
}
