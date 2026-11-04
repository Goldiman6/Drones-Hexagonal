package com.drone.aplicacion.service;

import com.drone.aplicacion.puerto.entrada.ManageDroneStateUseCase;
import com.drone.dominio.state.DroneWrapper;

/**
 * Implementacion del puerto de entrada {@link ManageDroneStateUseCase}.
 *
 * <p>Actua como servicio de aplicacion que recibe la solicitud de transicion
 * de estado desde el adaptador de entrada (UI), la traduce a una llamada
 * en el contexto del patron State ({@link DroneWrapper}) y retorna el nombre
 * del estado resultante.</p>
 */
public class DroneStateService implements ManageDroneStateUseCase {

    /**
     * Ejecuta la transicion de estado indicada sobre el contexto del dron y retorna
     * el nombre del nuevo estado resultante.
     *
     * @param context   contexto del patron State; contiene el DroneWrapper con estado actual
     * @param operacion identificador de la operacion ("ENCENDER", "APAGAR",
     *                  "DESPEGAR", "INICIAR_ATERRIZAJE", "FINALIZAR_ATERRIZAJE")
     * @return nombre del estado al que transiciono el dron
     * @throws IllegalStateException    si la transicion no esta permitida
     * @throws IllegalArgumentException si la operacion es desconocida
     */
    @Override
    public String ejecutarTransicion(DroneWrapper context, String operacion) {
        if (context == null) {
            throw new IllegalArgumentException("El contexto del dron no puede ser nulo para ejecutar una transicion de estado.");
        }
        if (operacion == null || operacion.trim().isEmpty()) {
            throw new IllegalArgumentException("La operacion de transicion no puede estar vacia.");
        }

        switch (operacion) {
            case "ENCENDER":
                context.encender();
                break;
            case "APAGAR":
                context.apagar();
                break;
            case "DESPEGAR":
                context.despegar();
                break;
            case "INICIAR_ATERRIZAJE":
                context.iniciarAterrizaje();
                break;
            case "FINALIZAR_ATERRIZAJE":
                context.finalizarAterrizaje();
                break;
            default:
                throw new IllegalArgumentException(
                    "Operacion de estado desconocida: '" + operacion + "'. " +
                    "Las operaciones validas son: ENCENDER, APAGAR, DESPEGAR, " +
                    "INICIAR_ATERRIZAJE, FINALIZAR_ATERRIZAJE."
                );
        }

        return context.getNombreEstado();
    }
}
