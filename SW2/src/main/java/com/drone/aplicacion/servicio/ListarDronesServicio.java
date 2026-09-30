package com.drone.aplicacion.servicio;
import com.drone.aplicacion.puerto.entrada.ListarDronesUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import com.drone.dominio.modelo.Drone;
import java.util.List;

public class ListarDronesServicio implements ListarDronesUseCase {
    private final DroneRepository droneRepository;
    
    public ListarDronesServicio(DroneRepository droneRepository) {
        this.droneRepository = droneRepository;
    }
    
    @Override
    public List<Drone> ejecutar() {
        return droneRepository.listar();
    }
}