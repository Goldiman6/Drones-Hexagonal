package com.drone.aplicacion.port.in;

import com.drone.aplicacion.mediator.MissionExecutionResult;

/**
 * Puerto de entrada (Port In) segun la nomenclatura de arquitectura hexagonal en ingles.
 */
public interface ExecuteMissionUseCase extends com.drone.aplicacion.puerto.entrada.ExecuteMissionUseCase {

    @Override
    MissionExecutionResult ejecutarMisionVigilancia(String sectorVigilancia);
}
