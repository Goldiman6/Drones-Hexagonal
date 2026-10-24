package com.drone.infraestructura.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Cliente HTTP que consume la API de geocodificacion de Open-Meteo para
 * convertir nombres de ciudades en coordenadas geograficas (latitud y longitud).
 *
 * <p><b>API consumida:</b> {@code https://geocoding-api.open-meteo.com/v1/search}</p>
 *
 * <p>Esta clase es un cliente de infraestructura: se comunica con el mundo exterior
 * mediante HTTP y mapea la respuesta JSON al tipo interno {@link Coordenadas}.
 * Es utilizada por {@link OpenMeteoAdapter} como primer paso antes de consultar
 * la API meteorologica.</p>
 *
 * <p><b>Rol en la Arquitectura Hexagonal:</b> Componente del adaptador de salida.
 * Pertenece a la capa de infraestructura.</p>
 *
 * @author SW2
 * @version 1.0
 * @see OpenMeteoAdapter
 * @see OpenMeteoWeatherClient
 */
public class GeoApiClient {

    /** Cliente HTTP de Java 11 con tiempo de conexion configurado. */
    private final HttpClient httpClient;

    /** Mapeador JSON para parsear las respuestas de la API. */
    private final ObjectMapper objectMapper;

    /**
     * Construye el cliente de geocodificacion con un timeout de conexion de 10 segundos.
     */
    public GeoApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Representa las coordenadas geograficas y datos basicos de una ciudad
     * obtenidos desde la API de geocodificacion de Open-Meteo.
     */
    public static class Coordenadas {

        /** Latitud geografica en grados decimales. */
        public final double latitud;

        /** Longitud geografica en grados decimales. */
        public final double longitud;

        /** Nombre de la ciudad tal como lo devuelve la API. */
        public final String nombre;

        /** Pais al que pertenece la ciudad. */
        public final String pais;

        /**
         * Construye un objeto de coordenadas con todos sus campos.
         *
         * @param latitud  latitud en grados decimales
         * @param longitud longitud en grados decimales
         * @param nombre   nombre de la ciudad
         * @param pais     pais de la ciudad
         */
        public Coordenadas(double latitud, double longitud, String nombre, String pais) {
            this.latitud = latitud;
            this.longitud = longitud;
            this.nombre = nombre;
            this.pais = pais;
        }
    }

    /**
     * Consulta la API de geocodificacion de Open-Meteo para obtener las coordenadas
     * geograficas del primer resultado que coincida con el nombre de la ciudad.
     *
     * <p>La URL consultada sigue el formato:
     * {@code https://geocoding-api.open-meteo.com/v1/search?name={ciudad}&count=1&language=es}</p>
     *
     * <p>El parametro {@code language=es} garantiza que los nombres de ciudades
     * y paises sean devueltos en espanol (por ejemplo, "Belgica" en lugar de "Belgium").</p>
     *
     * <p><b>Nota sobre Brujas:</b> Con {@code language=es}, el primer resultado
     * para "Brujas" corresponde a Brujas, Belgica (lat: 51.20892, lon: 3.22424),
     * que es la ciudad correcta segun el requerimiento del sistema.</p>
     *
     * @param ciudad nombre de la ciudad a geocodificar; no debe ser nulo ni vacio
     * @return objeto {@link Coordenadas} con latitud, longitud, nombre y pais del primer resultado
     * @throws Exception si el codigo HTTP no es 200, si la ciudad no fue encontrada
     *                   en la API, o si ocurre un error de red o de parseo JSON
     */
    public Coordenadas obtenerCoordenadas(String ciudad) throws Exception {
        String ciudadEncoded = URLEncoder.encode(ciudad.trim(), StandardCharsets.UTF_8);
        String url = "https://geocoding-api.open-meteo.com/v1/search?name="
                + ciudadEncoded + "&count=1&language=es";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Error al consultar API de Geocodificacion (HTTP "
                    + response.statusCode() + ").");
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode results = root.path("results");

        if (!results.isArray() || results.isEmpty()) {
            throw new Exception("No se encontraron coordenadas para la ciudad: " + ciudad);
        }

        JsonNode primerResultado = results.get(0);
        double lat    = primerResultado.path("latitude").asDouble();
        double lon    = primerResultado.path("longitude").asDouble();
        String nombre = primerResultado.path("name").asText(ciudad);
        String pais   = primerResultado.path("country").asText("");

        return new Coordenadas(lat, lon, nombre, pais);
    }
}
