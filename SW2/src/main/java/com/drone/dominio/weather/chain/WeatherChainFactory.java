package com.drone.dominio.weather.chain;

/**
 * Fabrica estatica que ensambla la cadena de responsabilidad climatica
 * en el orden correcto de validacion para la viabilidad de vuelo de drones.
 *
 * <p>Centraliza la construccion de la cadena, garantizando que el orden
 * de los handlers sea siempre el definido por los requisitos del sistema.
 * Ninguna otra clase debe encadenar los handlers directamente.</p>
 *
 * <p><b>Orden de validacion ensamblado:</b></p>
 * <pre>
 * TemperatureHandler  (0 C a 40 C)
 *       |
 * HumidityHandler     (menor o igual a 85%)
 *       |
 * WindHandler         (menor o igual a 40 km/h)
 *       |
 * PressureHandler     (950 a 1050 hPa)
 *       |
 * VisibilityHandler   (mayor o igual a 5 km)
 *       |
 *      null           (fin de cadena = APROBADO)
 * </pre>
 *
 * <p>Cada llamada a {@link #crearCadena()} crea instancias nuevas e independientes
 * de todos los handlers, lo que permite reutilizar la fabrica sin efectos secundarios.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherHandler
 * @see TemperatureHandler
 * @see HumidityHandler
 * @see WindHandler
 * @see PressureHandler
 * @see VisibilityHandler
 */
public class WeatherChainFactory {

    /**
     * Crea y retorna el primer eslabon de la cadena de responsabilidad climatica
     * con todos los handlers ensamblados en el orden correcto.
     *
     * <p>El objeto retornado es el {@link TemperatureHandler}, que actua como
     * punto de entrada de la cadena. Los demas handlers estan encadenados
     * internamente y se invocan de forma automatica y secuencial.</p>
     *
     * <p><b>Uso tipico:</b></p>
     * <pre>
     * WeatherHandler cadena = WeatherChainFactory.crearCadena();
     * FlightEvaluation resultado = cadena.evaluate(weather);
     * </pre>
     *
     * @return primer handler de la cadena ({@link TemperatureHandler}),
     *         listo para recibir datos meteorologicos y evaluar la viabilidad de vuelo
     */
    public static WeatherHandler crearCadena() {
        WeatherHandler temp  = new TemperatureHandler();
        WeatherHandler hum   = new HumidityHandler();
        WeatherHandler wind  = new WindHandler();
        WeatherHandler press = new PressureHandler();
        WeatherHandler vis   = new VisibilityHandler();

        // Ensamblado de la cadena en orden estricto
        temp.setNext(hum)
            .setNext(wind)
            .setNext(press)
            .setNext(vis);

        return temp;
    }
}
