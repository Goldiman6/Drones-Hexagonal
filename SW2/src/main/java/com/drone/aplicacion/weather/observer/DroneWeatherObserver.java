package com.drone.aplicacion.weather.observer;

import com.drone.dominio.modelo.Drone;
import com.drone.dominio.weather.Weather;
import com.drone.dominio.weather.WeatherObserver;

/**
 * Observador concreto del patron Observer que envuelve una entidad {@link Drone}
 * para suscribirla al sistema de monitoreo climatico.
 *
 * <p>Cada instancia de esta clase representa un dron que ha sido suscrito
 * a las notificaciones climaticas. Cuando el {@code WeatherSubject} recibe
 * nuevos datos meteorologicos, invoca {@link #actualizarClima(Weather)}
 * en cada observador registrado.</p>
 *
 * <p><b>Rol en el patron Observer:</b> ConcreteObserver.</p>
 *
 * <p><b>Ciclo de vida:</b> Las instancias de esta clase existen solo en memoria
 * durante la sesion activa de la aplicacion. No se persisten en base de datos.
 * Al cerrar la aplicacion, todas las suscripciones se pierden.</p>
 *
 * <p>El ultimo clima recibido se almacena internamente en {@link #ultimoClima}
 * para su posterior consulta si fuera necesario.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherObserver
 * @see com.drone.dominio.weather.WeatherSubject
 */
public class DroneWeatherObserver implements WeatherObserver {

    /** Dron al que este observador representa dentro del sistema de monitoreo. */
    private final Drone drone;

    /** Ultimo objeto de datos climaticos recibido mediante notificacion del Subject. */
    private Weather ultimoClima;

    /**
     * Construye un observador para el dron especificado.
     *
     * @param drone entidad Dron a suscribir al monitoreo climatico; no debe ser nulo
     */
    public DroneWeatherObserver(Drone drone) {
        this.drone = drone;
    }

    /**
     * Retorna la entidad Dron asociada a este observador.
     *
     * @return el dron suscrito al monitoreo climatico
     */
    public Drone getDrone() {
        return drone;
    }

    /**
     * Retorna el identificador unico del observador.
     *
     * <p>Corresponde al ID del dron ({@link Drone#getId()}).
     * El {@code WeatherSubject} utiliza este valor para evitar
     * suscripciones duplicadas y para desuscribir por ID.</p>
     *
     * @return ID del dron, o cadena vacia si el dron o su ID son nulos
     */
    @Override
    public String getIdentificador() {
        return (drone != null && drone.getId() != null) ? drone.getId() : "";
    }

    /**
     * Recibe y almacena la notificacion con los nuevos datos meteorologicos.
     *
     * <p>Este metodo es invocado automaticamente por el {@code WeatherSubject}
     * cada vez que se obtiene una actualizacion climatica desde la API externa.
     * Registra el evento en consola e internamente guarda el ultimo clima recibido.</p>
     *
     * @param weather objeto con los cinco aspectos meteorologicos actualizados:
     *                temperatura, humedad, viento, presion y visibilidad
     */
    @Override
    public void actualizarClima(Weather weather) {
        this.ultimoClima = weather;
        System.out.println("[OBSERVER] Dron " + getIdentificador()
                + " (" + (drone != null ? drone.getModelo() : "N/A") + ")"
                + " notificado con nuevo clima: "
                + (weather != null ? weather.toString() : "sin datos"));
    }

    /**
     * Retorna el ultimo objeto de datos climaticos recibido por notificacion.
     *
     * <p>Sera {@code null} si este observador aun no ha recibido ninguna
     * notificacion desde que fue suscrito.</p>
     *
     * @return ultimo {@link Weather} recibido, o {@code null} si no hay datos aun
     */
    public Weather getUltimoClima() {
        return ultimoClima;
    }
}
