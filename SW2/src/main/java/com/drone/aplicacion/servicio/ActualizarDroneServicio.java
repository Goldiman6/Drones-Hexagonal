package com.drone.aplicacion.servicio;
import com.drone.aplicacion.puerto.entrada.ActualizarDroneUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import com.drone.dominio.modelo.Drone;

public class ActualizarDroneServicio implements ActualizarDroneUseCase {
    private final DroneRepository droneRepository;
    
    public ActualizarDroneServicio(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }
    
    @Override
    public boolean ejecutar(Drone drone) {
        return droneRepository.actualizar(drone);
    }
}