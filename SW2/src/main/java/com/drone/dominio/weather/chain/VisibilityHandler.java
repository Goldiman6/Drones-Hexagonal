package com.drone.dominio.weather.chain;

import com.drone.dominio.weather.Weather;

/**
 * Handler concreto del patron Chain of Responsibility que valida
 * la visibilidad como quinta y ultima condicion de viabilidad de vuelo.
 *
 * <p><b>Regla de validacion:</b> La visibilidad debe ser mayor o igual a 5 km.
 * Una visibilidad inferior dificulta la operacion visual de los drones
 * y representa un riesgo para la seguridad aerea.</p>
 *
 * <p><b>Posicion en la cadena:</b> Quinto eslabon (ultimo).</p>
 * <pre>
 * TemperatureHandler -&gt; HumidityHandler -&gt; WindHandler -&gt; PressureHandler -&gt; [VisibilityHandler]
 * </pre>
 *
 * <p>Solo se ejecuta si los cuatro handlers anteriores aprobaron sus condiciones.
 * Si la visibilidad falla, la cadena retorna rechazo. Si pasa, como es el
 * ultimo eslabon ({@code next == null}), {@code checkNext()} retorna
 * automaticamente {@link FlightEvaluation#apto()}, indicando que el clima
 * es completamente apto para la operacion de drones.</p>
 *
 * <p><b>Conversion de unidades:</b> La API de Open-Meteo entrega la visibilidad
 * en metros. El adaptador {@code OpenMeteoWeatherClient} la convierte a kilometros
 * antes de almacenarla en el objeto {@link com.drone.dominio.weather.Weather},
 * por lo que este handler trabaja directamente en kilometros.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherHandler
 * @see FlightEvaluation
 */
public class VisibilityHandler extends WeatherHandler {

    /** Visibilidad minima requerida en kilometros. */
    private static final double VISIBILIDAD_MIN = 5.0;

    /**
     * Evalua si la visibilidad actual es apta para la operacion de drones.
     *
     * <p>Si la visibilidad es inferior a 5 km, la cadena se detiene y se retorna
     * un rechazo con el valor registrado y el minimo requerido.
     * Si la visibilidad es valida y no hay mas handlers, se retorna
     * {@link FlightEvaluation#apto()} indicando aprobacion total.</p>
     *
     * @param weather datos meteorologicos actuales; se lee {@code weather.getVisibilidad()}
     *                (valor ya convertido a kilometros por el adaptador de infraestructura)
     * @return {@link FlightEvaluation#noApto(String, String)} si la visibilidad es insuficiente;
     *         {@link FlightEvaluation#apto()} si todas las condiciones pasaron exitosamente
     */
    @Override
    public FlightEvaluation evaluate(Weather weather) {
        double vis = weather.getVisibilidad();
        if (vis < VISIBILIDAD_MIN) {
            return FlightEvaluation.noApto(
                    "Visibilidad (mayor o igual a 5 km)",
                    String.format("Visibilidad inferior al minimo requerido (5 km). Registrada: %.2f km", vis)
            );
        }
        return checkNext(weather);
    }
}
