package com.drone.aplicacion.mediator.components;

import com.drone.aplicacion.mediator.DroneMediator;

/**
 * Componente Colega (Colleague) del patron GoF Mediator: Sistema de bateria.
 *
 * <p>Responsable de la telemetria energetica del dron y de comprobar si el nivel
 * de carga restante garantiza el despegue, la operacion de patrullaje y el retorno
 * seguro a la estacion base.</p>
 *
 * <p><b>Principio de desacoplamiento GoF:</b> Este componente NO conoce la ruta,
 * ni el enlace de radio, ni los sensores. Se limita a reportar su disponibilidad
 * energetica exclusivamente al {@link DroneMediator} mediante
 * {@code mediator.notificar(this, evento)}.</p>
 */
public class BatterySystem {

    /**
     * Referencia a la interfaz del mediador central.
     */
    private DroneMediator mediator;

    /** Nivel actual de carga expresado en porcentaje (0.0% a 100.0%). */
    private double nivelBateria;

    /**
     * Constructor por defecto: asigna una carga nominal inicial del 88.0%.
     */
    public BatterySystem() {
        this.nivelBateria = 88.0;
    }

    /**
     * Constructor que vincula el mediador durante la instanciacion.
     *
     * @param mediator instancia de {@link DroneMediator}
     */
    public BatterySystem(DroneMediator mediator) {
        this();
        this.mediator = mediator;
    }

    /**
     * Asigna o actualiza la referencia al mediador central.
     *
     * @param mediator mediador que recibira las notificaciones
     */
    public void setMediator(DroneMediator mediator) {
        this.mediator = mediator;
    }

    public double getNivelBateria() {
        return nivelBateria;
    }

    public void setNivelBateria(double nivelBateria) {
        this.nivelBateria = nivelBateria;
    }

    /**
     * Verifica si la disponibilidad energetica supera el umbral minimo de seguridad exigido.
     *
     * <p><b>Llamado de vuelta al Mediador (Componente -> Mediador):</b>
     * Una vez evaluado el porcentaje, notifica al mediador con {@code mediator.notificar(this, ...)}
     * indicando si la bateria fue validada ({@code BATERIA_OK}) o insuficiente ({@code BATERIA_INSUFICIENTE}).</p>
     *
     * @param minimoRequerido umbral de bateria minimo requerido para autorizar la mision
     * @return {@code true} si la bateria es apta, {@code false} si es insuficiente
     */
    public boolean verificarDisponibilidad(double minimoRequerido) {
        boolean apto = this.nivelBateria >= minimoRequerido;

        // ── COMUNICACIÓN COMPONENTE -> MEDIADOR ──────────────────────────────────
        // El componente le notifica el veredicto energetico al mediador
        if (this.mediator != null) {
            String evento = apto
                    ? String.format("BATERIA_OK: Nivel actual %.1f%% (Supera el umbral de seguridad minimo de %.1f%%). Energia lista.", nivelBateria, minimoRequerido)
                    : String.format("BATERIA_INSUFICIENTE: Nivel critico de %.1f%% (Inferior al umbral minimo de %.1f%%). No se autoriza el despegue.", nivelBateria, minimoRequerido);
            this.mediator.notificar(this, evento);
        }

        return apto;
    }
}
