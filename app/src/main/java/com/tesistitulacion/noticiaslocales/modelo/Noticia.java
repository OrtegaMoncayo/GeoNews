package com.tesistitulacion.noticiaslocales.modelo;

/**
 * Modelo de datos para Noticia
 * Coincide con la tabla 'noticias' de la base de datos
 */
public class Noticia {
    private Integer id;
    private String firestoreId; // ID del documento en Firestore
    private String titulo;
    private String descripcion;
    private String contenido;
    private String imagenUrl;
    private Integer autorId;
    private String autorNombre;
    private Integer categoriaId;
    private String categoriaNombre;
    private Long fechaCreacion;
    private Long fechaActualizacion;
    private Integer visualizaciones;
    private Double latitud;
    private Double longitud;
    private String ubicacion;
    private Boolean destacada;
    private String estado; // 'draft', 'published', 'archived'
    private String createdAt;
    private String updatedAt;
    private Double distancia; // Distancia en km (solo para búsqueda por radio)

    // Nuevos campos para contenido enriquecido
    private String citaDestacada; // Cita destacada / Quote
    private String hashtags; // Hashtags separados por comas
    private String impactoComunitario; // Sección de impacto comunitario

    // Constructor vacío (necesario para Gson)
    public Noticia() {
    }

    // Constructor con campos principales
    public Noticia(String titulo, String descripcion, String contenido, Integer categoriaId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.categoriaId = categoriaId;
    }

    // Getters y Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
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

    public Integer getAutorId() {
        return autorId;
    }

    public void setAutorId(Integer autorId) {
        this.autorId = autorId;
    }

    public String getAutorNombre() {
        return autorNombre;
    }

    public void setAutorNombre(String autorNombre) {
        this.autorNombre = autorNombre;
    }

    public Long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Long fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getVisualizaciones() {
        return visualizaciones;
    }

    public void setVisualizaciones(Integer visualizaciones) {
        this.visualizaciones = visualizaciones;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getDestacada() {
        return destacada;
    }

    public void setDestacada(Boolean destacada) {
        this.destacada = destacada;
    }

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getCitaDestacada() {
        return citaDestacada;
    }

    public void setCitaDestacada(String citaDestacada) {
        this.citaDestacada = citaDestacada;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getImpactoComunitario() {
        return impactoComunitario;
    }

    public void setImpactoComunitario(String impactoComunitario) {
        this.impactoComunitario = impactoComunitario;
    }

    /**
     * Método de compatibilidad para código antiguo que usa parroquiaNombre
     */
    public String getParroquiaNombre() {
        return ubicacion;
    }

    /**
     * Método de compatibilidad para código antiguo que usa fechaPublicacion
     */
    public String getFechaPublicacion() {
        if (fechaCreacion != null) {
            // Convertir timestamp a formato ISO
            return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .format(new java.util.Date(fechaCreacion));
        }
        return null;
    }

    // Método para obtener color de categoría
    public String getColorCategoria() {
        if (categoriaId == null) return "#1976D2";

        switch (categoriaId) {
            case 1: return "#FF6B35"; // Política
            case 2: return "#004E89"; // Economía
            case 3: return "#9B59B6"; // Cultura
            case 4: return "#27AE60"; // Deportes
            case 5: return "#F39C12"; // Educación
            case 6: return "#E74C3C"; // Salud
            case 7: return "#34495E"; // Seguridad
            case 8: return "#16A085"; // Medio Ambiente
            case 9: return "#8E44AD"; // Turismo
            case 10: return "#3498DB"; // Tecnología
            default: return "#1976D2";
        }
    }

    @Override
    public String toString() {
        return "Noticia{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", categoriaId=" + categoriaId +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }
}
