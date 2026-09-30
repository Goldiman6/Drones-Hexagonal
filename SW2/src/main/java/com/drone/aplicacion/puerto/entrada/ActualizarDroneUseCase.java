package com.drone.aplicacion.puerto.entrada;
import com.drone.dominio.modelo.Drone;

public interface ActualizarDroneUseCase {
    boolean ejecutar(Drone drone);
}