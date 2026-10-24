package com.drone.aplicacion.puerto.entrada;

import com.drone.aplicacion.weather.WeatherResultDTO;

/**
 * Puerto de entrada (Inbound Port) de la capa de aplicacion para la consulta
 * climatica y evaluacion de viabilidad de vuelo de drones.
 *
 * <p>Define el contrato que expone la aplicacion hacia los adaptadores de entrada
 * (por ejemplo, la interfaz grafica JavaFX) para iniciar el flujo completo de
 * consulta climatica. El adaptador de entrada ({@code DroneController}) depende
 * de esta interfaz, nunca de la implementacion concreta.</p>
 *
 * <p><b>Rol en la Arquitectura Hexagonal:</b> Puerto de entrada (Inbound Port /
 * Use Case Interface). Representa lo que el exterior puede pedirle a la aplicacion
 * en materia de consulta climatica.</p>
 *
 * <p><b>Flujo que desencadena este caso de uso:</b></p>
 * <ol>
 *   <li>Consulta la API de geocodificacion (latitud y longitud de la ciudad).</li>
 *   <li>Consulta la API meteorologica de Open-Meteo con las coordenadas.</li>
 *   <li>Evalua los cinco aspectos climaticos mediante la Chain of Responsibility.</li>
 *   <li>Notifica a todos los drones suscritos mediante el patron Observer.</li>
 *   <li>Retorna el resultado encapsulado en un {@link WeatherResultDTO}.</li>
 * </ol>
 *
 * <p>Implementacion en la capa de aplicacion: {@code ConsultarClimaServicio}.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherResultDTO
 */
public interface ConsultarClimaUseCase {

    /**
     * Ejecuta el caso de uso completo de consulta climatica y evaluacion de vuelo.
     *
     * <p>El flujo interno es: API Geocodificacion -> API Meteorologica ->
     * Chain of Responsibility -> Observer -> DTO de resultado.</p>
     *
     * @param ciudad nombre de la ciudad a consultar; debe ser "Bogota" o "Brujas"
     *               segun las opciones disponibles en la interfaz de usuario
     * @return {@link WeatherResultDTO} que contiene los datos climaticos obtenidos,
     *         el veredicto de viabilidad de vuelo ({@code FlightEvaluation}) y
     *         la lista de IDs de los drones suscritos que fueron notificados
     * @throws Exception si falla la geocodificacion, la consulta meteorologica,
     *                   o si ocurre algun error de red o de procesamiento
     */
    WeatherResultDTO ejecutar(String ciudad) throws Exception;
}
