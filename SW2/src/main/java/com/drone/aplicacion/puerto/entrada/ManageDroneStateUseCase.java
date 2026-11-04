package com.drone.aplicacion.puerto.entrada;

import com.drone.dominio.state.DroneWrapper;

/**
 * Puerto de entrada del patron hexagonal para la gestion del estado
 * operativo de los drones mediante el patron State.
 *
 * <p>Define el contrato que el adaptador de entrada (controlador de UI)
 * debe invocar para solicitar transiciones de estado, sin conocer los
 * detalles de la logica de dominio.</p>
 *
 * <p>Recibe un {@link DroneWrapper} (Contexto del patron State) en lugar del
 * {@code Drone} de dominio, ya que el estado operativo es transitorio y vive
 * en el wrapper, no en la entidad de datos.</p>
 */
public interface ManageDroneStateUseCase {

    /**
     * Ejecuta una transicion de estado sobre el contexto del dron indicado.
     *
     * <p>Operaciones validas:</p>
     * <ul>
     *   <li>{@code "ENCENDER"}            — APAGADO → EN_ESPERA</li>
     *   <li>{@code "APAGAR"}              — EN_ESPERA → APAGADO</li>
     *   <li>{@code "DESPEGAR"}            — EN_ESPERA → VOLANDO</li>
     *   <li>{@code "INICIAR_ATERRIZAJE"}  — VOLANDO → ATERRIZANDO</li>
     *   <li>{@code "FINALIZAR_ATERRIZAJE"}— ATERRIZANDO → EN_ESPERA</li>
     * </ul>
     *
     * @param context   contexto del patron State (DroneWrapper con estado actual)
     * @param operacion identificador de la operacion de transicion
     * @return nombre del nuevo estado tras la transicion exitosa
     * @throws IllegalStateException     si la transicion no esta permitida desde el estado actual
     * @throws IllegalArgumentException  si la operacion recibida no es reconocida
     */
    String ejecutarTransicion(DroneWrapper context, String operacion);
}
