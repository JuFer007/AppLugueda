package com.example.applugueda.modelo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tarjeta {
    private String numeroTarjeta;
    private String fechaVencimiento;
    private double saldoActual;
    private List<Movimiento> movimientos;

    public Tarjeta(String numeroTarjeta, String fechaVencimiento, double saldoActual) {
        this.numeroTarjeta = numeroTarjeta;
        this.fechaVencimiento = fechaVencimiento;
        this.saldoActual = saldoActual;
        this.movimientos = new ArrayList<>();
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public double getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(double saldoActual) {
        this.saldoActual = saldoActual;
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public void agregarMovimiento(String tipo, double monto, Date fecha) {
        if (movimientos == null) {
            movimientos = new ArrayList<>();
        }
        movimientos.add(new Movimiento(tipo, monto, fecha));
    }
}
