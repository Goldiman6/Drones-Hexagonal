package com.drone.infraestructura.util;

/**
 * Utilidad matematica para el calculo de distancias geograficas
 * mediante la formula de Haversine a partir de coordenadas de
 * latitud y longitud.
 */
public class HaversineUtil {

    /** Radio medio de la Tierra en kilometros. */
    private static final double RADIO_TIERRA_KM = 6371.0;

    /** Constructor privado para evitar instanciacion directa. */
    private HaversineUtil() {}

    /**
     * Calcula la distancia en kilometros entre dos puntos geograficos
     * dadas sus coordenadas de latitud y longitud en grados decimales.
     *
     * @param lat1 latitud del punto de origen en grados decimales
     * @param lon1 longitud del punto de origen en grados decimales
     * @param lat2 latitud del punto de destino en grados decimales
     * @param lon2 longitud del punto de destino en grados decimales
     * @return distancia aproximada sobre la superficie terrestre en kilometros
     */
    public static double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(radLat1) * Math.cos(radLat2)
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }
}
