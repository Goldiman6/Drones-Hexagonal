package com.drone.dominio.weather.chain;

import com.drone.dominio.weather.Weather;

/**
 * Handler concreto del patron Chain of Responsibility que valida
 * la presion atmosferica como cuarta condicion de viabilidad de vuelo.
 *
 * <p><b>Regla de validacion:</b> La presion atmosferica debe estar entre 950 hPa y 1050 hPa.
 * Presiones fuera de este rango indican condiciones meteorologicas extremas
 * (tormentas severas o alturas criticas) que comprometen la operacion segura
 * de los drones.</p>
 *
 * <p><b>Posicion en la cadena:</b> Cuarto eslabon.</p>
 * <pre>
 * TemperatureHandler -&gt; HumidityHandler -&gt; WindHandler -&gt; [PressureHandler] -&gt; VisibilityHandler
 * </pre>
 *
 * <p>Solo se ejecuta si los tres handlers anteriores aprobaron sus condiciones.
 * Si la presion es valida, delega al siguiente handler. Si falla,
 * la cadena se detiene y retorna un {@link FlightEvaluation} de rechazo.</p>
 *
 * <p><b>Nota sobre Bogota:</b> La presion en Bogota (~757 hPa) esta por debajo del
 * limite inferior de 950 hPa, lo que refleja que la ciudad esta a alta altitud
 * (2600 m sobre el nivel del mar). Esto hara que la cadena rechace el vuelo
 * en Bogota por esta condicion.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherHandler
 * @see FlightEvaluation
 */
public class PressureHandler extends WeatherHandler {

    /** Presion atmosferica minima permitida en hPa. */
    private static final double PRESION_MIN = 950.0;

    /** Presion atmosferica maxima permitida en hPa. */
    private static final double PRESION_MAX = 1050.0;

    /**
     * Evalua si la presion atmosferica actual es apta para la operacion de drones.
     *
     * <p>Si la presion esta fuera del rango [950 hPa, 1050 hPa], la cadena se
     * detiene y se retorna un rechazo con el valor registrado y el rango permitido.
     * Si la presion es valida, se delega al siguiente handler.</p>
     *
     * @param weather datos meteorologicos actuales; se lee {@code weather.getPresionAtmosferica()}
     * @return {@link FlightEvaluation#noApto(String, String)} si la presion esta fuera de rango;
     *         resultado del siguiente handler si la presion es valida
     */
    @Override
    public FlightEvaluation evaluate(Weather weather) {
        double press = weather.getPresionAtmosferica();
        if (press < PRESION_MIN || press > PRESION_MAX) {
            return FlightEvaluation.noApto(
                    "Presion atmosferica (950 a 1050 hPa)",
                    String.format("Presion atmosferica fuera de rango permitido [950 a 1050 hPa]. Registrada: %.1f hPa", press)
            );
        }
        return checkNext(weather);
    }
}
