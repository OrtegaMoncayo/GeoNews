package com.tesistitulacion.noticiaslocales.modelo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Modelo de Evento - Representa eventos comunitarios
 * Compatible con tabla 'eventos' de la base de datos
 *
 * Funcionalidad: Sistema de eventos con notificaciones por email
 * Permite a usuarios registrar eventos comunitarios en Ibarra
 */
public class Evento {
    private Integer id;
    private String firestoreId; // ID del documento en Firestore
    private String descripcion;
    private Long fecha; // Timestamp en milisegundos o String ISO
    private String ubicacion;
    private Integer creadorId;
    private Integer parroquiaId;
    private Double latitud;
    private Double longitud;
    private String categoriaEvento; // 'cultural', 'deportivo', 'educativo', 'comunitario', 'otro'
    private Integer cupoMaximo;
    private Integer cupoActual;
    private Double costo;
    private String imagenUrl;
    private String contactoTelefono;
    private String contactoEmail;
    private String estado; // 'programado', 'en_curso', 'finalizado', 'cancelado'
    private Long fechaCreacion;

    // Campos adicionales (calculados)
    private String parroquiaNombre;
    private String creadorNombre;
    private Double distancia;

    // Constructores
    public Evento() {
    }

    public Evento(String descripcion, Long fecha, String ubicacion) {
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.fechaCreacion = System.currentTimeMillis();
        this.estado = "programado";
        this.cupoActual = 0;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(Integer creadorId) {
        this.creadorId = creadorId;
    }

    public Integer getParroquiaId() {
        return parroquiaId;
    }

    public void setParroquiaId(Integer parroquiaId) {
        this.parroquiaId = parroquiaId;
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

    public String getCategoriaEvento() {
        return categoriaEvento;
    }

    public void setCategoriaEvento(String categoriaEvento) {
        this.categoriaEvento = categoriaEvento;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Integer getCupoActual() {
        return cupoActual;
    }

    public void setCupoActual(Integer cupoActual) {
        this.cupoActual = cupoActual;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getContactoTelefono() {
        return contactoTelefono;
    }

    public void setContactoTelefono(String contactoTelefono) {
        this.contactoTelefono = contactoTelefono;
    }

    public String getContactoEmail() {
        return contactoEmail;
    }

    public void setContactoEmail(String contactoEmail) {
        this.contactoEmail = contactoEmail;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Campos calculados
    public String getParroquiaNombre() {
        return parroquiaNombre;
    }

    public void setParroquiaNombre(String parroquiaNombre) {
        this.parroquiaNombre = parroquiaNombre;
    }

    public String getCreadorNombre() {
        return creadorNombre;
    }

    public void setCreadorNombre(String creadorNombre) {
        this.creadorNombre = creadorNombre;
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

    // Métodos auxiliares
    /**
     * Verifica si hay cupos disponibles
     */
    public boolean tieneCuposDisponibles() {
        if (cupoMaximo == null || cupoActual == null) {
            return true; // Sin límite de cupos
        }
        return cupoActual < cupoMaximo;
    }

    /**
     * Obtiene cupos restantes
     */
    public Integer getCuposRestantes() {
        if (cupoMaximo == null || cupoActual == null) {
            return null; // Sin límite
        }
        return Math.max(0, cupoMaximo - cupoActual);
    }

    /**
     * Verifica si el evento ya pasó
     */
    public boolean yaOcurrio() {
        if (fecha == null) {
            return false;
        }
        return fecha < System.currentTimeMillis();
    }

    /**
     * Verifica si el evento es gratuito
     */
    public boolean esGratuito() {
        return costo == null || costo <= 0;
    }

    /**
     * Formatea la fecha del evento para mostrar
     */
    public String getFechaFormateada() {
        if (fecha == null) {
            return "Fecha por definir";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("es", "ES"));
        return sdf.format(new Date(fecha));
    }

    /**
     * Formatea el costo para mostrar
     */
    public String getCostoFormateado() {
        if (esGratuito()) {
            return "Gratis";
        }
        return String.format(Locale.getDefault(), "$%.2f", costo);
    }

    /**
     * Obtiene el nombre de la categoría en español
     */
    public String getCategoriaNombre() {
        if (categoriaEvento == null) {
            return "Otro";
        }

        switch (categoriaEvento.toLowerCase()) {
            case "cultural":
                return "Cultural";
            case "deportivo":
                return "Deportivo";
            case "educativo":
                return "Educativo";
            case "comunitario":
                return "Comunitario";
            default:
                return "Otro";
        }
    }

    /**
     * Obtiene el color asociado a la categoría
     */
    public String getColorCategoria() {
        if (categoriaEvento == null) {
            return "#9E9E9E"; // Gris
        }

        switch (categoriaEvento.toLowerCase()) {
            case "cultural":
                return "#E91E63"; // Rosa
            case "deportivo":
                return "#4CAF50"; // Verde
            case "educativo":
                return "#2196F3"; // Azul
            case "comunitario":
                return "#FF9800"; // Naranja
            default:
                return "#9E9E9E"; // Gris
        }
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", fecha=" + getFechaFormateada() +
                ", estado='" + estado + '\'' +
                '}';
    }
}
