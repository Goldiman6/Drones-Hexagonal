package com.drone.aplicacion.servicio;
import com.drone.aplicacion.puerto.entrada.EliminarDroneUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;

public class EliminarDroneServicio implements EliminarDroneUseCase {
    private final DroneRepository droneRepository;
    
    public EliminarDroneServicio(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }
    
    @Override
    public boolean ejecutar(String id) {
        return droneRepository.eliminar(id);
    }
}