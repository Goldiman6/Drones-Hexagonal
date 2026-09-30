package com.drone.aplicacion.servicio;
import com.drone.aplicacion.puerto.entrada.CrearDroneUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import com.drone.dominio.modelo.Drone;
public class CrearDroneServicio implements CrearDroneUseCase { private final DroneRepository repo; public CrearDroneServicio(DroneRepository repo) { this.repo = repo; } @Override public void ejecutar(Drone drone) throws Exception { repo.guardar(drone); } }