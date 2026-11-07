package com.drone.aplicacion.mediator.components;

import com.drone.aplicacion.mediator.DroneMediator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Componente Colega (Colleague) del patron GoF Mediator: Sistema de navegacion.
 *
 * <p>Responsable de calcular y configurar el plan de vuelo perimetral del dron
 * adaptado a las condiciones topograficas, geocercas y requerimientos especificos
 * de cada sector de vigilancia asignado:
 * <ul>
 *   <li><b>Sector Alfa (Perimetro Base Norte):</b> Terreno llano y despejado, patrullaje perimetral estandar
 *       con 8 waypoints, 120m de altitud segura, 6.5 km de recorrido y velocidad de 45 km/h.</li>
 *   <li><b>Sector Bravo (Frontera Forestal):</b> Zona montanosa de vegetacion densa, escaneo perimetral
 *       con 14 waypoints continuos, vuelo a baja cota (80m) bajo dosel forestal, 11.2 km de recorrido y velocidad de 35 km/h.</li>
 *   <li><b>Sector Charlie (Instalaciones Criticas):</b> Complejo industrial de alta seguridad con zonas de exclusion
 *       aerea y geocercas estrictas, 6 waypoints de alta precision perimetral, 150m de altitud para vista panoramica, 4.0 km de recorrido y velocidad de 50 km/h.</li>
 * </ul>
 *
 * <p><b>Principio de desacoplamiento GoF Mediator:</b>
 * Este componente NO conoce la existencia de {@link BatterySystem}, {@link CommunicationSystem}
 * ni {@link SensorSystem}. Toda interaccion hacia el sistema se realiza exclusivamente
 * notificando a la interfaz {@link DroneMediator} mediante {@code mediator.notificar(this, evento)}.</p>
 */
public class NavigationSystem {

    /**
     * Referencia a la interfaz del mediador central (Torre de control).
     * El componente solo interactua a traves de este contrato abstracto.
     */
    private DroneMediator mediator;

    /** Identificador o nombre del sector actualmente cargado en el piloto automatico. */
    private String rutaActual;

    /** Cantidad de puntos de navegacion (waypoints) que componen la ruta. */
    private int cantidadWaypoints;

    /** Altitud de vuelo de crucero segura en metros (AGL / Above Ground Level). */
    private double altitudCruceroMetros;

    /** Distancia total estimada del circuito de patrullaje en kilometros. */
    private double distanciaEstimadaKm;

    /** Velocidad promedio de crucero recomendada en km/h. */
    private double velocidadCruceroKmH;

    /** Descripcion topografica y condiciones del entorno del sector. */
    private String descripcionTerreno;

    /** Lista detallada de coordenadas y puntos de navegacion calculados para la ruta. */
    private final List<String> waypointsCalculados = new ArrayList<>();

    /**
     * Constructor por defecto del sistema de navegacion.
     */
    public NavigationSystem() {
        this.cantidadWaypoints = 8;
        this.altitudCruceroMetros = 120.0;
        this.distanciaEstimadaKm = 6.5;
        this.velocidadCruceroKmH = 45.0;
        this.descripcionTerreno = "Llanura abierta con linea visual despejada";
    }

    /**
     * Constructor que vincula al mediador central durante la instanciacion.
     *
     * @param mediator instancia de {@link DroneMediator} que coordinara la mision
     */
    public NavigationSystem(DroneMediator mediator) {
        this();
        this.mediator = mediator;
    }

    /**
     * Asigna o actualiza la referencia al mediador central.
     *
     * @param mediator mediador que recibira las notificaciones de navegacion
     */
    public void setMediator(DroneMediator mediator) {
        this.mediator = mediator;
    }

    public String getRutaActual() {
        return rutaActual;
    }

    public int getCantidadWaypoints() {
        return cantidadWaypoints;
    }

    public double getAltitudCruceroMetros() {
        return altitudCruceroMetros;
    }

    public double getDistanciaEstimadaKm() {
        return distanciaEstimadaKm;
    }

    public double getVelocidadCruceroKmH() {
        return velocidadCruceroKmH;
    }

    public String getDescripcionTerreno() {
        return descripcionTerreno;
    }

    public List<String> getWaypointsCalculados() {
        return Collections.unmodifiableList(waypointsCalculados);
    }

    /**
     * Prepara, parametriza y calcula el plan de vuelo perimetral especifico
     * para el sector de vigilancia recibido:
     * <ul>
     *   <li>Si el sector contiene <b>"BRAVO"</b> o <b>"FORESTAL"</b>: configura el plan para el Sector Bravo.</li>
     *   <li>Si el sector contiene <b>"CHARLIE"</b> o <b>"CRÍTICAS"</b>: configura el plan para el Sector Charlie.</li>
     *   <li>En cualquier otro caso: configura el plan estandar para el Sector Alfa.</li>
     * </ul>
     *
     * <p><b>Llamado de vuelta al Mediador (Componente -> Mediador):</b>
     * Una vez calculado el plan de navegacion y sus waypoints, este componente
     * <b>NO</b> contacta a los sensores ni a las comunicaciones. En su lugar, invoca
     * {@code mediator.notificar(this, detalleRuta)}, permitiendo que el mediador
     * orqueste la continuacion de la mision.</p>
     *
     * @param sectorVigilancia nombre del sector de vigilancia asignado por el operador
     * @return reporte tecnico con la configuracion cargada en el piloto automatico
     */
    public String prepararRuta(String sectorVigilancia) {
        String sector = (sectorVigilancia != null && !sectorVigilancia.trim().isEmpty())
                ? sectorVigilancia.trim()
                : "Sector Alfa - Perímetro Base Norte";

        this.rutaActual = sector;
        this.waypointsCalculados.clear();

        String sectorUpper = sector.toUpperCase();

        if (sectorUpper.contains("BRAVO") || sectorUpper.contains("FORESTAL")) {
            // ── CONFIGURACIÓN TÉCNICA: SECTOR BRAVO (FRONTERA FORESTAL) ──
            this.cantidadWaypoints = 14;
            this.altitudCruceroMetros = 80.0;
            this.distanciaEstimadaKm = 11.2;
            this.velocidadCruceroKmH = 35.0;
            this.descripcionTerreno = "Frontera Forestal (relieve montanoso, cobertura vegetal densa y niebla matutina)";

            this.waypointsCalculados.add("WP-B01 [Lat: 4.712, Lon: -74.021, Alt: 80m] - Punto de insercion forestal");
            this.waypointsCalculados.add("WP-B02 [Lat: 4.718, Lon: -74.015, Alt: 80m] - Caniada Rio Seco");
            this.waypointsCalculados.add("WP-B03 [Lat: 4.725, Lon: -74.009, Alt: 80m] - Cresta Cerro Oriental");
            this.waypointsCalculados.add("WP-B04 [Lat: 4.731, Lon: -74.002, Alt: 80m] - Lindero Reserva Natural");
            this.waypointsCalculados.add("WP-B05 [Lat: 4.738, Lon: -73.996, Alt: 80m] - Vaguada Bosque Alto");
            this.waypointsCalculados.add("WP-B06 [Lat: 4.744, Lon: -73.991, Alt: 80m] - Puesto Avanzado Forestal");
            this.waypointsCalculados.add("WP-B07 [Lat: 4.750, Lon: -73.985, Alt: 80m] - Vertice Nororiental");
            this.waypointsCalculados.add("WP-B08 [Lat: 4.743, Lon: -73.990, Alt: 80m] - Retorno perimetro selvatico");
            this.waypointsCalculados.add("WP-B09 [Lat: 4.736, Lon: -73.998, Alt: 80m] - Cruce sendero ecologico");
            this.waypointsCalculados.add("WP-B10 [Lat: 4.729, Lon: -74.005, Alt: 80m] - Quebrada La Esperanza");
            this.waypointsCalculados.add("WP-B11 [Lat: 4.721, Lon: -74.012, Alt: 80m] - Zona amortiguamiento");
            this.waypointsCalculados.add("WP-B12 [Lat: 4.716, Lon: -74.018, Alt: 80m] - Mirador Panoramico");
            this.waypointsCalculados.add("WP-B13 [Lat: 4.713, Lon: -74.020, Alt: 80m] - Aproximacion a base");
            this.waypointsCalculados.add("WP-B14 [Lat: 4.712, Lon: -74.021, Alt: 80m] - Punto de retorno y espera");

        } else if (sectorUpper.contains("CHARLIE") || sectorUpper.contains("CRÍTICAS") || sectorUpper.contains("CRITICAS")) {
            // ── CONFIGURACIÓN TÉCNICA: SECTOR CHARLIE (INSTALACIONES CRÍTICAS) ──
            this.cantidadWaypoints = 6;
            this.altitudCruceroMetros = 150.0;
            this.distanciaEstimadaKm = 4.0;
            this.velocidadCruceroKmH = 50.0;
            this.descripcionTerreno = "Instalaciones Criticas (complejo industrial con zonas de exclusion aerea y geocercas estrictas)";

            this.waypointsCalculados.add("WP-C01 [Lat: 4.685, Lon: -74.095, Alt: 150m] - Vertice Sur Acceso Principal");
            this.waypointsCalculados.add("WP-C02 [Lat: 4.692, Lon: -74.090, Alt: 150m] - Perimetro Este Subestacion Electrica");
            this.waypointsCalculados.add("WP-C03 [Lat: 4.698, Lon: -74.093, Alt: 150m] - Vertice Norte Tanques de Combustible");
            this.waypointsCalculados.add("WP-C04 [Lat: 4.696, Lon: -74.101, Alt: 150m] - Perimetro Oeste Patio de Maniobras");
            this.waypointsCalculados.add("WP-C05 [Lat: 4.689, Lon: -74.103, Alt: 150m] - Vertice Suroeste Garita Blindada");
            this.waypointsCalculados.add("WP-C06 [Lat: 4.685, Lon: -74.095, Alt: 150m] - Cierre de circuito perimetral");

        } else {
            // ── CONFIGURACIÓN TÉCNICA: SECTOR ALFA (PERÍMETRO BASE NORTE) ──
            this.cantidadWaypoints = 8;
            this.altitudCruceroMetros = 120.0;
            this.distanciaEstimadaKm = 6.5;
            this.velocidadCruceroKmH = 45.0;
            this.descripcionTerreno = "Perimetro Base Norte (llanura abierta con linea visual directa y antenas C2)";

            this.waypointsCalculados.add("WP-A01 [Lat: 4.660, Lon: -74.080, Alt: 120m] - Despegue y punto de entrada perimetral");
            this.waypointsCalculados.add("WP-A02 [Lat: 4.668, Lon: -74.075, Alt: 120m] - Vertice Noreste hangar principal");
            this.waypointsCalculados.add("WP-A03 [Lat: 4.675, Lon: -74.078, Alt: 120m] - Torre de control perimetral");
            this.waypointsCalculados.add("WP-A04 [Lat: 4.680, Lon: -74.085, Alt: 120m] - Vertice Norte limite territorial");
            this.waypointsCalculados.add("WP-A05 [Lat: 4.676, Lon: -74.092, Alt: 120m] - Garita de acceso Noroccidental");
            this.waypointsCalculados.add("WP-A06 [Lat: 4.669, Lon: -74.095, Alt: 120m] - Valla de seguridad perimetro Oeste");
            this.waypointsCalculados.add("WP-A07 [Lat: 4.662, Lon: -74.088, Alt: 120m] - Vertice Suroeste helipuerto");
            this.waypointsCalculados.add("WP-A08 [Lat: 4.660, Lon: -74.080, Alt: 120m] - Retorno al punto inicial");
        }

        String detalleRuta = String.format(
                "RUTA_PREPARADA: Plan de vuelo perimetral trazado para '%s'.\n" +
                "   • Parametros tecnicos: %d waypoints | Altitud segura: %.1f m | Recorrido: %.1f km | Velocidad: %.1f km/h.\n" +
                "   • Entorno topografico: %s.\n" +
                "   • Geocercas cargadas en piloto automatico: [%s ... %s].",
                this.rutaActual,
                this.cantidadWaypoints,
                this.altitudCruceroMetros,
                this.distanciaEstimadaKm,
                this.velocidadCruceroKmH,
                this.descripcionTerreno,
                this.waypointsCalculados.get(0),
                this.waypointsCalculados.get(this.waypointsCalculados.size() - 1)
        );

        // ── COMUNICACIÓN COMPONENTE -> MEDIADOR ──────────────────────────────────
        // El componente notifica al mediador central que el plan de vuelo esta listo.
        // No conoce ni se comunica con ningun otro componente.
        if (this.mediator != null) {
            this.mediator.notificar(this, detalleRuta);
        }

        return detalleRuta;
    }
}
