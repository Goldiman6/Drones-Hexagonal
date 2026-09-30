package com.drone.aplicacion.servicio;

import com.drone.aplicacion.puerto.entrada.CrearDroneUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import com.drone.dominio.modelo.Drone;

public class CrearDroneServicio implements CrearDroneUseCase {
    private final DroneRepository droneRepository;
    
    public CrearDroneServicio(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }
    
    @Override
    public boolean ejecutar(Drone drone) {
        return droneRepository.guardar(drone);
    }
}