package com.drone.dominio.strategy;

import java.io.Serializable;

/**
 * Modelo de dominio que representa un punto geografico con coordenadas
 * de latitud y longitud, ademas de un nombre descriptivo.
 */
public class PuntoGeografico implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private double latitud;
    private double longitud;

    public PuntoGeografico() {}

    public PuntoGeografico(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return String.format("%s (Lat: %.5f, Lon: %.5f)", nombre, latitud, longitud);
    }
}
