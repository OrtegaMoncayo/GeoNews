package com.tesistitulacion.noticiaslocales.db;

/**
 * Configuración centralizada de URLs de la API
 *
 * IMPORTANTE:
 * - Para cambiar entre desarrollo y producción, editar Environment.CURRENT_ENV
 * - No cambiar BASE_URL directamente, usar Environment.getBaseUrl()
 *
 * @see Environment
 */
public class ApiConfig {
    // ==================== CONFIGURACIÓN ====================

    /**
     * URL base de la API - Se obtiene dinámicamente según el entorno
     *
     * Para cambiar de entorno:
     * 1. Ir a Environment.java
     * 2. Cambiar CURRENT_ENV a DEVELOPMENT o PRODUCTION
     */
    public static final String BASE_URL = Environment.getBaseUrl();

    // ==================== ENDPOINTS ====================

    // Noticias
    public static final String NOTICIAS_URL = BASE_URL + "noticias";
    public static final String NOTICIAS_CERCANAS_URL = BASE_URL + "noticias/cercanas";
    public static final String NOTICIAS_DESTACADAS_URL = BASE_URL + "noticias/destacadas";

    // Usuarios / Autenticación (TODO: Implementar en backend)
    public static final String AUTH_LOGIN_URL = BASE_URL + "auth/login";
    public static final String AUTH_REGISTER_URL = BASE_URL + "auth/register";
    public static final String AUTH_LOGOUT_URL = BASE_URL + "auth/logout";
    public static final String USUARIOS_URL = BASE_URL + "usuarios";
    public static final String USUARIOS_PERFIL_URL = BASE_URL + "usuarios/perfil";

    // Categorías
    public static final String CATEGORIAS_URL = BASE_URL + "categorias";

    // Eventos
    public static final String EVENTOS_URL = BASE_URL + "eventos";

    // Parroquias
    public static final String PARROQUIAS_URL = BASE_URL + "parroquias";

    // ==================== CONFIGURACIÓN HTTP ====================

    public static final int CONNECT_TIMEOUT = 10; // segundos
    public static final int READ_TIMEOUT = 30; // segundos
    public static final int WRITE_TIMEOUT = 30; // segundos

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Obtiene URL de noticia específica
     * @param id ID de Firestore (String) o ID numérico
     */
    public static String getNoticiaUrl(String id) {
        return NOTICIAS_URL + "/" + id;
    }

    /**
     * Obtiene URL de usuario específico
     * @param id ID de Firestore (String) o ID numérico
     */
    public static String getUsuarioUrl(String id) {
        return USUARIOS_URL + "/" + id;
    }

    /**
     * Obtiene URL de categoría específica
     * @param id ID de Firestore (String) o ID numérico
     */
    public static String getCategoriaUrl(String id) {
        return CATEGORIAS_URL + "/" + id;
    }

    /**
     * Obtiene URL de evento específico
     * @param id ID de Firestore (String) o ID numérico
     */
    public static String getEventoUrl(String id) {
        return EVENTOS_URL + "/" + id;
    }

    /**
     * Obtiene URL de parroquia específica
     * @param id ID de Firestore (String) o ID numérico
     */
    public static String getParroquiaUrl(String id) {
        return PARROQUIAS_URL + "/" + id;
    }

    // Métodos sobrecargados para compatibilidad con código existente
    public static String getNoticiaUrl(int id) {
        return getNoticiaUrl(String.valueOf(id));
    }

    public static String getUsuarioUrl(int id) {
        return getUsuarioUrl(String.valueOf(id));
    }

    public static String getCategoriaUrl(int id) {
        return getCategoriaUrl(String.valueOf(id));
    }

    public static String getEventoUrl(int id) {
        return getEventoUrl(String.valueOf(id));
    }

    public static String getParroquiaUrl(int id) {
        return getParroquiaUrl(String.valueOf(id));
    }

    /**
     * Verifica si la URL base está configurada
     */
    public static boolean isConfigured() {
        return !BASE_URL.contains("10.0.2.2") || isEmulator();
    }

    /**
     * Detecta si está corriendo en emulador
     */
    private static boolean isEmulator() {
        return android.os.Build.FINGERPRINT.contains("generic")
                || android.os.Build.MODEL.contains("Emulator");
    }
}
