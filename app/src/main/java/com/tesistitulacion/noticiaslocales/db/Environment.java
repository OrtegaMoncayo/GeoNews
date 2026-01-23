package com.tesistitulacion.noticiaslocales.db;

/**
 * Configuración de entornos para la aplicación
 *
 * Uso:
 * 1. Cambiar CURRENT_ENV para alternar entre desarrollo y producción
 * 2. En desarrollo, usar emulador o dispositivo físico
 * 3. En producción, usar Cloud Run
 */
public class Environment {

    // ==================== CONFIGURACIÓN DE ENTORNO ====================

    /**
     * Entorno actual de la aplicación
     *
     * Cambiar este valor para alternar entre entornos:
     * - DEVELOPMENT: Desarrollo local
     * - PRODUCTION: Google Cloud Run
     */
    public static final EnvType CURRENT_ENV = EnvType.PRODUCTION;

    // ==================== TIPOS DE ENTORNO ====================

    public enum EnvType {
        DEVELOPMENT,
        PRODUCTION
    }

    // ==================== URLs POR ENTORNO ====================

    /**
     * URL para desarrollo local - Emulador Android
     */
    private static final String DEV_URL_EMULATOR = "http://10.0.2.2:8000/";

    /**
     * URL para desarrollo local - Dispositivo físico
     * IMPORTANTE: Cambiar por la IP de tu PC en la red local
     *
     * Para obtener tu IP:
     * - Windows: cmd → ipconfig → buscar "IPv4"
     * - Mac/Linux: terminal → ifconfig → buscar "inet"
     */
    private static final String DEV_URL_DEVICE = "http://192.168.1.100:8000/"; // Cambiar esta IP

    /**
     * URL para producción - Google Cloud Run
     */
    private static final String PROD_URL = "https://noticias-ibarra-fastapi-166115544761.us-central1.run.app/";

    // ==================== MÉTODOS PÚBLICOS ====================

    /**
     * Obtiene la URL base según el entorno actual
     */
    public static String getBaseUrl() {
        switch (CURRENT_ENV) {
            case PRODUCTION:
                return PROD_URL;
            case DEVELOPMENT:
            default:
                return isEmulator() ? DEV_URL_EMULATOR : DEV_URL_DEVICE;
        }
    }

    /**
     * Verifica si está en modo de producción
     */
    public static boolean isProduction() {
        return CURRENT_ENV == EnvType.PRODUCTION;
    }

    /**
     * Verifica si está en modo de desarrollo
     */
    public static boolean isDevelopment() {
        return CURRENT_ENV == EnvType.DEVELOPMENT;
    }

    /**
     * Obtiene información del entorno actual
     */
    public static String getEnvironmentInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Entorno: ").append(CURRENT_ENV.name()).append("\n");
        info.append("URL Base: ").append(getBaseUrl()).append("\n");
        info.append("Dispositivo: ").append(isEmulator() ? "Emulador" : "Físico").append("\n");
        return info.toString();
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Detecta si está corriendo en emulador
     */
    private static boolean isEmulator() {
        return android.os.Build.FINGERPRINT.contains("generic")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.HARDWARE.contains("goldfish")
                || android.os.Build.HARDWARE.contains("ranchu");
    }
}
