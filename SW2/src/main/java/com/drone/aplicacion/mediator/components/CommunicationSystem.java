package com.drone.aplicacion.mediator.components;

import com.drone.aplicacion.mediator.DroneMediator;

/**
 * Componente Colega (Colleague) del patron GoF Mediator: Sistema de comunicacion.
 *
 * <p>Responsable del canal de control C2 (Command &amp; Control), enlace de radiofrecuencia
 * encriptada y sincronizacion de la telemetria en tiempo real con la estacion de control terrestre.</p>
 *
 * <p><b>Principio de desacoplamiento GoF:</b> Este componente NO conoce la bateria,
 * ni las coordenadas de navegacion, ni la carga util de sensores. Su unica interaccion
 * externa es notificar al {@link DroneMediator} mediante {@code mediator.notificar(this, evento)}.</p>
 */
public class CommunicationSystem {

    /**
     * Referencia a la interfaz del mediador central.
     */
    private DroneMediator mediator;

    private boolean enlaceActivo;
    private String frecuencia;

    /**
     * Constructor por defecto: inicializa el canal seguro en banda 5.8 GHz con cifrado AES-256.
     */
    public CommunicationSystem() {
        this.enlaceActivo = false;
        this.frecuencia = "5.8 GHz AES-256";
    }

    /**
     * Constructor que vincula el mediador durante la instanciacion.
     *
     * @param mediator instancia de {@link DroneMediator}
     */
    public CommunicationSystem(DroneMediator mediator) {
        this();
        this.mediator = mediator;
    }

    /**
     * Asigna o actualiza la referencia al mediador central.
     *
     * @param mediator mediador que recibira las notificaciones de comunicacion
     */
    public void setMediator(DroneMediator mediator) {
        this.mediator = mediator;
    }

    public boolean isEnlaceActivo() {
        return enlaceActivo;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    /**
     * Establece el handshake de comunicacion con la base y abre el canal de telemetria C2.
     *
     * <p><b>Llamado de vuelta al Mediador (Componente -> Mediador):</b>
     * Una vez sincronizado el enlace, notifica al {@link DroneMediator} mediante
     * {@code mediator.notificar(this, "ENLACE_ESTABLECIDO: ...")}.</p>
     *
     * @return {@code true} si el canal se establecio exitosamente
     */
    public boolean establecerEnlace() {
        this.enlaceActivo = true;

        // ── COMUNICACIÓN COMPONENTE -> MEDIADOR ──────────────────────────────────
        // El componente notifica al mediador que el canal de datos esta activo
        if (this.mediator != null) {
            String evento = String.format(
                    "ENLACE_ESTABLECIDO: Canal de radiofrecuencia C2 seguro (%s) activo. " +
                    "Telemetria bidireccional y telemetria GPS sincronizadas con la estacion base.",
                    frecuencia
            );
            this.mediator.notificar(this, evento);
        }

        return this.enlaceActivo;
    }

    /**
     * Cierra el enlace de comunicacion y notifica al mediador.
     */
    public void cerrarEnlace() {
        this.enlaceActivo = false;

        // ── COMUNICACIÓN COMPONENTE -> MEDIADOR ──────────────────────────────────
        if (this.mediator != null) {
            this.mediator.notificar(this, "ENLACE_CERRADO: Canal de comunicacion C2 desconectado de forma segura.");
        }
    }
}
