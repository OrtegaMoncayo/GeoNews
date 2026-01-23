package com.tesistitulacion.noticiaslocales.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * Gestor de idiomas para la aplicación GeoNews
 * Permite cambiar el idioma de toda la aplicación y persistir la preferencia
 */
public class LocaleManager {

    private static final String PREF_NAME = "locale_prefs";
    private static final String KEY_LANGUAGE = "language_code";
    private static final String DEFAULT_LANGUAGE = "es"; // Español por defecto

    /**
     * Idiomas soportados
     */
    public static final String SPANISH = "es";
    public static final String ENGLISH = "en";
    public static final String PORTUGUESE = "pt";

    /**
     * Aplica el idioma guardado al contexto
     * Debe llamarse en onCreate() de cada Activity
     */
    public static Context applyLocale(Context context) {
        String language = getLanguage(context);
        return setLocale(context, language);
    }

    /**
     * Cambia el idioma de la aplicación
     * @param activity Actividad actual
     * @param languageCode Código de idioma (es, en, pt)
     */
    public static void changeLanguage(Activity activity, String languageCode) {
        // Guardar preferencia
        saveLanguage(activity, languageCode);

        // Aplicar idioma
        setLocale(activity, languageCode);

        // Reiniciar la actividad actual para aplicar cambios
        Intent intent = activity.getIntent();
        activity.finish();
        activity.startActivity(intent);
    }

    /**
     * Establece el idioma en el contexto
     */
    private static Context setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            return context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        }
    }

    /**
     * Obtiene el código de idioma guardado
     */
    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE);
    }

    /**
     * Guarda el código de idioma
     */
    private static void saveLanguage(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    /**
     * Obtiene el nombre del idioma actual en su propio idioma
     */
    public static String getCurrentLanguageName(Context context) {
        String code = getLanguage(context);
        return getLanguageName(code);
    }

    /**
     * Obtiene el nombre del idioma por código
     */
    public static String getLanguageName(String languageCode) {
        switch (languageCode) {
            case SPANISH:
                return "Español";
            case ENGLISH:
                return "English";
            case PORTUGUESE:
                return "Português";
            default:
                return "Español";
        }
    }

    /**
     * Obtiene todos los idiomas disponibles
     */
    public static String[] getAvailableLanguages() {
        return new String[]{"Español", "English", "Português"};
    }

    /**
     * Obtiene los códigos de todos los idiomas disponibles
     */
    public static String[] getAvailableLanguageCodes() {
        return new String[]{SPANISH, ENGLISH, PORTUGUESE};
    }

    /**
     * Obtiene el índice del idioma actual
     */
    public static int getCurrentLanguageIndex(Context context) {
        String currentLanguage = getLanguage(context);
        String[] codes = getAvailableLanguageCodes();

        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(currentLanguage)) {
                return i;
            }
        }

        return 0; // Por defecto español
    }
}
