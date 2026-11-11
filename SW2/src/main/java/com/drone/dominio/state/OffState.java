package com.drone.dominio.state;

/**
 * Estado concreto APAGADO del patron State.
 *
 * <p>Representa al dron cuando se encuentra sin energia y en tierra.
 * Recibe el contexto {@link DroneWrapper} y valida que la UNICA actividad
 * permitida sea encenderse, transicionando el contexto hacia {@link ReadyState}.</p>
 */
public class OffState implements DroneState {

    @Override
    public String getNombre() {
        return "APAGADO";
    }

    @Override
    public void encender(DroneWrapper context) {
        // Transicion valida: APAGADO → EN_ESPERA; actualiza el estado en el contexto
        context.setEstado(new ReadyState());
    }

    // ── Permisos polimorficos de actividad ────────────────────────────────────

    @Override
    public boolean permiteEncender() {
        return true;
    }
}
