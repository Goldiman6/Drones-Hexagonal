package com.drone.dominio.weather.chain;

import com.drone.dominio.weather.Weather;

/**
 * Handler concreto del patron Chain of Responsibility que valida
 * la velocidad del viento como tercera condicion de viabilidad de vuelo.
 *
 * <p><b>Regla de validacion:</b> La velocidad del viento debe ser menor o igual a 40 km/h.
 * Vientos superiores a este limite comprometen la estabilidad y el control
 * de los drones durante la operacion.</p>
 *
 * <p><b>Posicion en la cadena:</b> Tercer eslabon.</p>
 * <pre>
 * TemperatureHandler -&gt; HumidityHandler -&gt; [WindHandler] -&gt; PressureHandler -&gt; VisibilityHandler
 * </pre>
 *
 * <p>Solo se ejecuta si los handlers anteriores aprobaron temperatura y humedad.
 * Si el viento es valido, delega al siguiente handler. Si falla,
 * la cadena se detiene y retorna un {@link FlightEvaluation} de rechazo.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherHandler
 * @see FlightEvaluation
 */
public class WindHandler extends WeatherHandler {

    /** Velocidad maxima del viento permitida en km/h. */
    private static final double VIENTO_MAX = 40.0;

    /**
     * Evalua si la velocidad del viento actual es apta para la operacion de drones.
     *
     * <p>Si el viento supera los 40 km/h, la cadena se detiene y se retorna
     * un rechazo con el valor registrado y el limite permitido.
     * Si el viento es valido, se delega al siguiente handler.</p>
     *
     * @param weather datos meteorologicos actuales; se lee {@code weather.getVelocidadViento()}
     * @return {@link FlightEvaluation#noApto(String, String)} si el viento supera el limite;
     *         resultado del siguiente handler si el viento es valido
     */
    @Override
    public FlightEvaluation evaluate(Weather weather) {
        double wind = weather.getVelocidadViento();
        if (wind > VIENTO_MAX) {
            return FlightEvaluation.noApto(
                    "Velocidad del viento (menor o igual a 40 km/h)",
                    String.format("Velocidad del viento excede el limite permitido (40 km/h). Registrada: %.1f km/h", wind)
            );
        }
        return checkNext(weather);
    }
}
