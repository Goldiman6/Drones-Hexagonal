package com.drone.infraestructura.ui;

import com.drone.aplicacion.puerto.entrada.ActualizarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.CrearDroneUseCase;
import com.drone.aplicacion.puerto.entrada.EliminarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ListarDronesUseCase;
import com.drone.dominio.modelo.Drone;
import java.util.List;

public class DroneController {

    private final CrearDroneUseCase crearDroneUseCase;
    private final ListarDronesUseCase listarDronesUseCase;
    private final ActualizarDroneUseCase actualizarDroneUseCase;
    private final EliminarDroneUseCase eliminarDroneUseCase;

    // Inyeccion de dependencias por constructor
    public DroneController(CrearDroneUseCase crearDroneUseCase,
                           ListarDronesUseCase listarDronesUseCase,
                           ActualizarDroneUseCase actualizarDroneUseCase,
                           EliminarDroneUseCase eliminarDroneUseCase) {
        this.crearDroneUseCase = crearDroneUseCase;
        this.listarDronesUseCase = listarDronesUseCase;
        this.actualizarDroneUseCase = actualizarDroneUseCase;
        this.eliminarDroneUseCase = eliminarDroneUseCase;
    }

    public boolean addDrone(Drone drone) throws Exception {
        return crearDroneUseCase.ejecutar(drone);
    }

    public List<Drone> getAllDrones() {
        return listarDronesUseCase.ejecutar();
    }

    public boolean updateDrone(Drone drone) throws Exception {
        return actualizarDroneUseCase.ejecutar(drone);
    }

    public boolean deleteDrone(String id) throws Exception {
        return eliminarDroneUseCase.ejecutar(id);
    }
}