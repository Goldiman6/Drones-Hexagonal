package com.drone.infraestructura.ui;

import com.drone.aplicacion.puerto.entrada.ActualizarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ConsultarClimaUseCase;
import com.drone.aplicacion.puerto.entrada.CrearDroneUseCase;
import com.drone.aplicacion.puerto.entrada.EliminarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ExecuteMissionUseCase;
import com.drone.aplicacion.puerto.entrada.ListarDronesUseCase;
import com.drone.aplicacion.puerto.entrada.ManageDroneStateUseCase;
import com.drone.aplicacion.mediator.MissionExecutionResult;
import com.drone.aplicacion.service.DroneOperacionService;
import com.drone.aplicacion.service.DroneStateService;
import com.drone.aplicacion.service.MissionService;
import com.drone.aplicacion.service.ProgramadorTareas;
import com.drone.aplicacion.usecase.CalcularConsumoUseCase;
import com.drone.aplicacion.weather.WeatherResultDTO;
import com.drone.aplicacion.weather.observer.DroneWeatherObserver;
import com.drone.dominio.command.DroneCommand;
import com.drone.dominio.command.EscanearZonaCommand;
import com.drone.dominio.command.MedirTemperaturaCommand;
import com.drone.dominio.command.TomarFotografiaCommand;
import com.drone.dominio.modelo.Drone;
import com.drone.dominio.state.DroneWrapper;
import com.drone.dominio.strategy.ConsumoStrategy;
import com.drone.dominio.strategy.ResultadoConsumo;
import com.drone.dominio.strategy.Ruta;
import com.drone.dominio.weather.WeatherSubject;
import com.drone.infraestructura.loader.JsonRutaLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ManageDroneStateUseCase manageDroneStateUseCase;
    private final ExecuteMissionUseCase executeMissionUseCase;

    /**
     * Mapa en memoria que asocia el ID de cada dron con su DroneWrapper (contexto State).
     * El estado operativo es transitorio: vive aqui durante la sesion y no se persiste en BD.
     */
    private final Map<String, DroneWrapper> droneWrappers = new HashMap<>();

    public DroneController(CrearDroneUseCase crearDroneUseCase,
                           ListarDronesUseCase listarDronesUseCase,
                           ActualizarDroneUseCase actualizarDroneUseCase,
                           EliminarDroneUseCase eliminarDroneUseCase,
                           WeatherSubject weatherSubject,
                           ConsultarClimaUseCase consultarClimaUseCase) {
        this(crearDroneUseCase, listarDronesUseCase, actualizarDroneUseCase, eliminarDroneUseCase,
                weatherSubject, consultarClimaUseCase, new CalcularConsumoUseCase(),
                new JsonRutaLoader(), new ProgramadorTareas(), new DroneStateService(), new MissionService());
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
                weatherSubject, consultarClimaUseCase, calcularConsumoUseCase,
                jsonRutaLoader, new ProgramadorTareas(), new DroneStateService(), new MissionService());
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
        this(crearDroneUseCase, listarDronesUseCase, actualizarDroneUseCase, eliminarDroneUseCase,
                weatherSubject, consultarClimaUseCase, calcularConsumoUseCase,
                jsonRutaLoader, programadorTareas, new DroneStateService(), new MissionService());
    }

    public DroneController(CrearDroneUseCase crearDroneUseCase,
                           ListarDronesUseCase listarDronesUseCase,
                           ActualizarDroneUseCase actualizarDroneUseCase,
                           EliminarDroneUseCase eliminarDroneUseCase,
                           WeatherSubject weatherSubject,
                           ConsultarClimaUseCase consultarClimaUseCase,
                           CalcularConsumoUseCase calcularConsumoUseCase,
                           JsonRutaLoader jsonRutaLoader,
                           ProgramadorTareas programadorTareas,
                           ManageDroneStateUseCase manageDroneStateUseCase) {
        this(crearDroneUseCase, listarDronesUseCase, actualizarDroneUseCase, eliminarDroneUseCase,
                weatherSubject, consultarClimaUseCase, calcularConsumoUseCase,
                jsonRutaLoader, programadorTareas, manageDroneStateUseCase, new MissionService());
    }

    public DroneController(CrearDroneUseCase crearDroneUseCase,
                           ListarDronesUseCase listarDronesUseCase,
                           ActualizarDroneUseCase actualizarDroneUseCase,
                           EliminarDroneUseCase eliminarDroneUseCase,
                           WeatherSubject weatherSubject,
                           ConsultarClimaUseCase consultarClimaUseCase,
                           CalcularConsumoUseCase calcularConsumoUseCase,
                           JsonRutaLoader jsonRutaLoader,
                           ProgramadorTareas programadorTareas,
                           ManageDroneStateUseCase manageDroneStateUseCase,
                           ExecuteMissionUseCase executeMissionUseCase) {
        this.crearDroneUseCase = crearDroneUseCase;
        this.listarDronesUseCase = listarDronesUseCase;
        this.actualizarDroneUseCase = actualizarDroneUseCase;
        this.eliminarDroneUseCase = eliminarDroneUseCase;
        this.weatherSubject = weatherSubject != null ? weatherSubject : new WeatherSubject();
        this.consultarClimaUseCase = consultarClimaUseCase;
        this.calcularConsumoUseCase = calcularConsumoUseCase != null ? calcularConsumoUseCase : new CalcularConsumoUseCase();
        this.jsonRutaLoader = jsonRutaLoader != null ? jsonRutaLoader : new JsonRutaLoader();
        this.programadorTareas = programadorTareas != null ? programadorTareas : new ProgramadorTareas();
        this.manageDroneStateUseCase = manageDroneStateUseCase != null ? manageDroneStateUseCase : new DroneStateService();
        this.executeMissionUseCase = executeMissionUseCase != null ? executeMissionUseCase : new MissionService();
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
        // Eliminar tambien el wrapper en memoria al eliminar el dron
        droneWrappers.remove(id);
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

    // ── GESTIÓN PATRÓN STATE (ESTADOS OPERATIVOS) ──

    /**
     * Obtiene el DroneWrapper existente para el dron dado, o crea uno nuevo si no existe.
     * El wrapper es el Contexto del patron State y vive en memoria durante la sesion.
     *
     * @param drone entidad de dominio cuyo wrapper se necesita
     * @return instancia de {@link DroneWrapper} asociada al ID del dron
     */
    public DroneWrapper obtenerWrapper(Drone drone) {
        if (drone == null || drone.getId() == null) return null;
        return droneWrappers.computeIfAbsent(drone.getId(), id -> new DroneWrapper(drone));
    }

    /**
     * Ejecuta una transicion de estado sobre el DroneWrapper del dron seleccionado.
     * El wrapper se crea automaticamente si es la primera operacion sobre ese dron.
     *
     * @param drone     el dron de dominio cuyo estado operativo se desea cambiar
     * @param operacion operacion a ejecutar (ENCENDER, APAGAR, DESPEGAR,
     *                  INICIAR_ATERRIZAJE, FINALIZAR_ATERRIZAJE)
     * @return nombre del nuevo estado tras la transicion
     * @throws IllegalStateException    si la transicion no esta permitida
     * @throws IllegalArgumentException si la operacion es desconocida
     */
    public String cambiarEstadoDrone(Drone drone, String operacion) {
        DroneWrapper wrapper = obtenerWrapper(drone);
        return manageDroneStateUseCase.ejecutarTransicion(wrapper, operacion);
    }

    // ── GESTIÓN PATRÓN MEDIATOR (COORDINACIÓN DE OPERACIÓN DE VIGILANCIA) ──

    /**
     * Inicia y coordina una operacion de vigilancia mediante el patron GoF Mediator.
     * Centraliza la comunicacion entre NavigationSystem, SensorSystem, BatterySystem
     * y CommunicationSystem a traves del mediador MissionCoordinator.
     *
     * @param sectorVigilancia sector o zona geografica asignada
     * @return resultado de la ejecucion con bitacora de coordinacion
     */
    public MissionExecutionResult coordinarOperacionVigilancia(String sectorVigilancia) {
        return executeMissionUseCase.ejecutarMisionVigilancia(sectorVigilancia);
    }
}
