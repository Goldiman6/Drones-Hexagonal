package com.drone.infraestructura.ui;

import com.drone.aplicacion.puerto.entrada.ActualizarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ConsultarClimaUseCase;
import com.drone.aplicacion.puerto.entrada.CrearDroneUseCase;
import com.drone.aplicacion.puerto.entrada.EliminarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ListarDronesUseCase;
import com.drone.aplicacion.weather.WeatherResultDTO;
import com.drone.aplicacion.weather.observer.DroneWeatherObserver;
import com.drone.dominio.modelo.Drone;
import com.drone.dominio.weather.WeatherSubject;
import java.util.List;

public class DroneController {

    private final CrearDroneUseCase crearDroneUseCase;
    private final ListarDronesUseCase listarDronesUseCase;
    private final ActualizarDroneUseCase actualizarDroneUseCase;
    private final EliminarDroneUseCase eliminarDroneUseCase;
    private final WeatherSubject weatherSubject;
    private final ConsultarClimaUseCase consultarClimaUseCase;

    public DroneController(CrearDroneUseCase crearDroneUseCase,
                           ListarDronesUseCase listarDronesUseCase,
                           ActualizarDroneUseCase actualizarDroneUseCase,
                           EliminarDroneUseCase eliminarDroneUseCase,
                           WeatherSubject weatherSubject,
                           ConsultarClimaUseCase consultarClimaUseCase) {
        this.crearDroneUseCase = crearDroneUseCase;
        this.listarDronesUseCase = listarDronesUseCase;
        this.actualizarDroneUseCase = actualizarDroneUseCase;
        this.eliminarDroneUseCase = eliminarDroneUseCase;
        this.weatherSubject = weatherSubject != null ? weatherSubject : new WeatherSubject();
        this.consultarClimaUseCase = consultarClimaUseCase;
    }

    public void addDrone(Drone drone) throws Exception {
        crearDroneUseCase.ejecutar(drone);
    }

    public List<Drone> getAllDrones() throws Exception {
        return listarDronesUseCase.ejecutar();
    }

    public void updateDrone(Drone drone) throws Exception {
        actualizarDroneUseCase.ejecutar(drone);
    }

    public void deleteDrone(String id) throws Exception {
        eliminarDroneUseCase.ejecutar(id);
    }

    // ── GESTIÓN PATRÓN OBSERVER (EN MEMORIA) ──

    public boolean suscribirDrone(Drone drone) {
        if (drone == null) return false;
        return weatherSubject.suscribir(new DroneWeatherObserver(drone));
    }

    public boolean desuscribirDrone(String id) {
        return weatherSubject.desuscribir(id);
    }

    public boolean estaSuscrito(String id) {
        return weatherSubject.estaSuscrito(id);
    }

    public List<String> getDronesSuscritosIds() {
        return weatherSubject.getIdentificadoresSuscritos();
    }

    public WeatherSubject getWeatherSubject() {
        return weatherSubject;
    }

    // ── GESTIÓN CLIMA Y CADENA DE RESPONSABILIDAD ──

    public WeatherResultDTO consultarYEvaluarClima(String ciudad) throws Exception {
        if (consultarClimaUseCase == null) {
            throw new Exception("El caso de uso de consulta climática no está configurado.");
        }
        return consultarClimaUseCase.ejecutar(ciudad);
    }
}
