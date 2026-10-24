package com.drone.dominio.weather.chain;

import com.drone.dominio.weather.Weather;

/**
 * Handler concreto del patron Chain of Responsibility que valida
 * la humedad relativa como segunda condicion de viabilidad de vuelo.
 *
 * <p><b>Regla de validacion:</b> La humedad relativa debe ser menor o igual al 85%.
 * Una humedad superior al 85% puede afectar los componentes electronicos
 * de los drones y reduce la viabilidad operacional.</p>
 *
 * <p><b>Posicion en la cadena:</b> Segundo eslabon.</p>
 * <pre>
 * TemperatureHandler -&gt; [HumidityHandler] -&gt; WindHandler -&gt; PressureHandler -&gt; VisibilityHandler
 * </pre>
 *
 * <p>Solo se ejecuta si {@link TemperatureHandler} aprobo la temperatura.
 * Si la humedad es valida, delega al siguiente handler. Si falla,
 * la cadena se detiene y retorna un {@link FlightEvaluation} de rechazo.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherHandler
 * @see FlightEvaluation
 */
public class HumidityHandler extends WeatherHandler {

    /** Humedad relativa maxima permitida en porcentaje. */
    private static final double HUMEDAD_MAX = 85.0;

    /**
     * Evalua si la humedad relativa actual es apta para la operacion de drones.
     *
     * <p>Si la humedad supera el 85%, la cadena se detiene y se retorna
     * un rechazo con el valor registrado y el limite permitido.
     * Si la humedad es valida, se delega al siguiente handler.</p>
     *
     * @param weather datos meteorologicos actuales; se lee {@code weather.getHumedad()}
     * @return {@link FlightEvaluation#noApto(String, String)} si la humedad supera el limite;
     *         resultado del siguiente handler si la humedad es valida
     */
    @Override
    public FlightEvaluation evaluate(Weather weather) {
        double hum = weather.getHumedad();
        if (hum > HUMEDAD_MAX) {
            return FlightEvaluation.noApto(
                    "Humedad (menor o igual a 85%)",
                    String.format("Humedad excede el limite maximo permitido (85%%). Registrada: %.1f%%", hum)
            );
        }
        return checkNext(weather);
    }
}
