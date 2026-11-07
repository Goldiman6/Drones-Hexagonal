package com.drone.aplicacion.puerto.entrada;

import com.drone.aplicacion.mediator.MissionExecutionResult;

/**
 * Puerto de entrada de la arquitectura hexagonal para la ejecucion
 * y coordinacion de misiones de drones mediante el patron Mediator.
 */
public interface ExecuteMissionUseCase {

    /**
     * Ejecuta una operacion de vigilancia coordinando los subsistemas mediante el mediador.
     *
     * @param sectorVigilancia sector asignado para la operacion
     * @return resultado detallado de la coordinacion
     */
    MissionExecutionResult ejecutarMisionVigilancia(String sectorVigilancia);
}
