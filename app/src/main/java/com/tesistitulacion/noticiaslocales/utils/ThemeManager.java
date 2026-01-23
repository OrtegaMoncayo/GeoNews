package com.tesistitulacion.noticiaslocales.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.tesistitulacion.noticiaslocales.R;

/**
 * Gestor de temas (claro/oscuro/auto) para la aplicación
 */
public class ThemeManager {

    private static final String TAG = "ThemeManager";
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";

    // Constantes para los modos de tema
    public static final int THEME_AUTO = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;

    /**
     * Aplica el tema guardado en las preferencias
     * Por defecto: Modo AUTOMÁTICO (sigue al sistema)
     */
    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int themeMode = prefs.getInt(KEY_THEME_MODE, THEME_AUTO);

        Log.d(TAG, "Aplicando tema modo: " + themeMode);

        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_AUTO:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Cambia el tema y guarda la preferencia
     * @param themeMode 0 = Auto, 1 = Light, 2 = Dark
     */
    public static void setTheme(Context context, int themeMode) {
        Log.d(TAG, "Cambiando tema a modo: " + themeMode);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME_MODE, themeMode).apply();

        Log.d(TAG, "Preferencia guardada correctamente");

        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_AUTO:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        Log.d(TAG, "Modo de AppCompat configurado");
    }

    /**
     * Método legacy para compatibilidad
     */
    public static void setDarkMode(Context context, boolean isDarkMode) {
        setTheme(context, isDarkMode ? THEME_DARK : THEME_LIGHT);
    }

    /**
     * Obtiene el índice del tema actual (0 = Auto, 1 = Light, 2 = Dark)
     */
    public static int getCurrentThemeIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME_MODE, THEME_AUTO);
    }

    /**
     * Obtiene el nombre del tema actual para mostrar en UI
     */
    public static String getCurrentThemeName(Context context) {
        int themeIndex = getCurrentThemeIndex(context);
        switch (themeIndex) {
            case THEME_LIGHT:
                return context.getString(R.string.settings_theme_light);
            case THEME_DARK:
                return context.getString(R.string.settings_theme_dark);
            case THEME_AUTO:
            default:
                return context.getString(R.string.settings_theme_auto);
        }
    }

    /**
     * Verifica si el modo oscuro está activado
     */
    public static boolean isDarkMode(Context context) {
        int themeMode = getCurrentThemeIndex(context);
        if (themeMode == THEME_DARK) {
            return true;
        } else if (themeMode == THEME_LIGHT) {
            return false;
        } else {
            // Auto mode - check system setting
            int currentNightMode = context.getResources().getConfiguration().uiMode
                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        }
    }
}
