package com.tesistitulacion.noticiaslocales.modelo;

/**
 * Modelo de Parroquia - Representa las 12 parroquias de Ibarra
 * Compatible con tabla 'parroquias' de la base de datos
 *
 * Ibarra tiene:
 * - 5 Parroquias Urbanas: El Sagrario, San Francisco, La Dolorosa del Priorato, Caranqui, Alpachaca
 * - 7 Parroquias Rurales: La Esperanza, Angochagua, Ambuquí, Salinas, La Carolina, San Antonio, Lita
 */
public class Parroquia {
    private Integer id;
    private String nombre;
    private String tipo; // 'urbana' o 'rural'
    private Double latitud;
    private Double longitud;
    private String descripcion;
    private Integer poblacion;
    private Double superficieKm2;

    // Campos calculados
    private Integer noticiasCount; // Número de noticias en esta parroquia
    private Integer eventosCount; // Número de eventos en esta parroquia

    // Constructores
    public Parroquia() {
    }

    public Parroquia(Integer id, String nombre, String tipo, Double latitud, Double longitud) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(Integer poblacion) {
        this.poblacion = poblacion;
    }

    public Double getSuperficieKm2() {
        return superficieKm2;
    }

    public void setSuperficieKm2(Double superficieKm2) {
        this.superficieKm2 = superficieKm2;
    }

    public Integer getNoticiasCount() {
        return noticiasCount;
    }

    public void setNoticiasCount(Integer noticiasCount) {
        this.noticiasCount = noticiasCount;
    }

    public Integer getEventosCount() {
        return eventosCount;
    }

    public void setEventosCount(Integer eventosCount) {
        this.eventosCount = eventosCount;
    }

    // Métodos auxiliares
    /**
     * Verifica si la parroquia es urbana
     */
    public boolean esUrbana() {
        return "urbana".equalsIgnoreCase(tipo);
    }

    /**
     * Verifica si la parroquia es rural
     */
    public boolean esRural() {
        return "rural".equalsIgnoreCase(tipo);
    }

    /**
     * Obtiene el nombre con tipo
     * Ejemplo: "El Sagrario (Urbana)"
     */
    public String getNombreCompleto() {
        String tipoCapitalizado = tipo != null ?
            tipo.substring(0, 1).toUpperCase() + tipo.substring(1).toLowerCase() : "";
        return nombre + " (" + tipoCapitalizado + ")";
    }

    /**
     * Obtiene un resumen de la parroquia
     */
    public String getResumen() {
        StringBuilder sb = new StringBuilder(nombre);

        if (poblacion != null && poblacion > 0) {
            sb.append(" - ").append(String.format("%,d", poblacion)).append(" hab.");
        }

        if (superficieKm2 != null && superficieKm2 > 0) {
            sb.append(" - ").append(String.format("%.2f", superficieKm2)).append(" km²");
        }

        return sb.toString();
    }

    /**
     * Datos de las 12 parroquias de Ibarra (valores reales)
     */
    public static class ParroquiasIbarra {
        // Parroquias Urbanas
        public static final Parroquia EL_SAGRARIO = new Parroquia(
            1, "El Sagrario", "urbana", 0.3476, -78.1223
        );

        public static final Parroquia SAN_FRANCISCO = new Parroquia(
            2, "San Francisco", "urbana", 0.3490, -78.1290
        );

        public static final Parroquia LA_DOLOROSA = new Parroquia(
            3, "La Dolorosa del Priorato", "urbana", 0.3520, -78.1180
        );

        public static final Parroquia CARANQUI = new Parroquia(
            4, "Caranqui", "urbana", 0.3650, -78.1150
        );

        public static final Parroquia ALPACHACA = new Parroquia(
            5, "Alpachaca", "urbana", 0.3350, -78.1350
        );

        // Parroquias Rurales
        public static final Parroquia LA_ESPERANZA = new Parroquia(
            6, "La Esperanza", "rural", 0.2800, -78.1500
        );

        public static final Parroquia ANGOCHAGUA = new Parroquia(
            7, "Angochagua", "rural", 0.3900, -78.0900
        );

        public static final Parroquia AMBUQUI = new Parroquia(
            8, "Ambuquí", "rural", 0.4200, -78.1200
        );

        public static final Parroquia SALINAS = new Parroquia(
            9, "Salinas", "rural", 0.4800, -78.0800
        );

        public static final Parroquia LA_CAROLINA = new Parroquia(
            10, "La Carolina", "rural", 0.6800, -78.0500
        );

        public static final Parroquia SAN_ANTONIO = new Parroquia(
            11, "San Antonio", "rural", 0.3300, -78.2200
        );

        public static final Parroquia LITA = new Parroquia(
            12, "Lita", "rural", 0.8900, -78.4500
        );

        /**
         * Obtiene todas las parroquias en un array
         */
        public static Parroquia[] getTodasLasParroquias() {
            return new Parroquia[]{
                EL_SAGRARIO, SAN_FRANCISCO, LA_DOLOROSA, CARANQUI, ALPACHACA,
                LA_ESPERANZA, ANGOCHAGUA, AMBUQUI, SALINAS, LA_CAROLINA,
                SAN_ANTONIO, LITA
            };
        }

        /**
         * Obtiene solo parroquias urbanas
         */
        public static Parroquia[] getParroquiasUrbanas() {
            return new Parroquia[]{
                EL_SAGRARIO, SAN_FRANCISCO, LA_DOLOROSA, CARANQUI, ALPACHACA
            };
        }

        /**
         * Obtiene solo parroquias rurales
         */
        public static Parroquia[] getParroquiasRurales() {
            return new Parroquia[]{
                LA_ESPERANZA, ANGOCHAGUA, AMBUQUI, SALINAS, LA_CAROLINA,
                SAN_ANTONIO, LITA
            };
        }

        /**
         * Obtiene nombres de todas las parroquias para Spinner
         */
        public static String[] getNombresParroquias() {
            return new String[]{
                "Todas las parroquias",
                "El Sagrario", "San Francisco", "La Dolorosa del Priorato",
                "Caranqui", "Alpachaca", "La Esperanza", "Angochagua",
                "Ambuquí", "Salinas", "La Carolina", "San Antonio", "Lita"
            };
        }
    }

    @Override
    public String toString() {
        return "Parroquia{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }
}
