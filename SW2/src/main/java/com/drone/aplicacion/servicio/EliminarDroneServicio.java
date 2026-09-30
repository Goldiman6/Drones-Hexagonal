package com.drone.aplicacion.servicio;
import com.drone.aplicacion.puerto.entrada.EliminarDroneUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
public class EliminarDroneServicio implements EliminarDroneUseCase { private final DroneRepository repo; public EliminarDroneServicio(DroneRepository repo) { this.repo = repo; } @Override public void ejecutar(String id) throws Exception { repo.eliminar(id); } }