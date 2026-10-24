package com.drone.dominio.weather.chain;

/**
 * Representa el resultado de la evaluacion de viabilidad de vuelo
 * producida por la cadena de responsabilidad climatica ({@code WeatherHandler}).
 *
 * <p>Encapsula tres datos clave:</p>
 * <ul>
 *   <li>Si el clima es apto o no para operar drones</li>
 *   <li>El mensaje descriptivo del veredicto final</li>
 *   <li>El nombre de la regla incumplida (solo si no es apto)</li>
 * </ul>
 *
 * <p>Se construye mediante los metodos de fabrica estaticos {@link #apto()}
 * y {@link #noApto(String, String)} para garantizar inmutabilidad y consistencia.</p>
 *
 * <p><b>Rol en la arquitectura:</b> Objeto de resultado de dominio;
 * no depende de ninguna tecnologia externa.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherHandler
 */
public class FlightEvaluation {

    /** Indica si el clima es apto para la operacion de drones. */
    private final boolean apto;

    /** Mensaje descriptivo del resultado de la evaluacion climatica. */
    private final String mensaje;

    /**
     * Nombre de la regla climatica incumplida.
     * Es {@code null} cuando el veredicto es apto.
     */
    private final String reglaIncumplida;

    /**
     * Constructor privado. Usar los metodos de fabrica {@link #apto()} o
     * {@link #noApto(String, String)} para instanciar.
     *
     * @param apto            {@code true} si el clima permite el vuelo
     * @param mensaje         mensaje final del veredicto
     * @param reglaIncumplida nombre de la regla que fallo, o {@code null} si todo paso
     */
    private FlightEvaluation(boolean apto, String mensaje, String reglaIncumplida) {
        this.apto = apto;
        this.mensaje = mensaje;
        this.reglaIncumplida = reglaIncumplida;
    }

    /**
     * Crea un resultado de evaluacion APROBADO.
     *
     * <p>Indica que todos los handlers de la cadena validaron sus condiciones
     * correctamente y el clima es apto para operar drones.</p>
     *
     * @return instancia de {@code FlightEvaluation} con veredicto positivo
     */
    public static FlightEvaluation apto() {
        return new FlightEvaluation(true, "CLIMA APTO PARA OPERACION DE DRONES", null);
    }

    /**
     * Crea un resultado de evaluacion RECHAZADO con el motivo especifico del fallo.
     *
     * <p>La cadena se detiene en el primer handler que incumple su condicion
     * y crea este resultado con el detalle exacto de la regla violada.</p>
     *
     * @param reglaIncumplida nombre descriptivo de la regla que fallo
     *                        (por ejemplo: "Temperatura (0 C a 40 C)")
     * @param motivo          descripcion detallada del incumplimiento, incluyendo
     *                        el valor registrado vs el limite permitido
     * @return instancia de {@code FlightEvaluation} con veredicto negativo
     */
    public static FlightEvaluation noApto(String reglaIncumplida, String motivo) {
        return new FlightEvaluation(
                false,
                "CLIMA NO APTO PARA OPERACION DE DRONES: " + motivo,
                reglaIncumplida);
    }

    /**
     * Indica si el veredicto de la evaluacion es positivo (apto para volar).
     *
     * @return {@code true} si todos los handlers aprobaron; {@code false} si alguno fallo
     */
    public boolean isApto() {
        return apto;
    }

    /**
     * Retorna el mensaje completo del veredicto de la evaluacion.
     *
     * <p>Si es apto: "CLIMA APTO PARA OPERACION DE DRONES".<br>
     * Si no es apto: "CLIMA NO APTO PARA OPERACION DE DRONES: [motivo detallado]".</p>
     *
     * @return mensaje descriptivo del resultado de la evaluacion
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Retorna el nombre de la regla climatica que causo el rechazo.
     *
     * <p>Solo tiene valor cuando {@link #isApto()} retorna {@code false}.
     * En caso de aprobacion, este campo es {@code null}.</p>
     *
     * @return nombre de la regla incumplida, o {@code null} si el vuelo es viable
     */
    public String getReglaIncumplida() {
        return reglaIncumplida;
    }
}
