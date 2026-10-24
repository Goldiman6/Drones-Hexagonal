package com.drone.infraestructura.weather;

import com.drone.aplicacion.puerto.salida.WeatherApiClientPort;
import com.drone.dominio.weather.Weather;

public class OpenMeteoAdapter implements WeatherApiClientPort {

    private final GeoApiClient geoApiClient;
    private final OpenMeteoWeatherClient weatherClient;

    public OpenMeteoAdapter() {
        this.geoApiClient = new GeoApiClient();
        this.weatherClient = new OpenMeteoWeatherClient();
    }

    public OpenMeteoAdapter(GeoApiClient geoApiClient, OpenMeteoWeatherClient weatherClient) {
        this.geoApiClient = geoApiClient;
        this.weatherClient = weatherClient;
    }

    @Override
    public Weather obtenerClimaPorCiudad(String ciudad) throws Exception {
        // 1. Obtener latitud y longitud mediante Geocodificación
        GeoApiClient.Coordenadas coord = geoApiClient.obtenerCoordenadas(ciudad);

        // 2. Consultar clima actual con las coordenadas obtenidas
        String nombreDescriptivo = coord.pais.isEmpty() ? coord.nombre : (coord.nombre + ", " + coord.pais);
        return weatherClient.obtenerClima(coord.latitud, coord.longitud, nombreDescriptivo);
    }
}
