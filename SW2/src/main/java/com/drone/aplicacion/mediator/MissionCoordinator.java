package com.drone.aplicacion.mediator;

import com.drone.aplicacion.mediator.components.BatterySystem;
import com.drone.aplicacion.mediator.components.CommunicationSystem;
import com.drone.aplicacion.mediator.components.NavigationSystem;
import com.drone.aplicacion.mediator.components.SensorSystem;
import java.util.ArrayList;
import java.util.List;

/**
 * Mediador Concreto (Concrete Mediator) del patron GoF Mediator.
 *
 * <p>Centraliza y orquesta la comunicacion e interaccion entre los cuatro componentes:
 * <ul>
 *   <li>{@link BatterySystem}: verifica la disponibilidad energetica.</li>
 *   <li>{@link CommunicationSystem}: establece el enlace de comunicacion C2.</li>
 *   <li>{@link NavigationSystem}: prepara la ruta perimetral de vuelo.</li>
 *   <li>{@link SensorSystem}: activa los sensores necesarios para la vigilancia.</li>
 * </ul>
 *
 * <p><b>Principio GoF Mediator:</b> Los componentes NO se comunican directamente entre si.
 * Toda interaccion es bidireccional entre este coordinador y cada componente individual:
 * <ol>
 *   <li><b>Mediador -> Componente:</b> El mediador invoca las operaciones tecnicas de los componentes
 *       (ej. {@code batterySystem.verificarDisponibilidad()}, {@code communicationSystem.establecerEnlace()},
 *       {@code navigationSystem.prepararRuta()}, {@code sensorSystem.activarSensoresVigilancia()}).</li>
 *   <li><b>Componente -> Mediador:</b> Cada componente notifica sus resultados al mediador mediante
 *       {@link #notificar(Object, String)}, desacoplandose completamente del resto del sistema.</li>
 * </ol>
 */
public class MissionCoordinator implements DroneMediator {

    /** Colega 1: Sistema de energia y bateria. */
    private final BatterySystem batterySystem;

    /** Colega 2: Sistema de enlace y telemetria. */
    private final CommunicationSystem communicationSystem;

    /** Colega 3: Sistema de navegacion y ruta. */
    private final NavigationSystem navigationSystem;

    /** Colega 4: Sistema de carga util y sensores. */
    private final SensorSystem sensorSystem;

    /** Bitacora de auditoria que registra cronologicamente todas las interacciones mediadas. */
    private final List<String> bitacora;

    /**
     * Constructor por defecto: inicializa los cuatro componentes colegas y
     * se registra a si mismo como mediador en cada uno de ellos mediante {@code setMediator(this)}.
     */
    public MissionCoordinator() {
        this.bitacora = new ArrayList<>();
        this.batterySystem = new BatterySystem(this);
        this.communicationSystem = new CommunicationSystem(this);
        this.navigationSystem = new NavigationSystem(this);
        this.sensorSystem = new SensorSystem(this);
    }

    /**
     * Constructor con inyeccion de dependencias para los componentes colegas.
     * Vincula a este mediador en cada componente inyectado.
     *
     * @param batterySystem       subsistema de bateria
     * @param communicationSystem subsistema de comunicacion
     * @param navigationSystem    subsistema de navegacion
     * @param sensorSystem        subsistema de sensores
     */
    public MissionCoordinator(BatterySystem batterySystem,
                              CommunicationSystem communicationSystem,
                              NavigationSystem navigationSystem,
                              SensorSystem sensorSystem) {
        this.bitacora = new ArrayList<>();
        this.batterySystem = batterySystem != null ? batterySystem : new BatterySystem(this);
        this.communicationSystem = communicationSystem != null ? communicationSystem : new CommunicationSystem(this);
        this.navigationSystem = navigationSystem != null ? navigationSystem : new NavigationSystem(this);
        this.sensorSystem = sensorSystem != null ? sensorSystem : new SensorSystem(this);

        // ── REGISTRO DEL MEDIADOR EN CADA COMPONENTE COLEGA ─────────────────────
        this.batterySystem.setMediator(this);
        this.communicationSystem.setMediator(this);
        this.navigationSystem.setMediator(this);
        this.sensorSystem.setMediator(this);
    }

    /**
     * Metodo del contrato {@link DroneMediator} invocado por los componentes colegas
     * cuando finalizan una tarea o experimentan un cambio de estado.
     *
     * <p><b>Flujo Componente -> Mediador:</b> Este metodo centraliza la recepcion de
     * eventos, impidiendo que los componentes necesiten referenciarse entre si.</p>
     *
     * @param remitente instancia del componente que origina la notificacion
     * @param evento    cadena descriptiva con el evento o datos tecnicos reportados
     */
    @Override
    public void notificar(Object remitente, String evento) {
        String componenteNombre = remitente != null ? remitente.getClass().getSimpleName() : "Desconocido";
        bitacora.add(String.format("[%s -> Mediador]: %s", componenteNombre, evento));
    }

    /**
     * Coordina y controla la secuencia estricta de condiciones requeridas para
     * iniciar una operacion de vigilancia en el sector asignado.
     *
     * <p><b>Secuencia de coordinacion (Mediador -> Componentes):</b></p>
     * <ol>
     *   <li><b>Paso 1 (Mediador -> BatterySystem):</b> Invoca {@code batterySystem.verificarDisponibilidad(30.0)}.
     *       Si la carga no supera el 30%, el mediador aborta la mision de inmediato.</li>
     *   <li><b>Paso 2 (Mediador -> CommunicationSystem):</b> Invoca {@code communicationSystem.establecerEnlace()}.
     *       Si el canal C2 no se sincroniza, el mediador aborta la mision.</li>
     *   <li><b>Paso 3 (Mediador -> NavigationSystem):</b> Invoca {@code navigationSystem.prepararRuta(sector)}.
     *       El sistema de navegacion calcula waypoints y altitud especifica para el sector (Alfa, Bravo o Charlie).</li>
     *   <li><b>Paso 4 (Mediador -> SensorSystem):</b> Invoca {@code sensorSystem.activarSensoresVigilancia()}.
     *       Energiza y calibra la camara 4K, sensor termico FLIR y LiDAR.</li>
     * </ol>
     *
     * @param sectorVigilancia sector o zona asignada ("Sector Alfa...", "Sector Bravo...", "Sector Charlie...")
     * @return {@link MissionExecutionResult} con el reporte integral de la coordinacion
     */
    public MissionExecutionResult coordinarOperacionVigilancia(String sectorVigilancia) {
        bitacora.clear();
        String sector = (sectorVigilancia != null && !sectorVigilancia.trim().isEmpty())
                ? sectorVigilancia.trim()
                : "Sector Alfa - Perímetro Base Norte";

        bitacora.add("================================================================================");
        bitacora.add(" INICIO DE COORDINACION DE MISION DE VIGILANCIA (Patron GoF Mediator)");
        bitacora.add(" Mediador central: MissionCoordinator | Sector asignado: " + sector);
        bitacora.add("================================================================================");

        // ── PASO 1: MEDIADOR -> BATTERYSYSTEM ────────────────────────────────────
        // El mediador solicita la comprobacion de carga antes de autorizar cualquier otra accion
        bitacora.add("\n[Paso 1/4] Coordinador solicita verificacion energetica a BatterySystem...");
        boolean bateriaOk = batterySystem.verificarDisponibilidad(30.0);
        if (!bateriaOk) {
            String abortMsg = "OPERACION ABORTADA POR EL MEDIADOR: Bateria insuficiente para garantizar retorno seguro.";
            bitacora.add("\n[Mediador]: " + abortMsg);
            return new MissionExecutionResult(false, sector, bitacora, abortMsg);
        }

        // ── PASO 2: MEDIADOR -> COMMUNICATIONSYSTEM ──────────────────────────────
        // Tras confirmar bateria, el mediador ordena abrir el canal de radiofrecuencia y telemetria
        bitacora.add("\n[Paso 2/4] Coordinador solicita enlace de telemetria a CommunicationSystem...");
        boolean enlaceOk = communicationSystem.establecerEnlace();
        if (!enlaceOk) {
            String abortMsg = "OPERACION ABORTADA POR EL MEDIADOR: No se pudo establecer enlace de radiofrecuencia seguro.";
            bitacora.add("\n[Mediador]: " + abortMsg);
            return new MissionExecutionResult(false, sector, bitacora, abortMsg);
        }

        // ── PASO 3: MEDIADOR -> NAVIGATIONSYSTEM ─────────────────────────────────
        // Con enlace activo, el mediador instruye al sistema de navegacion trazar la ruta segun el sector
        bitacora.add("\n[Paso 3/4] Coordinador solicita trazado de ruta perimetral a NavigationSystem...");
        navigationSystem.prepararRuta(sector);

        // ── PASO 4: MEDIADOR -> SENSORSYSTEM ─────────────────────────────────────
        // Con la ruta cargada en piloto automatico, el mediador activa la carga util de sensores
        bitacora.add("\n[Paso 4/4] Coordinador solicita activacion de carga util a SensorSystem...");
        sensorSystem.activarSensoresVigilancia();

        bitacora.add("\n================================================================================");
        bitacora.add(" RESULTADO: Todos los componentes coordinados exitosamente por MissionCoordinator.");
        bitacora.add(" Mision de vigilancia en curso sobre '" + sector + "'.");
        bitacora.add("================================================================================");

        String mensajeFinal = "¡Operacion de vigilancia iniciada y coordinada exitosamente mediante el Patron Mediator!";
        return new MissionExecutionResult(true, sector, bitacora, mensajeFinal);
    }

    public BatterySystem getBatterySystem() {
        return batterySystem;
    }

    public CommunicationSystem getCommunicationSystem() {
        return communicationSystem;
    }

    public NavigationSystem getNavigationSystem() {
        return navigationSystem;
    }

    public SensorSystem getSensorSystem() {
        return sensorSystem;
    }

    public List<String> getBitacora() {
        return new ArrayList<>(bitacora);
    }
}
