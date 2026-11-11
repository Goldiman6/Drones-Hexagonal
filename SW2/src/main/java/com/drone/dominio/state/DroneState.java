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
    default void encender(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' no puede encenderse desde el estado " + getNombre() + "."
        );
    }

    /**
     * Ejecuta la actividad de apagado sobre el contexto del dron.
     * Transicion valida unicamente desde EN_ESPERA → APAGADO.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    default void apagar(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' no puede apagarse desde el estado " + getNombre() + "."
        );
    }

    /**
     * Ejecuta la actividad de despegue sobre el contexto del dron.
     * Transicion valida unicamente desde EN_ESPERA → VOLANDO.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    default void despegar(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' no puede despegar desde el estado " + getNombre() + "."
        );
    }

    /**
     * Ejecuta el inicio del proceso de aterrizaje sobre el contexto del dron.
     * Transicion valida unicamente desde VOLANDO → ATERRIZANDO.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    default void iniciarAterrizaje(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' no puede iniciar aterrizaje desde el estado " + getNombre() + "."
        );
    }

    /**
     * Ejecuta la finalizacion del proceso de aterrizaje sobre el contexto del dron.
     * Transicion valida unicamente desde ATERRIZANDO → EN_ESPERA.
     *
     * @param context contexto (wrapper) del dron sobre el cual opera el estado
     * @throws IllegalStateException si la transicion no es permitida desde este estado
     */
    default void finalizarAterrizaje(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' no puede finalizar aterrizaje desde el estado " + getNombre() + "."
        );
    }

    // ── Metodos polimorficos de consulta de permisos de actividad ─────────────
    // Se proporcionan implementaciones por defecto (retornando false) para evitar
    // duplicacion en las clases concretas de estado.

    /** Indica si desde este estado se permite la actividad de encendido. */
    default boolean permiteEncender() {
        return false;
    }

    /** Indica si desde este estado se permite la actividad de apagado. */
    default boolean permiteApagar() {
        return false;
    }

    /** Indica si desde este estado se permite la actividad de despegue. */
    default boolean permiteDespegar() {
        return false;
    }

    /** Indica si desde este estado se permite iniciar el aterrizaje. */
    default boolean permiteIniciarAterrizaje() {
        return false;
    }

    /** Indica si desde este estado se permite finalizar el aterrizaje. */
    default boolean permiteFinalizarAterrizaje() {
        return false;
    }

    /** Indica si el dron se encuentra en una condicion apta para ejecutar misiones tecnicas. */
    default boolean estaOperativoParaMision() {
        return false;
    }
}
