package com.drone.dominio.strategy;

import java.io.Serializable;

/**
 * Representa una ruta de vuelo compuesta por un punto de origen
 * y un punto de destino geograficos.
 */
public class Ruta implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private PuntoGeografico origen;
    private PuntoGeografico destino;

    public Ruta() {}

    public Ruta(String id, String nombre, PuntoGeografico origen, PuntoGeografico destino) {
        this.id = id;
        this.nombre = nombre;
        this.origen = origen;
        this.destino = destino;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public PuntoGeografico getOrigen() {
        return origen;
    }

    public void setOrigen(PuntoGeografico origen) {
        this.origen = origen;
    }

    public PuntoGeografico getDestino() {
        return destino;
    }

    public void setDestino(PuntoGeografico destino) {
        this.destino = destino;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
