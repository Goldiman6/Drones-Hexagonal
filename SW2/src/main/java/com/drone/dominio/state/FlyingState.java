package com.drone.dominio.state;

/**
 * Estado concreto VOLANDO del patron State.
 *
 * <p>Representa al dron en vuelo activo.
 * Recibe el contexto {@link DroneWrapper} y permite unicamente iniciar el proceso
 * de aterrizaje, transicionando el contexto hacia {@link LandingState}.</p>
 */
public class FlyingState implements DroneState {

    @Override
    public String getNombre() {
        return "VOLANDO";
    }

    @Override
    public void iniciarAterrizaje(DroneWrapper context) {
        // Transicion valida: VOLANDO → ATERRIZANDO; actualiza el estado en el contexto
        context.setEstado(new LandingState());
    }

    // ── Permisos polimorficos de actividad ────────────────────────────────────

    @Override
    public boolean permiteIniciarAterrizaje() {
        return true;
    }

    @Override
    public boolean estaOperativoParaMision() {
        return true;
    }
}
