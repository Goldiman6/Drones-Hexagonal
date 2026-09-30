package com.drone.aplicacion.puerto.salida;

import com.drone.dominio.modelo.Drone;
import java.util.List;

public interface DroneRepository {
    void guardar(Drone drone) throws Exception;
    List<Drone> listar() throws Exception;
    void actualizar(Drone drone) throws Exception;
    void eliminar(String id) throws Exception;
}