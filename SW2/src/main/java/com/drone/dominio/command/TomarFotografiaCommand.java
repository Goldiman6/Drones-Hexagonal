package com.drone.dominio.command;

import com.drone.aplicacion.service.DroneOperacionService;

/**
 * Comando concreto del patron Command para la tarea de tomar fotografia.
 *
 * <p>Encapsula la solicitud de captura fotografica delegando la ejecucion
 * real al receptor {@link DroneOperacionService}.</p>
 */
public class TomarFotografiaCommand implements DroneCommand {

    private final DroneOperacionService receiver;

    /**
     * Construye el comando asignandole su receptor.
     *
     * @param receiver servicio de operaciones del dron que actua como receptor
     */
    public TomarFotografiaCommand(DroneOperacionService receiver) {
        this.receiver = receiver;
    }

    @Override
    public String getNombre() {
        return "Tomar Fotografia";
    }

    @Override
    public String ejecutar() {
        if (receiver == null) {
            return "Error: No se ha asignado un dron u operacion para este comando.";
        }
        return receiver.tomarFotografia();
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
