package com.drone.aplicacion.service;

import com.drone.aplicacion.mediator.MissionCoordinator;
import com.drone.aplicacion.mediator.MissionExecutionResult;
import com.drone.aplicacion.puerto.entrada.ExecuteMissionUseCase;

/**
 * Servicio de la capa de aplicacion que implementa el caso de uso
 * {@link ExecuteMissionUseCase} delegando en el mediador {@link MissionCoordinator}.
 */
public class MissionService implements ExecuteMissionUseCase {

    private final MissionCoordinator missionCoordinator;

    public MissionService() {
        this.missionCoordinator = new MissionCoordinator();
    }

    public MissionService(MissionCoordinator missionCoordinator) {
        this.missionCoordinator = missionCoordinator != null ? missionCoordinator : new MissionCoordinator();
    }

    @Override
    public MissionExecutionResult ejecutarMisionVigilancia(String sectorVigilancia) {
        return missionCoordinator.coordinarOperacionVigilancia(sectorVigilancia);
    }

    public MissionCoordinator getMissionCoordinator() {
        return missionCoordinator;
    }
}
