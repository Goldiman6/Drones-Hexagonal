package com.drone.dominio.strategy;

/**
 * Estrategia de consumo en Modo Alto Rendimiento.
 *
 * <p>Aumenta la velocidad y empuje al maximo. En este modo la inercia del peso
 * castiga severamente la bateria (+2.0% adicional por cada kg de peso).
 * Formula: distancia * 1.5 * (1 + (peso * 0.02)).</p>
 */
public class AltoRendimientoStrategy extends BaseConsumoStrategy {

    public AltoRendimientoStrategy() {
        super("Modo Alto Rendimiento (Base: 1.5x | Ajuste peso: +2.0%/kg)", 1.5, 0.02);
    }
}
