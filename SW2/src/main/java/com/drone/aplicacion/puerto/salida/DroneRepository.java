package com.drone.aplicacion.puerto.salida;

import com.drone.dominio.modelo.Drone;
import java.util.List;

public interface DroneRepository {
    boolean guardar(Drone drone);
    List<Drone> listar();
    boolean actualizar(Drone drone);
    boolean eliminar(String id);
}