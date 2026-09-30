package com.drone.aplicacion.puerto.entrada;
import com.drone.dominio.modelo.Drone;
public interface ActualizarDroneUseCase { void ejecutar(Drone drone) throws Exception; }