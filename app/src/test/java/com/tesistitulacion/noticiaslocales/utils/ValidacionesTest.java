package com.tesistitulacion.noticiaslocales.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para validaciones comunes
 *
 * Ejecutar con: gradlew.bat test
 */
public class ValidacionesTest {

    // ==================== VALIDACIÓN DE EMAILS ====================

    @Test
    public void email_debeSerValido_formatoCorrecto() {
        assertTrue(esEmailValido("usuario@ejemplo.com"));
        assertTrue(esEmailValido("test.user@gmail.com"));
        assertTrue(esEmailValido("admin@noticiasibarra.ec"));
        assertTrue(esEmailValido("user123@domain.co.uk"));
    }

    @Test
    public void email_debeSerInvalido_formatoIncorrecto() {
        assertFalse(esEmailValido("usuario"));
        assertFalse(esEmailValido("usuario@"));
        assertFalse(esEmailValido("@ejemplo.com"));
        assertFalse(esEmailValido("usuario @ejemplo.com"));
        assertFalse(esEmailValido("usuario@ejemplo"));
    }

    @Test
    public void email_debeSerInvalido_cuandoEsNull() {
        assertFalse(esEmailValido(null));
    }

    @Test
    public void email_debeSerInvalido_cuandoEsVacio() {
        assertFalse(esEmailValido(""));
        assertFalse(esEmailValido("   "));
    }

    // ==================== VALIDACIÓN DE TELÉFONOS ====================

    @Test
    public void telefono_debeSerValido_formatoEcuatoriano() {
        assertTrue(esTelefonoValido("0991234567"));  // Celular
        assertTrue(esTelefonoValido("0626051234"));  // Fijo Ibarra (06 = código Imbabura)
        assertTrue(esTelefonoValido("0999999999"));
    }

    @Test
    public void telefono_debeSerInvalido_longitudIncorrecta() {
        assertFalse(esTelefonoValido("12345"));
        assertFalse(esTelefonoValido("099123"));
        assertFalse(esTelefonoValido("099123456789"));
    }

    @Test
    public void telefono_debeSerInvalido_caracteresNoNumericos() {
        assertFalse(esTelefonoValido("099-123-4567"));
        assertFalse(esTelefonoValido("099 123 4567"));
        assertFalse(esTelefonoValido("099abc4567"));
    }

    @Test
    public void telefono_debeSerInvalido_cuandoEsNullOVacio() {
        assertFalse(esTelefonoValido(null));
        assertFalse(esTelefonoValido(""));
    }

    // ==================== VALIDACIÓN DE CONTRASEÑAS ====================

    @Test
    public void password_debeSerValido_cumpleRequisitos() {
        assertTrue(esPasswordSeguro("Password123"));
        assertTrue(esPasswordSeguro("MiClave2024"));
        assertTrue(esPasswordSeguro("SecurePass1"));
    }

    @Test
    public void password_debeSerInvalido_longitudMenorA8() {
        assertFalse(esPasswordSeguro("Pass1"));
        assertFalse(esPasswordSeguro("Ab1"));
    }

    @Test
    public void password_debeSerInvalido_sinMayusculas() {
        assertFalse(esPasswordSeguro("password123"));
    }

    @Test
    public void password_debeSerInvalido_sinMinusculas() {
        assertFalse(esPasswordSeguro("PASSWORD123"));
    }

    @Test
    public void password_debeSerInvalido_sinNumeros() {
        assertFalse(esPasswordSeguro("PasswordSeguro"));
    }

    @Test
    public void password_debeSerInvalido_cuandoEsNull() {
        assertFalse(esPasswordSeguro(null));
    }

    // ==================== VALIDACIÓN DE NOMBRES ====================

    @Test
    public void nombre_debeSerValido_soloLetras() {
        assertTrue(esNombreValido("Juan"));
        assertTrue(esNombreValido("María José"));
        assertTrue(esNombreValido("José Luis"));
    }

    @Test
    public void nombre_debeSerValido_conTildes() {
        assertTrue(esNombreValido("José"));
        assertTrue(esNombreValido("María"));
        assertTrue(esNombreValido("Andrés"));
    }

    @Test
    public void nombre_debeSerInvalido_conNumeros() {
        assertFalse(esNombreValido("Juan123"));
        assertFalse(esNombreValido("María2"));
    }

    @Test
    public void nombre_debeSerInvalido_conCaracteresEspeciales() {
        assertFalse(esNombreValido("Juan@"));
        assertFalse(esNombreValido("María#"));
    }

    @Test
    public void nombre_debeSerInvalido_muyCorto() {
        assertFalse(esNombreValido("J"));
        assertFalse(esNombreValido("Ab"));
    }

    @Test
    public void nombre_debeSerInvalido_cuandoEsNullOVacio() {
        assertFalse(esNombreValido(null));
        assertFalse(esNombreValido(""));
        assertFalse(esNombreValido("   "));
    }

    // ==================== VALIDACIÓN DE COORDENADAS ====================

    @Test
    public void coordenadas_debenSerValidas_rangoEcuador() {
        assertTrue(sonCoordenadasValidas(0.3476, -78.1223));  // Ibarra
        assertTrue(sonCoordenadasValidas(-0.25, -79.16));      // Quito
        assertTrue(sonCoordenadasValidas(-2.9, -79.0));        // Cuenca
    }

    @Test
    public void coordenadas_debenSerInvalidas_fueraDeRango() {
        assertFalse(sonCoordenadasValidas(100.0, -78.0));   // Latitud inválida
        assertFalse(sonCoordenadasValidas(0.0, 200.0));     // Longitud inválida
        assertFalse(sonCoordenadasValidas(-100.0, -200.0)); // Ambas inválidas
    }

    @Test
    public void coordenadas_debenSerInvalidas_cuandoSonNull() {
        assertFalse(sonCoordenadasValidas(null, -78.0));
        assertFalse(sonCoordenadasValidas(0.0, null));
        assertFalse(sonCoordenadasValidas(null, null));
    }

    // ==================== VALIDACIÓN DE URLS ====================

    @Test
    public void url_debeSerValida_formatoCorrecto() {
        assertTrue(esUrlValida("https://www.ejemplo.com"));
        assertTrue(esUrlValida("http://ejemplo.com"));
        assertTrue(esUrlValida("https://ejemplo.com/imagen.jpg"));
        assertTrue(esUrlValida("https://cdn.ejemplo.com/assets/img.png"));
    }

    @Test
    public void url_debeSerInvalida_formatoIncorrecto() {
        assertFalse(esUrlValida("www.ejemplo.com"));
        assertFalse(esUrlValida("ejemplo.com"));
        assertFalse(esUrlValida("htp://ejemplo.com"));
    }

    @Test
    public void url_debeSerInvalida_cuandoEsNullOVacia() {
        assertFalse(esUrlValida(null));
        assertFalse(esUrlValida(""));
    }

    // ==================== VALIDACIÓN DE FECHAS ====================

    @Test
    public void fecha_debeSerFutura() {
        Long fechaFutura = System.currentTimeMillis() + 86400000L; // +1 día
        assertTrue(esFechaFutura(fechaFutura));
    }

    @Test
    public void fecha_noDebeSerFutura_cuandoEsPasada() {
        Long fechaPasada = System.currentTimeMillis() - 86400000L; // -1 día
        assertFalse(esFechaFutura(fechaPasada));
    }

    @Test
    public void fecha_noDebeSerFutura_cuandoEsNull() {
        assertFalse(esFechaFutura(null));
    }

    // ==================== VALIDACIÓN DE STRINGS ====================

    @Test
    public void string_noDebeEstarVacio() {
        assertTrue(noEstaVacio("Texto"));
        assertTrue(noEstaVacio("   Texto   "));
    }

    @Test
    public void string_debeEstarVacio() {
        assertFalse(noEstaVacio(null));
        assertFalse(noEstaVacio(""));
        assertFalse(noEstaVacio("   "));
    }

    @Test
    public void string_debeTenerLongitudMinima() {
        assertTrue(tieneLongitudMinima("Hola", 3));
        assertTrue(tieneLongitudMinima("Hola Mundo", 5));
        assertFalse(tieneLongitudMinima("Hi", 5));
        assertFalse(tieneLongitudMinima(null, 5));
    }

    // ==================== MÉTODOS AUXILIARES DE VALIDACIÓN ====================

    private boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    private boolean esTelefonoValido(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return false;
        }
        // Formato Ecuador: 10 dígitos (celular: 09X o fijo: 0X)
        return telefono.matches("^0[0-9]{9}$");
    }

    private boolean esPasswordSeguro(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        // Debe contener al menos: 1 mayúscula, 1 minúscula, 1 número
        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneMinuscula = password.matches(".*[a-z].*");
        boolean tieneNumero = password.matches(".*[0-9].*");

        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }

    private boolean esNombreValido(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        if (nombre.trim().length() < 3) {
            return false;
        }
        // Solo letras y espacios (incluye tildes)
        return nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$");
    }

    private boolean sonCoordenadasValidas(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            return false;
        }
        // Rango válido: latitud [-90, 90], longitud [-180, 180]
        return latitud >= -90 && latitud <= 90 && longitud >= -180 && longitud <= 180;
    }

    private boolean esUrlValida(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private boolean esFechaFutura(Long timestamp) {
        if (timestamp == null) {
            return false;
        }
        return timestamp > System.currentTimeMillis();
    }

    private boolean noEstaVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    private boolean tieneLongitudMinima(String texto, int minimo) {
        if (texto == null) {
            return false;
        }
        return texto.length() >= minimo;
    }
}
