package com.drone.aplicacion.servicio;

import com.drone.aplicacion.puerto.entrada.ConsultarClimaUseCase;
import com.drone.aplicacion.puerto.salida.WeatherApiClientPort;
import com.drone.aplicacion.weather.WeatherResultDTO;
import com.drone.dominio.weather.Weather;
import com.drone.dominio.weather.WeatherSubject;
import com.drone.dominio.weather.chain.FlightEvaluation;
import com.drone.dominio.weather.chain.WeatherChainFactory;
import com.drone.dominio.weather.chain.WeatherHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicacion que implementa el caso de uso {@link ConsultarClimaUseCase}.
 *
 * <p>Orquesta el flujo completo de consulta climatica y evaluacion de viabilidad
 * de vuelo, coordinando los tres componentes principales del sistema:</p>
 * <ol>
 *   <li><b>API Externa</b> (via {@link WeatherApiClientPort}): Consulta la informacion
 *       meteorologica actual de la ciudad solicitada en dos pasos:
 *       geocodificacion para obtener coordenadas y luego consulta climatica.</li>
 *   <li><b>Chain of Responsibility</b> (via {@link WeatherHandler}): Evalua los cinco
 *       aspectos climaticos en orden secuencial para determinar si el clima es
 *       apto para la operacion de drones.</li>
 *   <li><b>Patron Observer</b> (via {@link WeatherSubject}): Notifica automaticamente
 *       a todos los drones suscritos con los datos climaticos obtenidos.</li>
 * </ol>
 *
 * <p><b>Rol en la Arquitectura Hexagonal:</b> Implementacion del puerto de entrada
 * {@link ConsultarClimaUseCase}. Vive en la capa de aplicacion y solo conoce
 * interfaces (puertos), nunca clases de infraestructura directamente.</p>
 *
 * <p>La cadena de responsabilidad se instancia una sola vez en el constructor
 * mediante {@link WeatherChainFactory#crearCadena()} y se reutiliza en cada
 * llamada a {@link #ejecutar(String)}.</p>
 *
 * @author SW2
 * @version 1.0
 * @see ConsultarClimaUseCase
 * @see WeatherApiClientPort
 * @see WeatherSubject
 * @see WeatherChainFactory
 */
public class ConsultarClimaServicio implements ConsultarClimaUseCase {

    /** Puerto de salida para obtener datos meteorologicos desde la API externa. */
    private final WeatherApiClientPort weatherApiClient;

    /** Sujeto del patron Observer que gestiona los drones suscritos al monitoreo climatico. */
    private final WeatherSubject weatherSubject;

    /**
     * Primer eslabon de la cadena de responsabilidad para la validacion climatica.
     * Ensamblado en el orden: Temperatura -> Humedad -> Viento -> Presion -> Visibilidad.
     */
    private final WeatherHandler chain;

    /**
     * Construye el servicio de consulta climatica con sus dependencias inyectadas.
     *
     * <p>La cadena de responsabilidad se ensambla una unica vez en el constructor
     * y se reutiliza en cada invocacion de {@link #ejecutar(String)}.</p>
     *
     * @param weatherApiClient adaptador de salida que implementa la consulta a la API externa;
     *                         no debe ser nulo
     * @param weatherSubject   sujeto del patron Observer con la lista de drones suscritos;
     *                         no debe ser nulo
     */
    public ConsultarClimaServicio(WeatherApiClientPort weatherApiClient, WeatherSubject weatherSubject) {
        this.weatherApiClient = weatherApiClient;
        this.weatherSubject = weatherSubject;
        this.chain = WeatherChainFactory.crearCadena();
    }

    /**
     * Ejecuta el caso de uso completo de consulta climatica y evaluacion de viabilidad de vuelo.
     *
     * <p><b>Flujo de ejecucion:</b></p>
     * <ol>
     *   <li>Llama a {@code weatherApiClient.obtenerClimaPorCiudad(ciudad)} para obtener
     *       los cinco aspectos meteorologicos actuales desde las APIs de Open-Meteo.</li>
     *   <li>Evalua los datos con la cadena de responsabilidad para determinar si
     *       el clima es apto para volar.</li>
     *   <li>Notifica a todos los drones suscritos mediante {@code weatherSubject.notificar()}.</li>
     *   <li>Empaqueta y retorna los tres resultados en un {@link WeatherResultDTO}.</li>
     * </ol>
     *
     * @param ciudad nombre de la ciudad a consultar (por ejemplo: "Bogota", "Brujas")
     * @return {@link WeatherResultDTO} con el clima obtenido, el veredicto de viabilidad
     *         y la lista de IDs de drones notificados
     * @throws Exception si falla la conexion a la API, si la ciudad no es encontrada,
     *                   o si ocurre cualquier error durante el procesamiento
     */
    @Override
    public WeatherResultDTO ejecutar(String ciudad) throws Exception {
        // Paso 1: Consultar la API externa (Geocodificacion + Clima actual)
        Weather weather = weatherApiClient.obtenerClimaPorCiudad(ciudad);

        // Paso 2: Evaluar viabilidad de vuelo con la Chain of Responsibility
        FlightEvaluation evaluation = chain.evaluate(weather);

        // Paso 3: Notificar a todos los drones suscritos (Patron Observer)
        weatherSubject.notificar(weather);
        List<String> dronesNotificados = new ArrayList<>(weatherSubject.getIdentificadoresSuscritos());

        // Paso 4: Retornar el resultado encapsulado
        return new WeatherResultDTO(weather, evaluation, dronesNotificados);
    }
}
