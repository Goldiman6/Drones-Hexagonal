package com.drone.aplicacion.weather;

import com.drone.dominio.weather.Weather;
import com.drone.dominio.weather.chain.FlightEvaluation;
import java.util.Collections;
import java.util.List;

/**
 * Objeto de transferencia de datos (DTO) que encapsula el resultado completo
 * de la ejecucion del caso de uso {@code ConsultarClimaUseCase}.
 *
 * <p>Agrupa en un unico objeto los tres componentes del resultado generado
 * por el servicio de consulta climatica:</p>
 * <ul>
 *   <li>Los datos meteorologicos obtenidos de la API externa.</li>
 *   <li>El veredicto de viabilidad de vuelo producido por la cadena de responsabilidad.</li>
 *   <li>La lista de identificadores de los drones notificados mediante el patron Observer.</li>
 * </ul>
 *
 * <p>Esta clase es inmutable: todos sus campos se asignan en el constructor
 * y no existen setters. La lista de drones notificados se retorna
 * como una referencia directa a la lista original (ya inmutable por diseno del servicio).</p>
 *
 * <p><b>Rol en la arquitectura:</b> DTO de la capa de aplicacion.
 * Viaja desde el servicio hasta el adaptador de entrada (Vista) sin
 * exponer logica de negocio.</p>
 *
 * @author SW2
 * @version 1.0
 * @see Weather
 * @see FlightEvaluation
 */
public class WeatherResultDTO {

    /** Datos meteorologicos obtenidos de la API externa para la ciudad consultada. */
    private final Weather weather;

    /** Resultado de la evaluacion de viabilidad de vuelo producido por la cadena de handlers. */
    private final FlightEvaluation evaluation;

    /**
     * Lista de identificadores (IDs) de los drones que fueron notificados
     * mediante el patron Observer durante esta consulta.
     */
    private final List<String> dronesNotificados;

    /**
     * Construye el DTO con los tres componentes del resultado de consulta climatica.
     *
     * @param weather            datos meteorologicos de la ciudad consultada; no debe ser nulo
     * @param evaluation         veredicto de viabilidad de vuelo; no debe ser nulo
     * @param dronesNotificados  lista de IDs de drones suscritos notificados;
     *                           si es {@code null}, se almacena una lista vacia
     */
    public WeatherResultDTO(Weather weather, FlightEvaluation evaluation, List<String> dronesNotificados) {
        this.weather = weather;
        this.evaluation = evaluation;
        this.dronesNotificados = dronesNotificados != null ? dronesNotificados : Collections.emptyList();
    }

    /**
     * Retorna los datos meteorologicos obtenidos de la API para la ciudad consultada.
     *
     * <p>Incluye los cinco aspectos climaticos: temperatura, humedad, velocidad del
     * viento, presion atmosferica y visibilidad.</p>
     *
     * @return objeto {@link Weather} con los datos climaticos actuales
     */
    public Weather getWeather() {
        return weather;
    }

    /**
     * Retorna el veredicto de viabilidad de vuelo producido por la cadena de responsabilidad.
     *
     * <p>Indica si el clima es apto o no apto para operar drones, y en caso de
     * rechazo, cual fue la regla climatica incumplida y el valor registrado.</p>
     *
     * @return resultado de la evaluacion climatica con mensaje y regla incumplida (si aplica)
     */
    public FlightEvaluation getEvaluation() {
        return evaluation;
    }

    /**
     * Retorna la lista de identificadores de los drones que fueron notificados
     * automaticamente mediante el patron Observer durante esta consulta.
     *
     * <p>La lista contiene los IDs de todos los drones que estaban suscritos
     * al {@code WeatherSubject} en el momento de la consulta.
     * Si no habia drones suscritos, la lista estara vacia.</p>
     *
     * @return lista (posiblemente vacia) de IDs de drones notificados
     */
    public List<String> getDronesNotificados() {
        return dronesNotificados;
    }
}
