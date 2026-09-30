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

    public void addDrone(Drone drone) throws Exception {
        crearDroneUseCase.ejecutar(drone);
    }

    public List<Drone> getAllDrones() throws Exception {
        return listarDronesUseCase.ejecutar();
    }

    public void updateDrone(Drone drone) throws Exception {
        actualizarDroneUseCase.ejecutar(drone);
    }

    public void deleteDrone(String id) throws Exception {
        eliminarDroneUseCase.ejecutar(id);
    }
}