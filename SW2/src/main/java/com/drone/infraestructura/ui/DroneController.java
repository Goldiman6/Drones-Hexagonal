package com.drone.infraestructura.ui;

import com.drone.aplicacion.puerto.entrada.ActualizarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ConsultarClimaUseCase;
import com.drone.aplicacion.puerto.entrada.CrearDroneUseCase;
import com.drone.aplicacion.puerto.entrada.EliminarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ListarDronesUseCase;
import com.drone.aplicacion.service.DroneOperacionService;
import com.drone.aplicacion.service.ProgramadorTareas;
import com.drone.aplicacion.usecase.CalcularConsumoUseCase;
import com.drone.aplicacion.weather.WeatherResultDTO;
import com.drone.aplicacion.weather.observer.DroneWeatherObserver;
import com.drone.dominio.command.DroneCommand;
import com.drone.dominio.command.EscanearZonaCommand;
import com.drone.dominio.command.MedirTemperaturaCommand;
import com.drone.dominio.command.TomarFotografiaCommand;
import com.drone.dominio.modelo.Drone;
import com.drone.dominio.strategy.ConsumoStrategy;
import com.drone.dominio.strategy.ResultadoConsumo;
import com.drone.dominio.strategy.Ruta;
import com.drone.dominio.weather.WeatherSubject;
import com.drone.infraestructura.loader.JsonRutaLoader;
import java.util.List;

public class DroneController {

    private final CrearDroneUseCase crearDroneUseCase;
    private final ListarDronesUseCase listarDronesUseCase;
    private final ActualizarDroneUseCase actualizarDroneUseCase;
    private final EliminarDroneUseCase eliminarDroneUseCase;
    private final WeatherSubject weatherSubject;
    private final ConsultarClimaUseCase consultarClimaUseCase;
    private final CalcularConsumoUseCase calcularConsumoUseCase;
    private final JsonRutaLoader jsonRutaLoader;
    private final ProgramadorTareas programadorTareas;

    public DroneController(CrearDroneUseCase crearDroneUseCase,
                           ListarDronesUseCase listarDronesUseCase,
                           ActualizarDroneUseCase actualizarDroneUseCase,
                           EliminarDroneUseCase eliminarDroneUseCase,
                           WeatherSubject weatherSubject,
                           ConsultarClimaUseCase consultarClimaUseCase) {
        this(crearDroneUseCase, listarDronesUseCase, actualizarDroneUseCase, eliminarDroneUseCase,
                weatherSubject, consultarClimaUseCase, new CalcularConsumoUseCase(), new JsonRutaLoader(), new ProgramadorTareas());
    }

    public DroneController(CrearDroneUseCase crearDroneUseCase,
                           ListarDronesUseCase listarDronesUseCase,
                           ActualizarDroneUseCase actualizarDroneUseCase,
                           EliminarDroneUseCase eliminarDroneUseCase,
                           WeatherSubject weatherSubject,
                           ConsultarClimaUseCase consultarClimaUseCase,
                           CalcularConsumoUseCase calcularConsumoUseCase,
                           JsonRutaLoader jsonRutaLoader) {
        this(crearDroneUseCase, listarDronesUseCase, actualizarDroneUseCase, eliminarDroneUseCase,
                weatherSubject, consultarClimaUseCase, calcularConsumoUseCase, jsonRutaLoader, new ProgramadorTareas());
    }

    public DroneController(CrearDroneUseCase crearDroneUseCase,
                           ListarDronesUseCase listarDronesUseCase,
                           ActualizarDroneUseCase actualizarDroneUseCase,
                           EliminarDroneUseCase eliminarDroneUseCase,
                           WeatherSubject weatherSubject,
                           ConsultarClimaUseCase consultarClimaUseCase,
                           CalcularConsumoUseCase calcularConsumoUseCase,
                           JsonRutaLoader jsonRutaLoader,
                           ProgramadorTareas programadorTareas) {
        this.crearDroneUseCase = crearDroneUseCase;
        this.listarDronesUseCase = listarDronesUseCase;
        this.actualizarDroneUseCase = actualizarDroneUseCase;
        this.eliminarDroneUseCase = eliminarDroneUseCase;
        this.weatherSubject = weatherSubject != null ? weatherSubject : new WeatherSubject();
        this.consultarClimaUseCase = consultarClimaUseCase;
        this.calcularConsumoUseCase = calcularConsumoUseCase != null ? calcularConsumoUseCase : new CalcularConsumoUseCase();
        this.jsonRutaLoader = jsonRutaLoader != null ? jsonRutaLoader : new JsonRutaLoader();
        this.programadorTareas = programadorTareas != null ? programadorTareas : new ProgramadorTareas();
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
            throw new Exception("El caso de uso de consulta climatica no esta configurado.");
        }
        return consultarClimaUseCase.ejecutar(ciudad);
    }

    // ── GESTIÓN PATRÓN STRATEGY (RUTAS Y CONSUMO ENERGÉTICO) ──

    public List<Ruta> obtenerRutasDisponibles() throws Exception {
        return jsonRutaLoader.cargarRutas("ruta.json");
    }

    public ResultadoConsumo calcularConsumo(Drone drone, Ruta ruta, ConsumoStrategy strategy) throws Exception {
        return calcularConsumoUseCase.ejecutar(drone, ruta, strategy);
    }

    // ── GESTIÓN PATRÓN COMMAND (SECUENCIA DE TAREAS / MISIÓN) ──

    public void agregarTareaFotografia(Drone drone) {
        DroneOperacionService receiver = new DroneOperacionService(drone);
        programadorTareas.agregarTarea(new TomarFotografiaCommand(receiver));
    }

    public void agregarTareaEscaneo(Drone drone) {
        DroneOperacionService receiver = new DroneOperacionService(drone);
        programadorTareas.agregarTarea(new EscanearZonaCommand(receiver));
    }

    public void agregarTareaTemperatura(Drone drone) {
        DroneOperacionService receiver = new DroneOperacionService(drone);
        programadorTareas.agregarTarea(new MedirTemperaturaCommand(receiver));
    }

    public void limpiarTareasMision() {
        programadorTareas.limpiarTareas();
    }

    public List<DroneCommand> getTareasMision() {
        return programadorTareas.getColaTareas();
    }

    public List<String> ejecutarMision() {
        return programadorTareas.ejecutarMision();
    }

    public ProgramadorTareas getProgramadorTareas() {
        return programadorTareas;
    }
}
