package com.drone.dominio.weather;

import java.io.Serializable;

/**
 * Modelo de dominio que representa los datos meteorologicos obtenidos
 * desde una API externa para una ciudad especifica.
 *
 * <p>Contiene los cinco aspectos climaticos requeridos para evaluar
 * la viabilidad de vuelo de los drones mediante el patron
 * Chain of Responsibility:</p>
 * <ul>
 *   <li>Temperatura (grados Celsius)</li>
 *   <li>Humedad relativa (porcentaje)</li>
 *   <li>Velocidad del viento (km/h)</li>
 *   <li>Presion atmosferica (hPa)</li>
 *   <li>Visibilidad (km)</li>
 * </ul>
 *
 * <p>Esta clase pertenece a la capa de Dominio y no depende
 * de ninguna tecnologia externa (sin imports de infraestructura).</p>
 *
 * @author SW2
 * @version 1.0
 */
public class Weather implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Nombre descriptivo de la ciudad consultada. */
    private String ciudad;

    /** Temperatura actual en grados Celsius. */
    private double temperatura;

    /** Humedad relativa actual en porcentaje (0-100). */
    private double humedad;

    /** Velocidad del viento actual en km/h. */
    private double velocidadViento;

    /** Presion atmosferica actual en hPa (hectopascales). */
    private double presionAtmosferica;

    /** Visibilidad actual en kilometros. */
    private double visibilidad;

    /**
     * Constructor por defecto requerido para la serializacion.
     */
    public Weather() {}

    /**
     * Constructor completo con todos los aspectos meteorologicos.
     *
     * @param ciudad            nombre de la ciudad consultada
     * @param temperatura       temperatura en grados Celsius
     * @param humedad           humedad relativa en porcentaje
     * @param velocidadViento   velocidad del viento en km/h
     * @param presionAtmosferica presion atmosferica en hPa
     * @param visibilidad       visibilidad en kilometros
     */
    public Weather(String ciudad, double temperatura, double humedad,
                   double velocidadViento, double presionAtmosferica, double visibilidad) {
        this.ciudad = ciudad;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.velocidadViento = velocidadViento;
        this.presionAtmosferica = presionAtmosferica;
        this.visibilidad = visibilidad;
    }

    /**
     * Retorna el nombre de la ciudad consultada.
     *
     * @return nombre de la ciudad
     */
    public String getCiudad() { return ciudad; }

    /**
     * Establece el nombre de la ciudad consultada.
     *
     * @param ciudad nombre de la ciudad
     */
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    /**
     * Retorna la temperatura actual en grados Celsius.
     *
     * @return temperatura en grados Celsius
     */
    public double getTemperatura() { return temperatura; }

    /**
     * Establece la temperatura en grados Celsius.
     *
     * @param temperatura temperatura en grados Celsius
     */
    public void setTemperatura(double temperatura) { this.temperatura = temperatura; }

    /**
     * Retorna la humedad relativa actual en porcentaje.
     *
     * @return humedad relativa (0-100)
     */
    public double getHumedad() { return humedad; }

    /**
     * Establece la humedad relativa en porcentaje.
     *
     * @param humedad humedad relativa (0-100)
     */
    public void setHumedad(double humedad) { this.humedad = humedad; }

    /**
     * Retorna la velocidad del viento actual en km/h.
     *
     * @return velocidad del viento en km/h
     */
    public double getVelocidadViento() { return velocidadViento; }

    /**
     * Establece la velocidad del viento en km/h.
     *
     * @param velocidadViento velocidad del viento en km/h
     */
    public void setVelocidadViento(double velocidadViento) { this.velocidadViento = velocidadViento; }

    /**
     * Retorna la presion atmosferica actual en hPa.
     *
     * @return presion atmosferica en hPa
     */
    public double getPresionAtmosferica() { return presionAtmosferica; }

    /**
     * Establece la presion atmosferica en hPa.
     *
     * @param presionAtmosferica presion atmosferica en hPa
     */
    public void setPresionAtmosferica(double presionAtmosferica) { this.presionAtmosferica = presionAtmosferica; }

    /**
     * Retorna la visibilidad actual en kilometros.
     *
     * @return visibilidad en kilometros
     */
    public double getVisibilidad() { return visibilidad; }

    /**
     * Establece la visibilidad en kilometros.
     *
     * @param visibilidad visibilidad en kilometros
     */
    public void setVisibilidad(double visibilidad) { this.visibilidad = visibilidad; }

    /**
     * Retorna una representacion legible de los datos meteorologicos.
     *
     * @return cadena con la ciudad y los cinco aspectos climaticos
     */
    @Override
    public String toString() {
        return String.format(
                "Clima en %s [Temp: %.1f C, Humedad: %.1f%%, Viento: %.1f km/h, Presion: %.1f hPa, Visibilidad: %.1f km]",
                ciudad, temperatura, humedad, velocidadViento, presionAtmosferica, visibilidad);
    }
}
