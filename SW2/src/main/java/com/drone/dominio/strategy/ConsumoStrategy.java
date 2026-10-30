package com.drone.dominio.strategy;

/**
 * Interfaz del patron Strategy para el calculo del consumo de bateria
 * de un dron en funcion de la distancia de recorrido y el peso del vehiculo.
 */
public interface ConsumoStrategy {

    /**
     * Retorna el nombre descriptivo de la estrategia de consumo.
     *
     * @return nombre de la estrategia (por ejemplo: "Modo Ahorro")
     */
    String getNombre();

    /**
     * Retorna el factor base de consumo por kilometro.
     *
     * @return factor de consumo base (0.5, 1.0, 1.5)
     */
    double getFactor();

    /**
     * Calcula el porcentaje estimado de bateria consumido teniendo en cuenta
     * la distancia recorrida y el peso del dron en kilogramos.
     *
     * @param distanciaKm distancia recorrida en kilometros
     * @param pesoKg      peso total del dron en kilogramos
     * @return porcentaje de bateria estimado consumido (0% - 100%+)
     */
    double calcularConsumo(double distanciaKm, double pesoKg);
}
