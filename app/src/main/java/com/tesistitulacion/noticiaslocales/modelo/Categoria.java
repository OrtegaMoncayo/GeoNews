package com.tesistitulacion.noticiaslocales.modelo;

/**
 * Modelo de datos para Categoria
 * Coincide con la tabla 'categorias' de la base de datos
 */
public class Categoria {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String icono;
    private String color;
    private Integer noticiasCount;
    private String createdAt;
    private String updatedAt;

    // Constructor vacío (necesario para Gson)
    public Categoria() {
    }

    // Constructor con campos principales
    public Categoria(String nombre, String descripcion, String color) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getNoticiasCount() {
        return noticiasCount;
    }

    public void setNoticiasCount(Integer noticiasCount) {
        this.noticiasCount = noticiasCount;
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

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", color='" + color + '\'' +
                ", noticiasCount=" + noticiasCount +
                '}';
    }
}
