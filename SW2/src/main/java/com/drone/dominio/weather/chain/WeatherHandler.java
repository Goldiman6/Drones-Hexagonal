package com.drone.dominio.weather.chain;

import com.drone.dominio.weather.Weather;

/**
 * Clase abstracta base del patron Chain of Responsibility para la
 * validacion de condiciones climaticas de viabilidad de vuelo.
 *
 * <p>Cada subclase concreta representa un eslabon de la cadena y es responsable
 * de validar <b>una unica condicion meteorologica</b>. Si la condicion se cumple,
 * el handler delega la evaluacion al siguiente eslabon mediante {@link #checkNext(Weather)}.
 * Si la condicion falla, la cadena se detiene y se retorna un {@link FlightEvaluation}
 * de rechazo con el motivo especifico.</p>
 *
 * <p><b>Cadena configurada en este proyecto:</b></p>
 * <pre>
 * TemperatureHandler
 *       |
 * HumidityHandler
 *       |
 * WindHandler
 *       |
 * PressureHandler
 *       |
 * VisibilityHandler
 * </pre>
 *
 * <p>La cadena se ensambla en {@link WeatherChainFactory} y es instanciada
 * una vez por el {@code ConsultarClimaServicio}.</p>
 *
 * <p><b>Rol en el patron:</b> Handler abstracto (AbstractHandler).</p>
 *
 * @author SW2
 * @version 1.0
 * @see FlightEvaluation
 * @see WeatherChainFactory
 */
public abstract class WeatherHandler {

    /**
     * Referencia al siguiente handler en la cadena de responsabilidad.
     * Es {@code null} si este es el ultimo eslabon de la cadena.
     */
    protected WeatherHandler next;

    /**
     * Encadena el siguiente handler de validacion climatica.
     *
     * <p>Retorna el handler recibido para permitir el encadenamiento fluido
     * mediante llamadas consecutivas:</p>
     * <pre>
     * temp.setNext(hum).setNext(wind).setNext(press).setNext(vis);
     * </pre>
     *
     * @param next siguiente handler de la cadena; puede ser {@code null}
     *             si este sera el ultimo eslabon
     * @return el mismo handler recibido como parametro
     */
    public WeatherHandler setNext(WeatherHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Metodo abstracto de evaluacion que cada subclase debe implementar.
     *
     * <p>Cada handler debe:</p>
     * <ol>
     *   <li>Leer el parametro meteorologico de su responsabilidad desde {@code weather}.</li>
     *   <li>Verificar si cumple su regla de validacion.</li>
     *   <li>Si falla: retornar {@link FlightEvaluation#noApto(String, String)}.</li>
     *   <li>Si pasa: retornar {@link #checkNext(Weather)} para continuar la cadena.</li>
     * </ol>
     *
     * @param weather datos meteorologicos actuales con los cinco aspectos climaticos
     * @return resultado de la evaluacion de viabilidad de vuelo
     */
    public abstract FlightEvaluation evaluate(Weather weather);

    /**
     * Delega la evaluacion al siguiente handler de la cadena.
     *
     * <p>Si no hay mas handlers ({@code next == null}), significa que todas
     * las condiciones fueron validadas exitosamente y se retorna
     * {@link FlightEvaluation#apto()}.</p>
     *
     * @param weather datos meteorologicos a pasar al siguiente handler
     * @return resultado del siguiente handler, o aprobacion final si la cadena termino
     */
    protected FlightEvaluation checkNext(Weather weather) {
        if (next != null) {
            return next.evaluate(weather);
        }
        return FlightEvaluation.apto();
    }
}
