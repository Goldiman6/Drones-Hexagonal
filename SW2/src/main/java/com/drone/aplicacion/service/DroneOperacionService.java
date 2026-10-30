package com.drone.aplicacion.service;

import com.drone.dominio.modelo.Agricultura;
import com.drone.dominio.modelo.Drone;
import com.drone.dominio.modelo.Vigilancia;

/**
 * Receptor (Receiver) y Wrapper del patron Command.
 *
 * <p>Envuelve a una entidad {@link Drone} existente sin modificar su clase original,
 * proporcionando la implementacion tecnica y logica de ejecucion para cada
 * una de las tareas u operaciones que el dron puede realizar en una mision.</p>
 */
public class DroneOperacionService {

    private final Drone drone;

    /**
     * Construye el servicio receptor envolviendo al dron especificado.
     *
     * @param drone dron seleccionado para la operacion
     */
    public DroneOperacionService(Drone drone) {
        this.drone = drone;
    }

    public Drone getDrone() {
        return drone;
    }

    /**
     * Ejecuta la tarea tecnica de captura fotografica aerea.
     *
     * @return reporte de la fotografia tomada
     */
    public String tomarFotografia() {
        String id = drone != null ? drone.getId() : "N/A";
        String modelo = drone != null ? drone.getModelo() : "Generico";
        return String.format(
                "Captura aerea 4K (3840x2160) realizada por dron %s (%s) a 120m de altura con angulo de 45 grados. Imagen georreferenciada guardada en almacenamiento local.",
                id, modelo
        );
    }

    /**
     * Ejecuta la tarea tecnica de escaneo de zona, adaptando el resultado
     * segun la especialidad del dron (Agricultura o Vigilancia).
     *
     * @return reporte detallado del escaneo realizado
     */
    public String escanearZona() {
        String id = drone != null ? drone.getId() : "N/A";
        String modelo = drone != null ? drone.getModelo() : "Generico";

        if (drone instanceof Agricultura) {
            return String.format(
                    "Escaneo multiespectral de cultivo completado por dron %s (%s). Indice de vegetacion NDVI: 0.78 (Cultivo saludable y vigoroso). Capacidad restante de tanque: %.1f L.",
                    id, modelo, ((Agricultura) drone).getCapacidadTanque()
            );
        } else if (drone instanceof Vigilancia) {
            boolean termica = ((Vigilancia) drone).isDeteccionTermica();
            return String.format(
                    "Barrido perimetral LiDAR 360 grados por dron %s (%s). Deteccion termica activa: %s. Cero anomalias o intrusiones detectadas.",
                    id, modelo, (termica ? "Si" : "No")
            );
        }

        return String.format(
                "Escaneo topografico de superficie completado por dron %s (%s). Malla de elevacion generada exitosamente.",
                id, modelo
        );
    }

    /**
     * Ejecuta la lectura del sensor termico y de telemetria ambiental.
     *
     * @return reporte de temperatura ambiental y de los motores
     */
    public String medirTemperatura() {
        String id = drone != null ? drone.getId() : "N/A";
        String modelo = drone != null ? drone.getModelo() : "Generico";
        return String.format(
                "Telemetria termica por dron %s (%s): Sensor infrarrojo calibrado. Temperatura ambiente: 18.7 C | Temperatura de motores: 36.2 C (Rango seguro y optimo).",
                id, modelo
        );
    }
}
