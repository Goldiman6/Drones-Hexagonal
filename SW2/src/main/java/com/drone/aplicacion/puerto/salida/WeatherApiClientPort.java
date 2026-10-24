package com.drone.aplicacion.puerto.salida;

import com.drone.dominio.weather.Weather;

/**
 * Puerto de salida (Outbound Port) de la capa de aplicacion para la consulta
 * de datos meteorologicos desde un servicio externo.
 *
 * <p>Define el contrato que debe cumplir cualquier adaptador de infraestructura
 * que provea informacion climatica. La capa de aplicacion (servicios) depende
 * de esta interfaz y no de ninguna implementacion concreta, lo que garantiza
 * la independencia tecnologica del nucleo del sistema.</p>
 *
 * <p><b>Rol en la Arquitectura Hexagonal:</b> Puerto de salida (Outbound Port).
 * Representa lo que la aplicacion necesita del mundo exterior relacionado con clima.</p>
 *
 * <p>Implementacion actual en infraestructura:</p>
 * <ul>
 *   <li>{@code OpenMeteoAdapter} - consulta la API de geocodificacion de Open-Meteo
 *       para obtener coordenadas y luego la API meteorologica para obtener
 *       los cinco aspectos climaticos.</li>
 * </ul>
 *
 * @author SW2
 * @version 1.0
 * @see Weather
 */
public interface WeatherApiClientPort {

    /**
     * Obtiene los datos meteorologicos actuales para la ciudad especificada.
     *
     * <p>La implementacion es responsable de:</p>
     * <ol>
     *   <li>Geocodificar el nombre de la ciudad para obtener latitud y longitud.</li>
     *   <li>Consultar la API meteorologica con las coordenadas obtenidas.</li>
     *   <li>Mapear la respuesta al modelo de dominio {@link Weather}.</li>
     * </ol>
     *
     * @param ciudad nombre de la ciudad a consultar (por ejemplo: "Bogota", "Brujas")
     * @return objeto {@link Weather} con los cinco aspectos meteorologicos:
     *         temperatura, humedad, velocidad del viento, presion atmosferica y visibilidad
     * @throws Exception si la geocodificacion falla, si la API no responde,
     *                   si la ciudad no es encontrada, o si ocurre un error de red
     */
    Weather obtenerClimaPorCiudad(String ciudad) throws Exception;
}
