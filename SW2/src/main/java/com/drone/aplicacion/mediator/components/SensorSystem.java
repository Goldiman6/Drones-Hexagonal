package com.drone.aplicacion.mediator.components;

import com.drone.aplicacion.mediator.DroneMediator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Componente Colega (Colleague) del patron GoF Mediator: Sistema de sensores.
 *
 * <p>Responsable de energizar, inicializar y calibrar la suite de carga util (payload)
 * para la mision de vigilancia (camara optica 4K, sensor termico infrarrojo FLIR, LiDAR perimetral 360°).</p>
 *
 * <p><b>Principio de desacoplamiento GoF:</b> Este componente NO se comunica con
 * el sistema de navegacion ni consulta el nivel de bateria directamente. Su unica via
 * de comunicacion hacia el exterior es notificar al {@link DroneMediator} mediante
 * {@code mediator.notificar(this, evento)}.</p>
 */
public class SensorSystem {

    /**
     * Referencia a la interfaz del mediador central.
     */
    private DroneMediator mediator;

    private final List<String> sensoresDisponibles;
    private boolean sensoresActivos;

    /**
     * Constructor por defecto: configura la suite de sensores de vigilancia requeridos.
     */
    public SensorSystem() {
        this.sensoresDisponibles = Arrays.asList(
                "Camara Optica 4K Ultra-HD (Zoom 30x)",
                "Sensor Termico Infrarrojo FLIR",
                "Escaner LiDAR Perimetral 360"
        );
        this.sensoresActivos = false;
    }

    /**
     * Constructor que vincula el mediador durante la instanciacion.
     *
     * @param mediator instancia de {@link DroneMediator}
     */
    public SensorSystem(DroneMediator mediator) {
        this();
        this.mediator = mediator;
    }

    /**
     * Asigna o actualiza la referencia al mediador central.
     *
     * @param mediator mediador que recibira las notificaciones de los sensores
     */
    public void setMediator(DroneMediator mediator) {
        this.mediator = mediator;
    }

    public boolean isSensoresActivos() {
        return sensoresActivos;
    }

    public List<String> getSensoresDisponibles() {
        return Collections.unmodifiableList(sensoresDisponibles);
    }

    /**
     * Activa y calibra la suite completa de sensores para la operacion de vigilancia.
     *
     * <p><b>Llamado de vuelta al Mediador (Componente -> Mediador):</b>
     * Una vez energizada y calibrada la carga util, notifica al {@link DroneMediator}
     * con {@code mediator.notificar(this, "SENSORES_ACTIVADOS: ...")}.</p>
     *
     * @return reporte descriptivo de los sensores activados
     */
    public String activarSensoresVigilancia() {
        this.sensoresActivos = true;

        String detalle = String.format(
                "SENSORES_ACTIVADOS: Suite de vigilancia lista y calibrada (%s). " +
                "Transmision de video 4K y telemetria espectral infrarroja en tiempo real iniciada.",
                String.join(", ", sensoresDisponibles)
        );

        // ── COMUNICACIÓN COMPONENTE -> MEDIADOR ──────────────────────────────────
        // El componente le notifica al mediador central que los sensores estan operativos
        if (this.mediator != null) {
            this.mediator.notificar(this, detalle);
        }

        return detalle;
    }

    /**
     * Pone la suite de sensores en modo de reposo o consumo reducido.
     */
    public void desactivarSensores() {
        this.sensoresActivos = false;

        // ── COMUNICACIÓN COMPONENTE -> MEDIADOR ──────────────────────────────────
        if (this.mediator != null) {
            this.mediator.notificar(this, "SENSORES_DESACTIVADOS: Suite de sensores puesta en reposo.");
        }
    }
}
