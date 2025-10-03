package com.example.applugueda.modelo;

public class Usuario {
    private String nombreCompleto;
    private String dni;
    private String correoElectronico;
    private String contraseña;
    private Tarjeta tarjeta;

    public Usuario(String nombreCompleto, String dni, String correoElectronico, String contraseña, Tarjeta tarjeta) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.correoElectronico = correoElectronico;
        this.contraseña = contraseña;
        this.tarjeta = tarjeta;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Tarjeta getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(Tarjeta tarjeta) {
        this.tarjeta = tarjeta;
    }
}
