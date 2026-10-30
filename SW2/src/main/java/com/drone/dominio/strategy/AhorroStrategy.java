package com.drone.dominio.strategy;

/**
 * Estrategia de consumo en Modo Ahorro.
 *
 * <p>Minimiza el gasto energetico operando a velocidad constante y baja aceleracion.
 * El impacto del peso es optimizado por el control de vuelo (+1.0% por cada kg).
 * Formula: distancia * 0.5 * (1 + (peso * 0.01)).</p>
 */
public class AhorroStrategy implements ConsumoStrategy {

    private static final double FACTOR_BASE = 0.5;
    private static final double IMPACTO_PESO_POR_KG = 0.01; // +1.0% por kg

    @Override
    public String getNombre() {
        return "Modo Ahorro (Base: 0.5x | Ajuste peso: +1.0%/kg)";
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
