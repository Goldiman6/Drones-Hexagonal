package com.drone.dominio.strategy;

/**
 * Estrategia de consumo en Modo Alto Rendimiento.
 *
 * <p>Aumenta la velocidad y empuje al maximo. En este modo la inercia del peso
 * castiga severamente la bateria (+2.0% adicional por cada kg de peso).
 * Formula: distancia * 1.5 * (1 + (peso * 0.02)).</p>
 */
public class AltoRendimientoStrategy implements ConsumoStrategy {

    private static final double FACTOR_BASE = 1.5;
    private static final double IMPACTO_PESO_POR_KG = 0.02; // +2.0% por kg

    @Override
    public String getNombre() {
        return "Modo Alto Rendimiento (Base: 1.5x | Ajuste peso: +2.0%/kg)";
    }

    @Override
    public double getFactor() {
        return FACTOR_BASE;
    }

    @Override
    public double calcularConsumo(double distanciaKm, double pesoKg) {
        double pesoSeguro = Math.max(0.0, pesoKg);
        double factorPeso = 1.0 + (pesoSeguro * IMPACTO_PESO_POR_KG);
        return distanciaKm * FACTOR_BASE * factorPeso;
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
