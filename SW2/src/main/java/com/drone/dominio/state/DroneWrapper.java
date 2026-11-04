package com.drone.dominio.state;

import com.drone.dominio.modelo.Drone;

/**
 * Contexto (Context) del patron State para la gestion del estado operativo de un dron.
 *
 * <p>Envuelve a una entidad {@link Drone} del dominio mediante composicion, manteniendo
 * separado el estado operativo transitorio ({@code estadoActual}) del modelo de datos persistido.
 * De este modo, {@link Drone} permanece como entidad de dominio pura (sin logica de estado),
 * y toda la gestion del ciclo de vida operativo (APAGADO, EN_ESPERA, VOLANDO, ATERRIZANDO)
 * se centraliza aqui.</p>
 *
 * <p>El contexto se pasa a cada estado concreto en sus metodos de actividad, permitiendo que
 * el estado actualice {@code estadoActual} mediante {@link #setEstado(DroneState)} sin
 * exponer ese mecanismo al exterior (encapsulamiento del patron).</p>
 *
 * <p>El estado es transitorio (en memoria) y NO se persiste en base de datos.</p>
 */
public class DroneWrapper {

    /** Dron de dominio envuelto (referencia inmutable; no se modifica). */
    private final Drone drone;

    /**
     * Estado operativo actual del dron en esta sesion.
     * Siempre inicia en {@link OffState} (APAGADO).
     */
    private DroneState estadoActual;

    /**
     * Construye el contexto State para el dron indicado.
     * El estado inicial es siempre APAGADO.
     *
     * @param drone entidad de dominio a envolver (no nula)
     */
    public DroneWrapper(Drone drone) {
        this.drone = drone;
        this.estadoActual = new OffState();
    }


    /**
     * Retorna la entidad de dominio envuelta.
     *
     * @return instancia de {@link Drone}
     */
    public Drone getDrone() {
        return drone;
    }

    /**
     * Conveniencia: retorna el identificador del dron envuelto.
     *
     * @return id del dron
     */
    public String getId() {
        return drone != null ? drone.getId() : null;
    }

    // ── Patron State: gestion del contexto ──────────────────────────────────

    /**
     * Retorna el nombre del estado operativo actual.
     *
     * @return "APAGADO", "EN_ESPERA", "VOLANDO" o "ATERRIZANDO"
     */
    public String getNombreEstado() {
        return getEstadoActual().getNombre();
    }

    /**
     * Retorna la instancia de estado actual (polimorfismo de estado).
     *
     * @return estado operativo actual; nunca nulo (garantia: retorna OffState si es nulo)
     */
    public DroneState getEstadoActual() {
        if (estadoActual == null) {
            estadoActual = new OffState();
        }
        return estadoActual;
    }

    /**
     * Permite a los estados concretos actualizar el estado interno del contexto
     * tras validar la transicion.
     *
     * <p>Solo los estados concretos (OffState, ReadyState, FlyingState, LandingState)
     * deben invocar este metodo desde sus metodos de actividad.</p>
     *
     * @param nuevoEstado nueva instancia de {@link DroneState}; ignorado si es nulo
     */
    public void setEstado(DroneState nuevoEstado) {
        if (nuevoEstado != null) {
            this.estadoActual = nuevoEstado;
        }
    }

    // ── Actividades del ciclo operativo (delegacion al estado actual) ────────

    /**
     * Actividad de Encendido: delega en el estado actual enviando este contexto.
     * Transicion valida unicamente desde APAGADO → EN_ESPERA.
     *
     * @throws IllegalStateException si la transicion no esta permitida desde el estado actual
     */
    public void encender() {
        getEstadoActual().encender(this);
    }

    /**
     * Actividad de Apagado: delega en el estado actual enviando este contexto.
     * Transicion valida unicamente desde EN_ESPERA → APAGADO.
     *
     * @throws IllegalStateException si la transicion no esta permitida desde el estado actual
     */
    public void apagar() {
        getEstadoActual().apagar(this);
    }

    /**
     * Actividad de Despegue: delega en el estado actual enviando este contexto.
     * Transicion valida unicamente desde EN_ESPERA → VOLANDO.
     *
     * @throws IllegalStateException si la transicion no esta permitida desde el estado actual
     */
    public void despegar() {
        getEstadoActual().despegar(this);
    }

    /**
     * Actividad de Iniciar Aterrizaje: delega en el estado actual enviando este contexto.
     * Transicion valida unicamente desde VOLANDO → ATERRIZANDO.
     *
     * @throws IllegalStateException si la transicion no esta permitida desde el estado actual
     */
    public void iniciarAterrizaje() {
        getEstadoActual().iniciarAterrizaje(this);
    }

    /**
     * Actividad de Finalizar Aterrizaje: delega en el estado actual enviando este contexto.
     * Transicion valida unicamente desde ATERRIZANDO → EN_ESPERA.
     *
     * @throws IllegalStateException si la transicion no esta permitida desde el estado actual
     */
    public void finalizarAterrizaje() {
        getEstadoActual().finalizarAterrizaje(this);
    }
}
