package com.tesistitulacion.noticiaslocales.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * Helper para Text-to-Speech (lectura de noticias en voz alta)
 * Soporta español e inglés con controles de reproducción
 */
public class TextToSpeechHelper {

    private static final String TAG = "TextToSpeechHelper";
    private static final String UTTERANCE_ID = "TTS_NOTICIA";

    private TextToSpeech textToSpeech;
    private Context context;
    private boolean isInitialized = false;
    private boolean isSpeaking = false;
    private boolean isPaused = false;

    private String currentText = "";
    private int currentPosition = 0;

    private TTSCallback callback;

    // Configuración de voz
    private float speechRate = 1.0f;
    private float pitch = 1.0f;
    private Locale currentLocale = new Locale("es", "ES");

    /**
     * Callback para eventos de TTS
     */
    public interface TTSCallback {
        void onInitialized(boolean success);
        void onSpeakStart();
        void onSpeakDone();
        void onSpeakError(String error);
        void onProgress(int position, int total);
    }

    /**
     * Constructor
     * @param context Contexto de la aplicación
     * @param callback Callback para eventos
     */
    public TextToSpeechHelper(Context context, TTSCallback callback) {
        this.context = context;
        this.callback = callback;
        initializeTTS();
    }

    /**
     * Inicializa el motor de TTS
     */
    private void initializeTTS() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(currentLocale);

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Idioma no soportado: " + currentLocale);
                    // Intentar con español genérico
                    result = textToSpeech.setLanguage(new Locale("es"));
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Usar inglés como fallback
                        textToSpeech.setLanguage(Locale.US);
                    }
                }

                textToSpeech.setSpeechRate(speechRate);
                textToSpeech.setPitch(pitch);

                setupProgressListener();

                isInitialized = true;
                Log.d(TAG, "TTS inicializado correctamente");

                if (callback != null) {
                    callback.onInitialized(true);
                }
            } else {
                Log.e(TAG, "Error al inicializar TTS");
                isInitialized = false;
                if (callback != null) {
                    callback.onInitialized(false);
                }
            }
        });
    }

    /**
     * Configura el listener de progreso
     */
    private void setupProgressListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                isSpeaking = true;
                isPaused = false;
                Log.d(TAG, "Inicio de lectura");
                if (callback != null) {
                    callback.onSpeakStart();
                }
            }

            @Override
            public void onDone(String utteranceId) {
                isSpeaking = false;
                isPaused = false;
                currentPosition = 0;
                Log.d(TAG, "Lectura completada");
                if (callback != null) {
                    callback.onSpeakDone();
                }
            }

            @Override
            public void onError(String utteranceId) {
                isSpeaking = false;
                isPaused = false;
                Log.e(TAG, "Error durante la lectura");
                if (callback != null) {
                    callback.onSpeakError("Error durante la lectura");
                }
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                currentPosition = start;
                if (callback != null && !currentText.isEmpty()) {
                    callback.onProgress(start, currentText.length());
                }
            }
        });
    }

    /**
     * Inicia la lectura del texto
     * @param text Texto a leer
     */
    public void speak(String text) {
        if (!isInitialized) {
            Log.e(TAG, "TTS no inicializado");
            if (callback != null) {
                callback.onSpeakError("Motor de voz no inicializado");
            }
            return;
        }

        if (text == null || text.trim().isEmpty()) {
            Log.w(TAG, "Texto vacío");
            return;
        }

        // Detener cualquier lectura anterior
        stop();

        currentText = text;
        currentPosition = 0;

        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID);
        Log.d(TAG, "Iniciando lectura de " + text.length() + " caracteres");
    }

    /**
     * Pausa la lectura
     */
    public void pause() {
        if (isSpeaking && !isPaused) {
            textToSpeech.stop();
            isPaused = true;
            isSpeaking = false;
            Log.d(TAG, "Lectura pausada en posición: " + currentPosition);
        }
    }

    /**
     * Reanuda la lectura desde donde se pausó
     */
    public void resume() {
        if (isPaused && !currentText.isEmpty()) {
            String remainingText = currentText.substring(currentPosition);
            if (!remainingText.isEmpty()) {
                textToSpeech.speak(remainingText, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID);
                isPaused = false;
                Log.d(TAG, "Reanudando lectura desde posición: " + currentPosition);
            }
        }
    }

    /**
     * Detiene la lectura completamente
     */
    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            isSpeaking = false;
            isPaused = false;
            currentPosition = 0;
            Log.d(TAG, "Lectura detenida");
        }
    }

    /**
     * Alterna entre reproducir/pausar
     */
    public void togglePlayPause() {
        if (isSpeaking) {
            pause();
        } else if (isPaused) {
            resume();
        } else if (!currentText.isEmpty()) {
            speak(currentText);
        }
    }

    /**
     * Cambia el idioma de lectura
     * @param languageCode Código del idioma (es, en, fr, etc.)
     */
    public void setLanguage(String languageCode) {
        if (!isInitialized) return;

        Locale newLocale;
        switch (languageCode.toLowerCase()) {
            case "en":
                newLocale = Locale.US;
                break;
            case "es":
                newLocale = new Locale("es", "ES");
                break;
            case "fr":
                newLocale = Locale.FRANCE;
                break;
            case "pt":
                newLocale = new Locale("pt", "BR");
                break;
            case "de":
                newLocale = Locale.GERMANY;
                break;
            default:
                newLocale = new Locale(languageCode);
        }

        int result = textToSpeech.setLanguage(newLocale);
        if (result != TextToSpeech.LANG_MISSING_DATA &&
            result != TextToSpeech.LANG_NOT_SUPPORTED) {
            currentLocale = newLocale;
            Log.d(TAG, "Idioma cambiado a: " + languageCode);
        } else {
            Log.w(TAG, "Idioma no soportado: " + languageCode);
        }
    }

    /**
     * Ajusta la velocidad de lectura
     * @param rate Velocidad (0.5 = lento, 1.0 = normal, 2.0 = rápido)
     */
    public void setSpeechRate(float rate) {
        if (rate < 0.25f) rate = 0.25f;
        if (rate > 4.0f) rate = 4.0f;

        this.speechRate = rate;
        if (isInitialized) {
            textToSpeech.setSpeechRate(rate);
            Log.d(TAG, "Velocidad ajustada a: " + rate);
        }
    }

    /**
     * Ajusta el tono de voz
     * @param pitch Tono (0.5 = grave, 1.0 = normal, 2.0 = agudo)
     */
    public void setPitch(float pitch) {
        if (pitch < 0.25f) pitch = 0.25f;
        if (pitch > 4.0f) pitch = 4.0f;

        this.pitch = pitch;
        if (isInitialized) {
            textToSpeech.setPitch(pitch);
            Log.d(TAG, "Tono ajustado a: " + pitch);
        }
    }

    /**
     * Verifica si está hablando actualmente
     */
    public boolean isSpeaking() {
        return isSpeaking;
    }

    /**
     * Verifica si está pausado
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Verifica si está inicializado
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Obtiene la velocidad actual
     */
    public float getSpeechRate() {
        return speechRate;
    }

    /**
     * Obtiene el tono actual
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Libera recursos del TTS
     * Llamar en onDestroy() de la Activity
     */
    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            isInitialized = false;
            Log.d(TAG, "TTS liberado");
        }
    }
}
