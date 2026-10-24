package com.drone.dominio.weather.chain;

import com.drone.dominio.weather.Weather;

/**
 * Handler concreto del patron Chain of Responsibility que valida
 * la temperatura como primera condicion de viabilidad de vuelo.
 *
 * <p><b>Regla de validacion:</b> La temperatura debe estar entre 0 C y 40 C (inclusive).
 * Temperaturas por debajo de 0 C o superiores a 40 C hacen que el vuelo
 * no sea viable para la operacion de drones.</p>
 *
 * <p><b>Posicion en la cadena:</b> Primer eslabon.</p>
 * <pre>
 * [TemperatureHandler] -&gt; HumidityHandler -&gt; WindHandler -&gt; PressureHandler -&gt; VisibilityHandler
 * </pre>
 *
 * <p>Si la temperatura es valida, delega la evaluacion al siguiente handler
 * mediante {@code checkNext()}. Si es invalida, la cadena se detiene
 * y retorna un {@link FlightEvaluation} de rechazo.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherHandler
 * @see FlightEvaluation
 */
public class TemperatureHandler extends WeatherHandler {

    /** Temperatura minima permitida en grados Celsius. */
    private static final double TEMP_MIN = 0.0;

    /** Temperatura maxima permitida en grados Celsius. */
    private static final double TEMP_MAX = 40.0;

    /**
     * Evalua si la temperatura actual es apta para la operacion de drones.
     *
     * <p>Si la temperatura esta fuera del rango [0 C, 40 C], la cadena se detiene
     * y se retorna un rechazo con el valor registrado y el rango permitido.
     * Si la temperatura es valida, se delega al siguiente handler.</p>
     *
     * @param weather datos meteorologicos actuales; se lee {@code weather.getTemperatura()}
     * @return {@link FlightEvaluation#noApto(String, String)} si la temperatura falla;
     *         resultado del siguiente handler si la temperatura es valida
     */
    @Override
    public FlightEvaluation evaluate(Weather weather) {
        double temp = weather.getTemperatura();
        if (temp < TEMP_MIN || temp > TEMP_MAX) {
            return FlightEvaluation.noApto(
                    "Temperatura (0 C a 40 C)",
                    String.format("Temperatura fuera de rango permitido [0 C a 40 C]. Registrada: %.1f C", temp)
            );
        }
        return checkNext(weather);
    }
}
