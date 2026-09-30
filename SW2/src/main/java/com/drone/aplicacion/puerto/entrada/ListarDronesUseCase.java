package com.drone.aplicacion.puerto.entrada;
import com.drone.dominio.modelo.Drone;
import java.util.List;

public interface ListarDronesUseCase {
    List<Drone> ejecutar();
}