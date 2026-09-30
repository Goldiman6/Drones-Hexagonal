package com.drone.aplicacion.puerto.entrada;
import com.drone.dominio.modelo.Drone;

public interface CrearDroneUseCase {
    boolean ejecutar(Drone drone);
}