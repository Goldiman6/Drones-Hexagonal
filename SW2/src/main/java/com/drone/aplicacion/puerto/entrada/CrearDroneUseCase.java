package com.drone.aplicacion.puerto.entrada;
import com.drone.dominio.modelo.Drone;
public interface CrearDroneUseCase { void ejecutar(Drone drone) throws Exception; }