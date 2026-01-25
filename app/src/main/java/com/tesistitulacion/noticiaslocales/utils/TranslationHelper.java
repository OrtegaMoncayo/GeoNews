package com.tesistitulacion.noticiaslocales.utils;

import android.content.Context;
import android.util.Log;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

/**
 * Helper para traducción de texto usando ML Kit
 * Soporta traducción offline después de descargar los modelos de idioma
 */
public class TranslationHelper {

    private static final String TAG = "TranslationHelper";

    // Idiomas soportados
    public static final String SPANISH = TranslateLanguage.SPANISH;
    public static final String ENGLISH = TranslateLanguage.ENGLISH;
    public static final String FRENCH = TranslateLanguage.FRENCH;
    public static final String PORTUGUESE = TranslateLanguage.PORTUGUESE;
    public static final String GERMAN = TranslateLanguage.GERMAN;

    private Translator translator;
    private String sourceLanguage;
    private String targetLanguage;
    private boolean isModelDownloaded = false;

    /**
     * Callback para resultados de traducción
     */
    public interface TranslationCallback {
        void onSuccess(String translatedText);
        void onError(Exception e);
    }

    /**
     * Callback para descarga de modelos
     */
    public interface ModelDownloadCallback {
        void onSuccess();
        void onError(Exception e);
        void onProgress(String message);
    }

    /**
     * Constructor con idiomas por defecto (Español -> Inglés)
     */
    public TranslationHelper() {
        this(SPANISH, ENGLISH);
    }

    /**
     * Constructor con idiomas personalizados
     * @param sourceLanguage Idioma de origen
     * @param targetLanguage Idioma de destino
     */
    public TranslationHelper(String sourceLanguage, String targetLanguage) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        initializeTranslator();
    }

    /**
     * Inicializa el traductor con los idiomas configurados
     */
    private void initializeTranslator() {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build();

        translator = Translation.getClient(options);
        Log.d(TAG, "Traductor inicializado: " + sourceLanguage + " -> " + targetLanguage);
    }

    /**
     * Cambia el idioma de destino y reinicializa el traductor
     * @param newTargetLanguage Nuevo idioma de destino
     */
    public void setTargetLanguage(String newTargetLanguage) {
        if (!newTargetLanguage.equals(this.targetLanguage)) {
            this.targetLanguage = newTargetLanguage;
            close(); // Cerrar traductor anterior
            initializeTranslator();
            isModelDownloaded = false;
        }
    }

    /**
     * Intercambia los idiomas de origen y destino
     */
    public void swapLanguages() {
        String temp = sourceLanguage;
        sourceLanguage = targetLanguage;
        targetLanguage = temp;
        close();
        initializeTranslator();
        isModelDownloaded = false;
    }

    /**
     * Verifica si el modelo necesario está descargado
     * @param callback Callback con el resultado
     */
    public void checkModelDownloaded(ModelDownloadCallback callback) {
        RemoteModelManager modelManager = RemoteModelManager.getInstance();
        TranslateRemoteModel model = new TranslateRemoteModel.Builder(targetLanguage).build();

        modelManager.isModelDownloaded(model)
                .addOnSuccessListener(isDownloaded -> {
                    isModelDownloaded = isDownloaded;
                    if (isDownloaded) {
                        callback.onSuccess();
                    } else {
                        callback.onProgress("Modelo no descargado");
                    }
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Descarga el modelo de idioma necesario para traducción offline
     * @param callback Callback para seguimiento del progreso
     */
    public void downloadModelIfNeeded(ModelDownloadCallback callback) {
        callback.onProgress("Verificando modelo de idioma...");

        // Condiciones de descarga: solo con WiFi para ahorrar datos
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> {
                    isModelDownloaded = true;
                    Log.d(TAG, "Modelo descargado exitosamente");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al descargar modelo", e);
                    // Intentar sin restricción de WiFi
                    DownloadConditions anyConditions = new DownloadConditions.Builder().build();
                    translator.downloadModelIfNeeded(anyConditions)
                            .addOnSuccessListener(unused -> {
                                isModelDownloaded = true;
                                callback.onSuccess();
                            })
                            .addOnFailureListener(callback::onError);
                });
    }

    /**
     * Traduce un texto
     * @param text Texto a traducir
     * @param callback Callback con el resultado
     */
    public void translate(String text, TranslationCallback callback) {
        if (text == null || text.trim().isEmpty()) {
            callback.onSuccess("");
            return;
        }

        if (!isModelDownloaded) {
            // Descargar modelo primero
            downloadModelIfNeeded(new ModelDownloadCallback() {
                @Override
                public void onSuccess() {
                    performTranslation(text, callback);
                }

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }

                @Override
                public void onProgress(String message) {
                    // Ignorar progreso durante traducción
                }
            });
        } else {
            performTranslation(text, callback);
        }
    }

    /**
     * Realiza la traducción efectiva
     */
    private void performTranslation(String text, TranslationCallback callback) {
        translator.translate(text)
                .addOnSuccessListener(translatedText -> {
                    Log.d(TAG, "Traducción exitosa: " + text.substring(0, Math.min(50, text.length())) + "...");
                    callback.onSuccess(translatedText);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al traducir", e);
                    callback.onError(e);
                });
    }

    /**
     * Obtiene el nombre legible del idioma
     * @param languageCode Código del idioma
     * @return Nombre del idioma en español
     */
    public static String getLanguageName(String languageCode) {
        switch (languageCode) {
            case TranslateLanguage.SPANISH:
                return "Español";
            case TranslateLanguage.ENGLISH:
                return "Inglés";
            case TranslateLanguage.FRENCH:
                return "Francés";
            case TranslateLanguage.PORTUGUESE:
                return "Portugués";
            case TranslateLanguage.GERMAN:
                return "Alemán";
            case TranslateLanguage.ITALIAN:
                return "Italiano";
            case TranslateLanguage.CHINESE:
                return "Chino";
            case TranslateLanguage.JAPANESE:
                return "Japonés";
            case TranslateLanguage.KOREAN:
                return "Coreano";
            case TranslateLanguage.RUSSIAN:
                return "Ruso";
            case TranslateLanguage.ARABIC:
                return "Árabe";
            default:
                return languageCode;
        }
    }

    /**
     * Obtiene el idioma de origen actual
     */
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    /**
     * Obtiene el idioma de destino actual
     */
    public String getTargetLanguage() {
        return targetLanguage;
    }

    /**
     * Verifica si el modelo está descargado
     */
    public boolean isReady() {
        return isModelDownloaded;
    }

    /**
     * Libera recursos del traductor
     * Llamar cuando ya no se necesite
     */
    public void close() {
        if (translator != null) {
            translator.close();
            translator = null;
        }
    }
}
