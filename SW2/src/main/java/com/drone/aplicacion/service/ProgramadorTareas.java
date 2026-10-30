package com.drone.aplicacion.service;

import com.drone.dominio.command.DroneCommand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Invocador (Invoker) del patron Command.
 *
 * <p>Mantiene una cola o secuencia de comandos ({@link DroneCommand}) programados
 * para una mision de vuelo. Permite registrar nuevas tareas, vaciar la secuencia
 * y ejecutarlas en el orden estricto en que fueron programadas.</p>
 */
public class ProgramadorTareas {

    private final List<DroneCommand> colaTareas = new ArrayList<>();

    /**
     * Agrega una nueva tarea a la cola de la mision.
     *
     * @param comando comando o tarea a encolar
     */
    public void agregarTarea(DroneCommand comando) {
        if (comando != null) {
            colaTareas.add(comando);
        }
    }

    /**
     * Limpia todas las tareas actualmente encoladas en la secuencia.
     */
    public void limpiarTareas() {
        colaTareas.clear();
    }

    /**
     * Indica si la secuencia de tareas esta vacia.
     *
     * @return {@code true} si no hay tareas programadas
     */
    public boolean estaVacia() {
        return colaTareas.isEmpty();
    }

    /**
     * Retorna la cantidad de tareas programadas actualmente.
     *
     * @return numero de comandos en cola
     */
    public int cantidadTareas() {
        return colaTareas.size();
    }

    /**
     * Retorna la lista inmutable con las tareas programadas.
     *
     * @return lista no modificable de comandos en la secuencia
     */
    public List<DroneCommand> getColaTareas() {
        return Collections.unmodifiableList(colaTareas);
    }

    /**
     * Ejecuta todas las tareas encoladas en estricto orden secuencial
     * y recopila el informe de ejecucion de cada paso.
     *
     * @return lista de cadenas con el reporte de cada tarea ejecutada
     */
    public List<String> ejecutarMision() {
        List<String> reporte = new ArrayList<>();
        for (int i = 0; i < colaTareas.size(); i++) {
            DroneCommand cmd = colaTareas.get(i);
            String resultado = cmd.ejecutar();
            reporte.add(String.format("Paso %d [%s]:\n   • %s", i + 1, cmd.getNombre(), resultado));
        }
        return reporte;
    }
}
