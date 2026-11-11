package com.drone.dominio.strategy;

/**
 * Estrategia de consumo en Modo Ahorro.
 *
 * <p>Minimiza el gasto energetico operando a velocidad constante y baja aceleracion.
 * El impacto del peso es optimizado por el control de vuelo (+1.0% por cada kg).
 * Formula: distancia * 0.5 * (1 + (peso * 0.01)).</p>
 */
public class AhorroStrategy extends BaseConsumoStrategy {

    public AhorroStrategy() {
        super("Modo Ahorro (Base: 0.5x | Ajuste peso: +1.0%/kg)", 0.5, 0.01);
    }
}
