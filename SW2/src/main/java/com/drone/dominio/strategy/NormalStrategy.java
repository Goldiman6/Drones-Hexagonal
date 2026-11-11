package com.drone.dominio.strategy;

/**
 * Estrategia de consumo en Modo Normal.
 *
 * <p>Consumo balanceado entre velocidad y autonomia.
 * El impacto del peso representa un desgaste estandar (+1.5% por cada kg).
 * Formula: distancia * 1.0 * (1 + (peso * 0.015)).</p>
 */
public class NormalStrategy extends BaseConsumoStrategy {

    public NormalStrategy() {
        super("Modo Normal (Base: 1.0x | Ajuste peso: +1.5%/kg)", 1.0, 0.015);
    }
}
