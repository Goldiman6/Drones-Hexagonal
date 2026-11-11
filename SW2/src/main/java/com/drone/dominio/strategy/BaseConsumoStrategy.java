package com.drone.dominio.strategy;

/**
 * Clase base abstracta para las estrategias de consumo de bateria.
 *
 * <p>Centraliza la formula de calculo y los atributos comunes (factor base e
 * impacto de peso por kilogramo) para evitar duplicacion de codigo cumpliendo
 * con los principios SOLID (DRY y Open/Closed).</p>
 */
public abstract class BaseConsumoStrategy implements ConsumoStrategy {

    private final String nombre;
    private final double factorBase;
    private final double impactoPesoPorKg;

    protected BaseConsumoStrategy(String nombre, double factorBase, double impactoPesoPorKg) {
        this.nombre = nombre;
        this.factorBase = factorBase;
        this.impactoPesoPorKg = impactoPesoPorKg;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public double getFactor() {
        return factorBase;
    }

    @Override
    public double calcularConsumo(double distanciaKm, double pesoKg) {
        double pesoSeguro = Math.max(0.0, pesoKg);
        double factorPeso = 1.0 + (pesoSeguro * impactoPesoPorKg);
        return distanciaKm * factorBase * factorPeso;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
