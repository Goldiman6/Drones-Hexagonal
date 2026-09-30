package com.drone.aplicacion.servicio;
import com.drone.aplicacion.puerto.entrada.ActualizarDroneUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import com.drone.dominio.modelo.Drone;
public class ActualizarDroneServicio implements ActualizarDroneUseCase { private final DroneRepository repo; public ActualizarDroneServicio(DroneRepository repo) { this.repo = repo; } @Override public void ejecutar(Drone drone) throws Exception { repo.actualizar(drone); } }