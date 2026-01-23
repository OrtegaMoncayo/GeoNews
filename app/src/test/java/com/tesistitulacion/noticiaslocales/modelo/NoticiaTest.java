package com.tesistitulacion.noticiaslocales.modelo;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para la clase Noticia
 *
 * Ejecutar con: gradlew.bat test
 */
public class NoticiaTest {

    private Noticia noticia;

    @Before
    public void setUp() {
        // Se ejecuta antes de cada test
        noticia = new Noticia();
    }

    // ==================== TESTS DE CONSTRUCTORES ====================

    @Test
    public void constructorVacio_debeCrearNoticiaNoNula() {
        Noticia nuevaNoticia = new Noticia();
        assertNotNull("La noticia no debe ser null", nuevaNoticia);
    }

    @Test
    public void constructorConParametros_debeAsignarValoresCorrectamente() {
        Noticia nuevaNoticia = new Noticia(
            "Título de prueba",
            "Descripción de prueba",
            "Contenido de prueba",
            1
        );

        assertEquals("Título de prueba", nuevaNoticia.getTitulo());
        assertEquals("Descripción de prueba", nuevaNoticia.getDescripcion());
        assertEquals("Contenido de prueba", nuevaNoticia.getContenido());
        assertEquals(Integer.valueOf(1), nuevaNoticia.getCategoriaId());
    }

    // ==================== TESTS DE GETTERS Y SETTERS ====================

    @Test
    public void setTitulo_debeAsignarYRecuperarCorrectamente() {
        String titulo = "Nueva noticia en Ibarra";
        noticia.setTitulo(titulo);

        assertEquals("El título debe coincidir", titulo, noticia.getTitulo());
    }

    @Test
    public void setDescripcion_debeAsignarYRecuperarCorrectamente() {
        String descripcion = "Esta es una descripción de prueba";
        noticia.setDescripcion(descripcion);

        assertEquals(descripcion, noticia.getDescripcion());
    }

    @Test
    public void setCategoriaId_debeAceptarValoresValidos() {
        noticia.setCategoriaId(1);
        assertEquals(Integer.valueOf(1), noticia.getCategoriaId());

        noticia.setCategoriaId(10);
        assertEquals(Integer.valueOf(10), noticia.getCategoriaId());
    }

    @Test
    public void setLatitudLongitud_debeAceptarCoordenadasValidas() {
        Double latitud = 0.3476;  // Ibarra
        Double longitud = -78.1223;

        noticia.setLatitud(latitud);
        noticia.setLongitud(longitud);

        assertEquals(latitud, noticia.getLatitud());
        assertEquals(longitud, noticia.getLongitud());
    }

    @Test
    public void setVisualizaciones_debeIncrementarCorrectamente() {
        noticia.setVisualizaciones(0);
        assertEquals(Integer.valueOf(0), noticia.getVisualizaciones());

        noticia.setVisualizaciones(1);
        assertEquals(Integer.valueOf(1), noticia.getVisualizaciones());

        noticia.setVisualizaciones(100);
        assertEquals(Integer.valueOf(100), noticia.getVisualizaciones());
    }

    @Test
    public void setDestacada_debeAlternarCorrectamente() {
        noticia.setDestacada(true);
        assertTrue("La noticia debe estar destacada", noticia.getDestacada());

        noticia.setDestacada(false);
        assertFalse("La noticia no debe estar destacada", noticia.getDestacada());
    }

    @Test
    public void setEstado_debeAceptarEstadosValidos() {
        noticia.setEstado("draft");
        assertEquals("draft", noticia.getEstado());

        noticia.setEstado("published");
        assertEquals("published", noticia.getEstado());

        noticia.setEstado("archived");
        assertEquals("archived", noticia.getEstado());
    }

    // ==================== TESTS DE MÉTODOS ESPECIALES ====================

    @Test
    public void getColorCategoria_debeRetornarColorPolitica() {
        noticia.setCategoriaId(1);
        assertEquals("#FF6B35", noticia.getColorCategoria());
    }

    @Test
    public void getColorCategoria_debeRetornarColorEconomia() {
        noticia.setCategoriaId(2);
        assertEquals("#004E89", noticia.getColorCategoria());
    }

    @Test
    public void getColorCategoria_debeRetornarColorCultura() {
        noticia.setCategoriaId(3);
        assertEquals("#9B59B6", noticia.getColorCategoria());
    }

    @Test
    public void getColorCategoria_debeRetornarColorDeportes() {
        noticia.setCategoriaId(4);
        assertEquals("#27AE60", noticia.getColorCategoria());
    }

    @Test
    public void getColorCategoria_debeRetornarColorEducacion() {
        noticia.setCategoriaId(5);
        assertEquals("#F39C12", noticia.getColorCategoria());
    }

    @Test
    public void getColorCategoria_debeRetornarColorSalud() {
        noticia.setCategoriaId(6);
        assertEquals("#E74C3C", noticia.getColorCategoria());
    }

    @Test
    public void getColorCategoria_debeRetornarColorPorDefecto_cuandoCategoriaEsNull() {
        noticia.setCategoriaId(null);
        assertEquals("#1976D2", noticia.getColorCategoria());
    }

    @Test
    public void getColorCategoria_debeRetornarColorPorDefecto_cuandoCategoriaEsInvalida() {
        noticia.setCategoriaId(999);
        assertEquals("#1976D2", noticia.getColorCategoria());
    }

    @Test
    public void getFechaPublicacion_debeRetornarNull_cuandoFechaCreacionEsNull() {
        noticia.setFechaCreacion(null);
        assertNull(noticia.getFechaPublicacion());
    }

    @Test
    public void getFechaPublicacion_debeConvertirTimestampCorrectamente() {
        // Timestamp: 1 de enero 2024, 12:00:00
        Long timestamp = 1704110400000L;
        noticia.setFechaCreacion(timestamp);

        String fecha = noticia.getFechaPublicacion();
        assertNotNull("La fecha no debe ser null", fecha);
        assertTrue("La fecha debe contener el año 2024", fecha.contains("2024"));
    }

    @Test
    public void getParroquiaNombre_debeRetornarUbicacion() {
        String ubicacion = "Ibarra Centro";
        noticia.setUbicacion(ubicacion);

        assertEquals(ubicacion, noticia.getParroquiaNombre());
    }

    // ==================== TESTS DE VALIDACIONES ====================

    @Test
    public void noticia_debePermitirTituloVacio() {
        noticia.setTitulo("");
        assertEquals("", noticia.getTitulo());
    }

    @Test
    public void noticia_debePermitirTituloNull() {
        noticia.setTitulo(null);
        assertNull(noticia.getTitulo());
    }

    @Test
    public void noticia_debePermitirDescripcionLarga() {
        String descripcionLarga = "a".repeat(1000);
        noticia.setDescripcion(descripcionLarga);

        assertEquals(1000, noticia.getDescripcion().length());
    }

    @Test
    public void setDistancia_debeAceptarDistanciasPositivas() {
        noticia.setDistancia(5.5);
        assertEquals(Double.valueOf(5.5), noticia.getDistancia());

        noticia.setDistancia(0.0);
        assertEquals(Double.valueOf(0.0), noticia.getDistancia());
    }

    @Test
    public void setId_debeAceptarIdsPositivos() {
        noticia.setId(1);
        assertEquals(Integer.valueOf(1), noticia.getId());

        noticia.setId(999);
        assertEquals(Integer.valueOf(999), noticia.getId());
    }

    @Test
    public void setFirestoreId_debeAceptarIdAlfanumerico() {
        String firestoreId = "abc123XYZ";
        noticia.setFirestoreId(firestoreId);

        assertEquals(firestoreId, noticia.getFirestoreId());
    }

    @Test
    public void setAutor_debeAsignarCorrectamente() {
        noticia.setAutorId(1);
        noticia.setAutorNombre("Juan Pérez");

        assertEquals(Integer.valueOf(1), noticia.getAutorId());
        assertEquals("Juan Pérez", noticia.getAutorNombre());
    }

    @Test
    public void setImagenUrl_debeAceptarURLValida() {
        String url = "https://ejemplo.com/imagen.jpg";
        noticia.setImagenUrl(url);

        assertEquals(url, noticia.getImagenUrl());
    }

    @Test
    public void setFechaActualizacion_debeSerPosteriorAFechaCreacion() {
        Long fechaCreacion = 1704110400000L;
        Long fechaActualizacion = 1704196800000L; // 1 día después

        noticia.setFechaCreacion(fechaCreacion);
        noticia.setFechaActualizacion(fechaActualizacion);

        assertTrue("La fecha de actualización debe ser posterior",
                   noticia.getFechaActualizacion() >= noticia.getFechaCreacion());
    }

    // ==================== TESTS DEL MÉTODO toString() ====================

    @Test
    public void toString_debeContenerInformacionBasica() {
        noticia.setId(1);
        noticia.setTitulo("Test");
        noticia.setCategoriaId(1);
        noticia.setLatitud(0.34);
        noticia.setLongitud(-78.12);

        String resultado = noticia.toString();

        assertTrue("Debe contener el ID", resultado.contains("id=1"));
        assertTrue("Debe contener el título", resultado.contains("titulo='Test'"));
        assertTrue("Debe contener categoriaId", resultado.contains("categoriaId=1"));
    }

    @Test
    public void toString_noDebeLanzarExcepcion_cuandoCamposSonNull() {
        Noticia noticiaVacia = new Noticia();

        String resultado = noticiaVacia.toString();
        assertNotNull("toString no debe retornar null", resultado);
    }

    // ==================== TESTS DE CASOS EDGE ====================

    @Test
    public void coordenadas_debenAceptarValoresNegativos() {
        noticia.setLatitud(-90.0);
        noticia.setLongitud(-180.0);

        assertEquals(Double.valueOf(-90.0), noticia.getLatitud());
        assertEquals(Double.valueOf(-180.0), noticia.getLongitud());
    }

    @Test
    public void coordenadas_debenAceptarCero() {
        noticia.setLatitud(0.0);
        noticia.setLongitud(0.0);

        assertEquals(Double.valueOf(0.0), noticia.getLatitud());
        assertEquals(Double.valueOf(0.0), noticia.getLongitud());
    }

    @Test
    public void visualizaciones_debenAceptarCero() {
        noticia.setVisualizaciones(0);
        assertEquals(Integer.valueOf(0), noticia.getVisualizaciones());
    }

    @Test
    public void categoriaNombre_debeAsignarseCorrectamente() {
        noticia.setCategoriaId(1);
        noticia.setCategoriaNombre("Política");

        assertEquals("Política", noticia.getCategoriaNombre());
    }
}
