package com.drone.dominio.command;

import com.drone.aplicacion.service.DroneOperacionService;

/**
 * Comando concreto del patron Command para la tarea de escanear zona.
 *
 * <p>Encapsula la solicitud de escaneo de superficie o perimetro delegando
 * la ejecucion real al receptor {@link DroneOperacionService}.</p>
 */
public class EscanearZonaCommand implements DroneCommand {

    private final DroneOperacionService receiver;

    /**
     * Construye el comando asignandole su receptor.
     *
     * @param receiver servicio de operaciones del dron que actua como receptor
     */
    public EscanearZonaCommand(DroneOperacionService receiver) {
        this.receiver = receiver;
    }

    @Override
    public String getNombre() {
        return "Escanear Zona";
    }

    @Override
    public String ejecutar() {
        if (receiver == null) {
            return "Error: No se ha asignado un dron u operacion para este comando.";
        }
        return receiver.escanearZona();
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
