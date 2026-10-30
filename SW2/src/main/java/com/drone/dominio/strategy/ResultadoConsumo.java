package com.drone.dominio.strategy;

import com.drone.dominio.modelo.Drone;

/**
 * Objeto de resultado que encapsula la estimacion de consumo energetico
 * realizada por el caso de uso mediante el patron Strategy.
 */
public class ResultadoConsumo {

    private final Drone drone;
    private final Ruta ruta;
    private final double distanciaKm;
    private final String estrategiaNombre;
    private final double factorEstrategia;
    private final double porcentajeConsumo;
    private final boolean esViable;
    private final String mensaje;

    public ResultadoConsumo(Drone drone, Ruta ruta, double distanciaKm,
                            String estrategiaNombre, double factorEstrategia,
                            double porcentajeConsumo, boolean esViable, String mensaje) {
        this.drone = drone;
        this.ruta = ruta;
        this.distanciaKm = distanciaKm;
        this.estrategiaNombre = estrategiaNombre;
        this.factorEstrategia = factorEstrategia;
        this.porcentajeConsumo = porcentajeConsumo;
        this.esViable = esViable;
        this.mensaje = mensaje;
    }

    public Drone getDrone() {
        return drone;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public String getEstrategiaNombre() {
        return estrategiaNombre;
    }

    public double getFactorEstrategia() {
        return factorEstrategia;
    }

    public double getPorcentajeConsumo() {
        return porcentajeConsumo;
    }

    public boolean isEsViable() {
        return esViable;
    }

    public String getMensaje() {
        return mensaje;
    }
}
