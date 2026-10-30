package com.drone.dominio.strategy;

/**
 * Estrategia de consumo en Modo Normal.
 *
 * <p>Consumo balanceado entre velocidad y autonomia.
 * El impacto del peso representa un desgaste estandar (+1.5% por cada kg).
 * Formula: distancia * 1.0 * (1 + (peso * 0.015)).</p>
 */
public class NormalStrategy implements ConsumoStrategy {

    private static final double FACTOR_BASE = 1.0;
    private static final double IMPACTO_PESO_POR_KG = 0.015; // +1.5% por kg

    @Override
    public String getNombre() {
        return "Modo Normal (Base: 1.0x | Ajuste peso: +1.5%/kg)";
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
