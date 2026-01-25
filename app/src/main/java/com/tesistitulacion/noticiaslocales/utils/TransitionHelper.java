package com.tesistitulacion.noticiaslocales.utils;

import android.app.Activity;
import android.content.Intent;

import com.tesistitulacion.noticiaslocales.R;

/**
 * Helper para aplicar animaciones de transición entre actividades
 * Proporciona transiciones consistentes en toda la app
 */
public class TransitionHelper {

    /**
     * Tipos de transición disponibles
     */
    public enum TransitionType {
        SLIDE_RIGHT,    // Desliza desde la derecha (navegación hacia adelante)
        SLIDE_LEFT,     // Desliza desde la izquierda (navegación hacia atrás)
        SLIDE_UP,       // Desliza desde abajo (abrir detalle/modal)
        SLIDE_DOWN,     // Desliza hacia abajo (cerrar detalle/modal)
        FADE,           // Fade in/out suave
        NONE            // Sin animación
    }

    /**
     * Inicia una actividad con transición slide desde la derecha
     * Usar para navegación hacia adelante (abrir nueva pantalla)
     */
    public static void startActivitySlideRight(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Inicia una actividad con transición slide desde abajo
     * Usar para abrir detalles o modales
     */
    public static void startActivitySlideUp(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
    }

    /**
     * Inicia una actividad con transición fade
     * Usar para transiciones suaves (configuración, perfil)
     */
    public static void startActivityFade(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Inicia una actividad con el tipo de transición especificado
     */
    public static void startActivity(Activity activity, Intent intent, TransitionType type) {
        activity.startActivity(intent);
        applyEnterTransition(activity, type);
    }

    /**
     * Aplica la animación de entrada según el tipo
     */
    private static void applyEnterTransition(Activity activity, TransitionType type) {
        switch (type) {
            case SLIDE_RIGHT:
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case SLIDE_LEFT:
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case SLIDE_UP:
                activity.overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
                break;
            case SLIDE_DOWN:
                activity.overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
                break;
            case FADE:
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case NONE:
            default:
                // Sin animación
                break;
        }
    }

    /**
     * Finaliza la actividad con transición slide hacia la derecha
     * Usar para volver atrás con deslizamiento
     */
    public static void finishWithSlideRight(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Finaliza la actividad con transición slide hacia abajo
     * Usar para cerrar detalles o modales
     */
    public static void finishWithSlideDown(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
    }

    /**
     * Finaliza la actividad con transición fade
     * Usar para cerrar configuración, perfil, etc.
     */
    public static void finishWithFade(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Finaliza la actividad con el tipo de transición especificado
     */
    public static void finishActivity(Activity activity, TransitionType type) {
        activity.finish();
        applyExitTransition(activity, type);
    }

    /**
     * Aplica la animación de salida según el tipo
     */
    private static void applyExitTransition(Activity activity, TransitionType type) {
        switch (type) {
            case SLIDE_RIGHT:
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case SLIDE_LEFT:
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case SLIDE_UP:
                activity.overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
                break;
            case SLIDE_DOWN:
                activity.overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
                break;
            case FADE:
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case NONE:
            default:
                // Sin animación
                break;
        }
    }

    /**
     * Aplica transición de retorno cuando se presiona el botón back
     * Llamar en onBackPressed() de la actividad
     */
    public static void applyBackTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Aplica transición de retorno con slide down
     * Usar para cerrar detalles cuando se presiona back
     */
    public static void applyBackTransitionSlideDown(Activity activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
    }
}
