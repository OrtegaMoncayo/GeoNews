package com.tesistitulacion.noticiaslocales.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Helper para animaciones comunes reutilizables
 * Centraliza las animaciones usadas en toda la app para consistencia
 */
public class AnimationHelper {

    // Duraciones estándar
    public static final int DURATION_SHORT = 150;
    public static final int DURATION_MEDIUM = 300;
    public static final int DURATION_LONG = 500;

    /**
     * Animación fade in + slide up (aparición desde abajo)
     * Común en RecyclerViews y cards
     *
     * @param view View a animar
     */
    public static void fadeInSlideUp(View view) {
        fadeInSlideUp(view, DURATION_LONG, 0);
    }

    /**
     * Animación fade in + slide up con delay personalizado
     *
     * @param view View a animar
     * @param duration Duración de la animación
     * @param startDelay Delay antes de iniciar
     */
    public static void fadeInSlideUp(View view, int duration, int startDelay) {
        view.setAlpha(0f);
        view.setTranslationY(50f);
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(duration)
            .setStartDelay(startDelay)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }

    /**
     * Animación fade out + slide down (desaparición hacia abajo)
     *
     * @param view View a animar
     * @param onComplete Callback al completar (puede ser null)
     */
    public static void fadeOutSlideDown(View view, Runnable onComplete) {
        view.animate()
            .alpha(0f)
            .translationY(50f)
            .setDuration(DURATION_MEDIUM)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                view.setVisibility(View.GONE);
                if (onComplete != null) {
                    onComplete.run();
                }
            })
            .start();
    }

    /**
     * Animación fade in + slide down (aparición desde arriba)
     *
     * @param view View a animar
     */
    public static void fadeInSlideDown(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        view.setTranslationY(-50f);
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(DURATION_MEDIUM)
            .setStartDelay(100)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }

    /**
     * Animación fade out + slide up (desaparición hacia arriba)
     *
     * @param view View a animar
     * @param onComplete Callback al completar
     */
    public static void fadeOutSlideUp(View view, Runnable onComplete) {
        view.animate()
            .alpha(0f)
            .translationY(-50f)
            .setDuration(DURATION_SHORT)
            .withEndAction(() -> {
                view.setVisibility(View.GONE);
                if (onComplete != null) {
                    onComplete.run();
                }
            })
            .start();
    }

    /**
     * Animación de pulso/escala (efecto de énfasis)
     * Útil para botones y notificaciones
     *
     * @param view View a animar
     */
    public static void pulse(View view) {
        pulse(view, 1.2f);
    }

    /**
     * Animación de pulso con escala personalizada
     *
     * @param view View a animar
     * @param scale Escala máxima (ej: 1.2f = 120%)
     */
    public static void pulse(View view, float scale) {
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(DURATION_SHORT)
            .setInterpolator(new OvershootInterpolator())
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(DURATION_SHORT)
                    .start();
            })
            .start();
    }

    /**
     * Animación de click (pequeña escala hacia abajo y volver)
     *
     * @param view View a animar
     */
    public static void clickEffect(View view) {
        view.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .setDuration(100)
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }

    /**
     * Animación de shake (sacudir horizontalmente)
     * Útil para indicar error o input inválido
     *
     * @param view View a animar
     */
    public static void shake(View view) {
        view.animate()
            .translationX(-10f)
            .setDuration(50)
            .withEndAction(() -> {
                view.animate()
                    .translationX(10f)
                    .setDuration(50)
                    .withEndAction(() -> {
                        view.animate()
                            .translationX(-10f)
                            .setDuration(50)
                            .withEndAction(() -> {
                                view.animate()
                                    .translationX(0f)
                                    .setDuration(50)
                                    .start();
                            })
                            .start();
                    })
                    .start();
            })
            .start();
    }

    /**
     * Simple fade in
     *
     * @param view View a animar
     */
    public static void fadeIn(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        view.animate()
            .alpha(1f)
            .setDuration(DURATION_MEDIUM)
            .start();
    }

    /**
     * Simple fade out
     *
     * @param view View a animar
     * @param hideOnComplete Si se debe ocultar (GONE) al terminar
     */
    public static void fadeOut(View view, boolean hideOnComplete) {
        view.animate()
            .alpha(0f)
            .setDuration(DURATION_MEDIUM)
            .withEndAction(() -> {
                if (hideOnComplete) {
                    view.setVisibility(View.GONE);
                }
            })
            .start();
    }

    /**
     * Rotación de 360 grados
     * Útil para iconos de refresh
     *
     * @param view View a rotar
     */
    public static void rotate360(View view) {
        view.animate()
            .rotation(360f)
            .setDuration(DURATION_LONG)
            .withEndAction(() -> view.setRotation(0f))
            .start();
    }

    /**
     * Animación de aparición con bounce (rebote)
     *
     * @param view View a animar
     */
    public static void bounceIn(View view) {
        view.setVisibility(View.VISIBLE);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(DURATION_MEDIUM)
            .setInterpolator(new OvershootInterpolator(2f))
            .start();
    }

    /**
     * Animación de desaparición con bounce inverso
     *
     * @param view View a animar
     * @param onComplete Callback al completar
     */
    public static void bounceOut(View view, Runnable onComplete) {
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(DURATION_MEDIUM)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                view.setVisibility(View.GONE);
                if (onComplete != null) {
                    onComplete.run();
                }
            })
            .start();
    }

    /**
     * Slide desde la izquierda
     *
     * @param view View a animar
     */
    public static void slideInFromLeft(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationX(-view.getWidth());
        view.animate()
            .translationX(0f)
            .setDuration(DURATION_MEDIUM)
            .setInterpolator(new DecelerateInterpolator())
            .start();
    }

    /**
     * Slide hacia la derecha y ocultar
     *
     * @param view View a animar
     * @param onComplete Callback al completar
     */
    public static void slideOutToRight(View view, Runnable onComplete) {
        view.animate()
            .translationX(view.getWidth())
            .setDuration(DURATION_MEDIUM)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                view.setVisibility(View.GONE);
                view.setTranslationX(0f); // Reset para próxima vez
                if (onComplete != null) {
                    onComplete.run();
                }
            })
            .start();
    }

    /**
     * Cancela todas las animaciones activas en una view
     *
     * @param view View cuyas animaciones cancelar
     */
    public static void cancelAnimations(View view) {
        view.animate().cancel();
        view.clearAnimation();
    }

    /**
     * Reset completo de transformaciones de una view
     *
     * @param view View a resetear
     */
    public static void resetTransforms(View view) {
        view.setAlpha(1f);
        view.setTranslationX(0f);
        view.setTranslationY(0f);
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setRotation(0f);
    }

    /**
     * Animación especial para botón de bookmark/favorito
     * Efecto de "pop" con escala mayor y rotación sutil
     *
     * @param view View a animar
     * @param isSaved Si la noticia está siendo guardada (true) o eliminada (false)
     * @param onMidAnimation Callback en el medio de la animación para cambiar el icono
     */
    public static void bookmarkToggle(View view, boolean isSaved, Runnable onMidAnimation) {
        // Si está guardando, hacer animación más dramática
        float maxScale = isSaved ? 1.4f : 1.2f;
        float rotation = isSaved ? 15f : -10f;
        int duration = isSaved ? 200 : 150;

        view.animate()
            .scaleX(0.6f)
            .scaleY(0.6f)
            .rotation(rotation)
            .setDuration(duration / 2)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                // Cambiar icono en el medio de la animación
                if (onMidAnimation != null) {
                    onMidAnimation.run();
                }

                // Segunda fase: expandir con bounce
                view.animate()
                    .scaleX(maxScale)
                    .scaleY(maxScale)
                    .rotation(0f)
                    .setDuration(duration)
                    .setInterpolator(new OvershootInterpolator(3f))
                    .withEndAction(() -> {
                        // Volver a tamaño normal
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(duration / 2)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    })
                    .start();
            })
            .start();
    }

    /**
     * Animación de "latido" para botones de favoritos
     * Simula un corazón latiendo
     *
     * @param view View a animar
     */
    public static void heartbeat(View view) {
        view.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(100)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        view.animate()
                            .scaleX(1.15f)
                            .scaleY(1.15f)
                            .setDuration(80)
                            .withEndAction(() -> {
                                view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(80)
                                    .start();
                            })
                            .start();
                    })
                    .start();
            })
            .start();
    }

    /**
     * Animación de estrella/sparkle para guardar
     * Escala con rotación y rebote
     *
     * @param view View a animar
     */
    public static void starSave(View view) {
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .rotation(-180f)
            .setDuration(150)
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .rotation(0f)
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator(4f))
                    .withEndAction(() -> {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .start();
                    })
                    .start();
            })
            .start();
    }

    /**
     * Animación de eliminación suave
     * Desvanece y escala hacia abajo
     *
     * @param view View a animar
     * @param onMidAnimation Callback para cambiar contenido
     */
    public static void removeAnimation(View view, Runnable onMidAnimation) {
        view.animate()
            .scaleX(0.8f)
            .scaleY(0.8f)
            .alpha(0.3f)
            .setDuration(100)
            .withEndAction(() -> {
                if (onMidAnimation != null) {
                    onMidAnimation.run();
                }
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(150)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
            })
            .start();
    }
}
