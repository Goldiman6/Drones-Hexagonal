package com.drone.dominio.state;

/**
 * Estado concreto ATERRIZANDO del patron State.
 *
 * <p>Representa al dron ejecutando la maniobra de descenso y aterrizaje.
 * Recibe el contexto {@link DroneWrapper} y permite unicamente finalizar el proceso,
 * transicionando el contexto hacia {@link ReadyState} (EN_ESPERA).</p>
 */
public class LandingState implements DroneState {

    @Override
    public String getNombre() {
        return "ATERRIZANDO";
    }

    @Override
    public void finalizarAterrizaje(DroneWrapper context) {
        // Transicion valida: ATERRIZANDO → EN_ESPERA; actualiza el estado en el contexto
        context.setEstado(new ReadyState());
    }

    // ── Permisos polimorficos de actividad ────────────────────────────────────

    @Override
    public boolean permiteFinalizarAterrizaje() {
        return true;
    }
}
