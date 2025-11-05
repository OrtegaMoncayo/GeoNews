package com.tesistitulacion.noticiaslocales.modelo;

/**
 * Modelo de datos para Usuario
 * Coincide con la tabla 'usuarios' de la base de datos
 */
public class Usuario {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String password; // Solo para enviar, nunca se recibe del servidor
    private String fotoPerfil;
    private String bio;
    private String telefonocelular;
    private String ubicacion;
    private Long fechaRegistro;
    private Long ultimaConexion;
    private Integer noticiasPublicadas;
    private Boolean verificado;
    private String tipoUsuario; // 'usuario', 'reportero', 'admin'
    private String createdAt;

    // Constructor vacío (necesario para Gson)
    public Usuario() {
    }

    // Constructor para registro
    public Usuario(String nombre, String apellido, String email, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTelefonocelular() {
        return telefonocelular;
    }

    public void setTelefonocelular(String telefonocelular) {
        this.telefonocelular = telefonocelular;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Long getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Long fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Long getUltimaConexion() {
        return ultimaConexion;
    }

    public void setUltimaConexion(Long ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }

    public Integer getNoticiasPublicadas() {
        return noticiasPublicadas;
    }

    public void setNoticiasPublicadas(Integer noticiasPublicadas) {
        this.noticiasPublicadas = noticiasPublicadas;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obtiene el nombre completo del usuario
     */
    public String getNombreCompleto() {
        if (nombre != null && apellido != null) {
            return nombre + " " + apellido;
        } else if (nombre != null) {
            return nombre;
        } else if (apellido != null) {
            return apellido;
        }
        return email != null ? email : "Usuario";
    }

    /**
     * Verifica si el usuario es administrador
     */
    public boolean esAdmin() {
        return "admin".equalsIgnoreCase(tipoUsuario);
    }

    /**
     * Verifica si el usuario es reportero
     */
    public boolean esReportero() {
        return "reportero".equalsIgnoreCase(tipoUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                '}';
    }
}
