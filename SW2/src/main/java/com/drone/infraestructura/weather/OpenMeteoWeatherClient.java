package com.drone.infraestructura.weather;

import com.drone.dominio.weather.Weather;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

/**
 * Cliente HTTP que consume la API meteorologica de Open-Meteo para obtener
 * las condiciones climaticas actuales de una ubicacion geografica especifica.
 *
 * <p><b>API consumida:</b> {@code https://api.open-meteo.com/v1/forecast}</p>
 *
 * <p>Recibe las coordenadas geograficas (latitud y longitud) previamente
 * obtenidas por {@link GeoApiClient} y consulta los cinco aspectos climaticos
 * requeridos para la evaluacion de viabilidad de vuelo:</p>
 * <ul>
 *   <li>{@code temperature_2m} - Temperatura a 2 metros de altitud (grados Celsius)</li>
 *   <li>{@code relative_humidity_2m} - Humedad relativa a 2 metros (porcentaje)</li>
 *   <li>{@code wind_speed_10m} - Velocidad del viento a 10 metros (km/h)</li>
 *   <li>{@code surface_pressure} - Presion atmosferica en superficie (hPa)</li>
 *   <li>{@code visibility} - Visibilidad (metros, convertida a km internamente)</li>
 * </ul>
 *
 * <p><b>Conversion de unidades:</b> La API entrega la visibilidad en metros.
 * Este cliente la divide entre 1000 para convertirla a kilometros antes de
 * almacenarla en el modelo de dominio {@link Weather}.</p>
 *
 * <p><b>Rol en la Arquitectura Hexagonal:</b> Componente del adaptador de salida.
 * Pertenece a la capa de infraestructura.</p>
 *
 * @author SW2
 * @version 1.0
 * @see GeoApiClient
 * @see OpenMeteoAdapter
 * @see Weather
 */
public class OpenMeteoWeatherClient {

    /** Cliente HTTP de Java 11 con tiempo de conexion configurado. */
    private final HttpClient httpClient;

    /** Mapeador JSON para parsear las respuestas de la API. */
    private final ObjectMapper objectMapper;

    /**
     * Construye el cliente meteorologico con un timeout de conexion de 10 segundos.
     */
    public OpenMeteoWeatherClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Consulta la API meteorologica de Open-Meteo con las coordenadas dadas y retorna
     * los cinco aspectos climaticos actuales como un objeto de dominio {@link Weather}.
     *
     * <p>La URL consultada sigue el formato:</p>
     * <pre>
     * https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}
     *     &current=temperature_2m,relative_humidity_2m,wind_speed_10m,surface_pressure,visibility
     * </pre>
     *
     * <p><b>Precision numerica:</b> La latitud y longitud se formatean con
     * cinco decimales usando {@link Locale#US} para garantizar el uso del punto
     * decimal independientemente de la configuracion regional del sistema.</p>
     *
     * @param latitud      latitud geografica de la ciudad en grados decimales
     * @param longitud     longitud geografica de la ciudad en grados decimales
     * @param ciudadNombre nombre descriptivo de la ciudad (usado para el campo
     *                     {@link Weather#getCiudad()}, puede incluir el pais)
     * @return objeto {@link Weather} con los cinco aspectos meteorologicos actuales;
     *         la visibilidad ya esta convertida de metros a kilometros
     * @throws Exception si el codigo HTTP no es 200, si la respuesta no contiene
     *                   el nodo {@code current}, o si ocurre un error de red o de parseo JSON
     */
    public Weather obtenerClima(double latitud, double longitud, String ciudadNombre) throws Exception {
        String url = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.5f&longitude=%.5f"
                        + "&current=temperature_2m,relative_humidity_2m,wind_speed_10m,surface_pressure,visibility",
                latitud, longitud);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Error al consultar API meteorologica (HTTP "
                    + response.statusCode() + ").");
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode current = root.path("current");

        if (current.isMissingNode() || current.isNull()) {
            throw new Exception("La respuesta meteorologica no contiene el nodo de clima actual.");
        }

        double temperatura      = current.path("temperature_2m").asDouble();
        double humedad          = current.path("relative_humidity_2m").asDouble();
        double velocidadViento  = current.path("wind_speed_10m").asDouble();
        double presion          = current.path("surface_pressure").asDouble();

        // La API entrega la visibilidad en metros; se convierte a kilometros
        double visibilidadKm    = current.path("visibility").asDouble() / 1000.0;

        return new Weather(ciudadNombre, temperatura, humedad, velocidadViento, presion, visibilidadKm);
    }
}
