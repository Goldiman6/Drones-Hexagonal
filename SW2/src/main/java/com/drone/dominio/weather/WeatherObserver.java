package com.drone.dominio.weather;

/**
 * Interfaz del patron Observer (Observador) para el monitoreo climatico.
 *
 * <p>Define el contrato que deben implementar todos los objetos que deseen
 * recibir notificaciones automaticas cuando el {@link WeatherSubject}
 * obtenga nuevos datos meteorologicos desde la API externa.</p>
 *
 * <p><b>Rol en el patron Observer:</b> Observer (Observador concreto base).</p>
 *
 * <p>Los observadores se registran y eliminan dinamicamente en el
 * {@link WeatherSubject} durante la sesion, sin persistirse en base de datos.</p>
 *
 * <p>Implementaciones concretas en este proyecto:</p>
 * <ul>
 *   <li>{@code DroneWeatherObserver} - envuelve un Dron suscrito al monitoreo</li>
 * </ul>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherSubject
 * @see Weather
 */
public interface WeatherObserver {

    /**
     * Retorna el identificador unico del observador.
     *
     * <p>En el caso de {@code DroneWeatherObserver}, corresponde al ID del dron.
     * Se utiliza por el {@link WeatherSubject} para evitar suscripciones
     * duplicadas y para desuscribir por identificador.</p>
     *
     * @return identificador unico no nulo ni vacio del observador
     */
    String getIdentificador();

    /**
     * Metodo de notificacion invocado automaticamente por el {@link WeatherSubject}
     * cada vez que se obtienen nuevos datos meteorologicos desde la API.
     *
     * <p>El observador recibe los cinco aspectos climaticos encapsulados
     * en el objeto {@link Weather} y puede reaccionar segun su logica interna
     * (almacenar el ultimo clima recibido, mostrar alertas, etc.).</p>
     *
     * @param weather objeto con los datos meteorologicos actualizados;
     *                contiene temperatura, humedad, viento, presion y visibilidad
     */
    void actualizarClima(Weather weather);
}
