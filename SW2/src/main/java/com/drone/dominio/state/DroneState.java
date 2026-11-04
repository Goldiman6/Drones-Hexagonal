package com.drone.dominio.state;

/**
 * Interfaz del patron State que define el contrato de comportamiento
 * para cada estado operativo de un dron.
 *
 * <p>Recibe como parametro el contexto ({@link DroneWrapper}) en cada una de sus
 * operaciones de actividad, permitiendo que cada estado concreto modifique
 * directamente el estado interno del contexto o valide sus condiciones de negocio,
 * sin recurrir a estructuras condicionales (if-else / switch) en el contexto.</p>
 *
 * <p>El {@link DroneWrapper} actua como Contexto del patron: encapsula al
 * {@code Drone} de dominio y mantiene la referencia al estado actual en memoria.</p>
 *
 * <p>Ciclo de estados permitido:</p>
 * <pre>
 *   APAGADO → EN_ESPERA → VOLANDO → ATERRIZANDO → EN_ESPERA
 *   EN_ESPERA → APAGADO
 * </pre>
 */
public interface DroneState {

    /**
     * Retorna el nombre descriptivo del estado actual.
     *
     * @return nombre del estado ("APAGADO", "EN_ESPERA", "VOLANDO", "ATERRIZANDO")
     */
    String getNombre();

    /**
     * Ejecuta la actividad de encendido sobre el contexto del dron.
     * Transicion valida unicamente desde APAGADO → EN_ESPERA.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    void encender(DroneWrapper context);

    /**
     * Ejecuta la actividad de apagado sobre el contexto del dron.
     * Transicion valida unicamente desde EN_ESPERA → APAGADO.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    void apagar(DroneWrapper context);

    /**
     * Ejecuta la actividad de despegue sobre el contexto del dron.
     * Transicion valida unicamente desde EN_ESPERA → VOLANDO.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    void despegar(DroneWrapper context);

    /**
     * Ejecuta el inicio del proceso de aterrizaje sobre el contexto del dron.
     * Transicion valida unicamente desde VOLANDO → ATERRIZANDO.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    void iniciarAterrizaje(DroneWrapper context);

    /**
     * Ejecuta la finalizacion del proceso de aterrizaje sobre el contexto del dron.
     * Transicion valida unicamente desde ATERRIZANDO → EN_ESPERA.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    void finalizarAterrizaje(DroneWrapper context);

    // ── Metodos polimorficos de consulta de permisos de actividad ─────────────

    /** Indica si desde este estado se permite la actividad de encendido. */
    boolean permiteEncender();

    /** Indica si desde este estado se permite la actividad de apagado. */
    boolean permiteApagar();

    /** Indica si desde este estado se permite la actividad de despegue. */
    boolean permiteDespegar();

    /** Indica si desde este estado se permite iniciar el aterrizaje. */
    boolean permiteIniciarAterrizaje();

    /** Indica si desde este estado se permite finalizar el aterrizaje. */
    boolean permiteFinalizarAterrizaje();

    /** Indica si el dron se encuentra en una condicion apta para ejecutar misiones tecnicas. */
    boolean estaOperativoParaMision();
}
