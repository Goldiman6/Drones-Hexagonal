package com.drone.dominio.command;

/**
 * Interfaz del patron Command que define el contrato para todas
 * las tareas u operaciones que puede ejecutar un dron durante una mision.
 */
public interface DroneCommand {

    /**
     * Retorna el nombre descriptivo de la tarea o comando.
     *
     * @return nombre del comando (por ejemplo: "Tomar Fotografia")
     */
    String getNombre();

    /**
     * Ejecuta la tarea delegando la accion al receptor (Receiver)
     * y retorna el reporte o resultado de la operacion.
     *
     * @return resultado o log detallado de la ejecucion
     */
    String ejecutar();
}
