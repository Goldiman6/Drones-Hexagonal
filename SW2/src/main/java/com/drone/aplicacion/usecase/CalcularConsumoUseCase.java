package com.drone.aplicacion.usecase;

import com.drone.dominio.modelo.Drone;
import com.drone.dominio.strategy.ConsumoStrategy;
import com.drone.dominio.strategy.ResultadoConsumo;
import com.drone.dominio.strategy.Ruta;
import com.drone.infraestructura.util.HaversineUtil;

/**
 * Caso de uso y Contexto del patron Strategy para el calculo del consumo
 * de bateria estimado durante el recorrido de una ruta de vuelo.
 *
 * <p>Mantiene una referencia a la estrategia de consumo seleccionada
 * y coordina la traduccion de coordenadas geograficas a distancia (km)
 * mediante Haversine, delegando el calculo final a la estrategia considerando
 * tanto la distancia como el peso de carga del dron.</p>
 */
public class CalcularConsumoUseCase {

    private ConsumoStrategy strategy;

    public CalcularConsumoUseCase() {}

    public CalcularConsumoUseCase(ConsumoStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(ConsumoStrategy strategy) {
        this.strategy = strategy;
    }

    public ConsumoStrategy getStrategy() {
        return strategy;
    }

    /**
     * Ejecuta el calculo de consumo energetico para un dron, una ruta
     * y la estrategia de consumo especificada, ponderando el peso del vehiculo.
     *
     * @param drone    dron seleccionado para la mision (aporta el peso)
     * @param ruta     ruta con puntos de origen y destino
     * @param strategy estrategia de consumo seleccionada
     * @return {@link ResultadoConsumo} con el analisis de viabilidad
     * @throws Exception si faltan datos obligatorios
     */
    public ResultadoConsumo ejecutar(Drone drone, Ruta ruta, ConsumoStrategy strategy) throws Exception {
        if (drone == null) {
            throw new Exception("Debe seleccionar un dron para realizar el calculo.");
        }
        if (ruta == null || ruta.getOrigen() == null || ruta.getDestino() == null) {
            throw new Exception("La ruta seleccionada no contiene puntos de origen o destino validos.");
        }
        if (strategy == null) {
            throw new Exception("Debe seleccionar una estrategia de consumo energetico.");
        }

        this.strategy = strategy;

        // 1. Calcular distancia en kilometros mediante formula de Haversine
        double distanciaKm = HaversineUtil.calcularDistanciaKm(
                ruta.getOrigen().getLatitud(), ruta.getOrigen().getLongitud(),
                ruta.getDestino().getLatitud(), ruta.getDestino().getLongitud()
        );

        // 2. Aplicar la estrategia seleccionada considerando el peso del dron (Patron Strategy)
        double pesoDron = drone.getPeso();
        double consumoEstimado = strategy.calcularConsumo(distanciaKm, pesoDron);

        // 3. Evaluar si la bateria del dron permite completar la mision
        boolean esViable = consumoEstimado <= 100.0;
        String mensaje;

        if (esViable) {
            mensaje = String.format("Vuelo viable. Bateria restante estimada: %.1f%%.", 100.0 - consumoEstimado);
        } else {
            mensaje = String.format("Bateria insuficiente. El consumo (%.1f%%) excede la capacidad maxima.", consumoEstimado);
        }

        return new ResultadoConsumo(
                drone, ruta, distanciaKm, strategy.getNombre(),
                strategy.getFactor(), consumoEstimado, esViable, mensaje
        );
    }
}
