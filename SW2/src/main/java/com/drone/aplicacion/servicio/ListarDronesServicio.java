package com.drone.aplicacion.servicio;
import com.drone.aplicacion.puerto.entrada.ListarDronesUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import com.drone.dominio.modelo.Drone;
import java.util.List;
public class ListarDronesServicio implements ListarDronesUseCase { private final DroneRepository repo; public ListarDronesServicio(DroneRepository repo) { this.repo = repo; } @Override public List<Drone> ejecutar() throws Exception { return repo.listar(); } }