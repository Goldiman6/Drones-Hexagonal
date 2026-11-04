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
    public void encender(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta VOLANDO. No puede encenderse en vuelo."
        );
    }

    @Override
    public void apagar(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta VOLANDO. Apagarlo en vuelo representa un riesgo critico de seguridad. Debe aterrizar primero."
        );
    }

    @Override
    public void despegar(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' ya esta VOLANDO. No puede despegar cuando ya se encuentra en vuelo."
        );
    }

    @Override
    public void iniciarAterrizaje(DroneWrapper context) {
        // Transicion valida: VOLANDO → ATERRIZANDO; actualiza el estado en el contexto
        context.setEstado(new LandingState());
    }

    @Override
    public void finalizarAterrizaje(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta VOLANDO. Debe iniciar el proceso de aterrizaje antes de finalizarlo."
        );
    }

    // ── Permisos polimorficos de actividad ────────────────────────────────────

    @Override
    public boolean permiteEncender() {
        return false;
    }

    @Override
    public boolean permiteApagar() {
        return false;
    }

    @Override
    public boolean permiteDespegar() {
        return false;
    }

    @Override
    public boolean permiteIniciarAterrizaje() {
        return true;
    }

    @Override
    public boolean permiteFinalizarAterrizaje() {
        return false;
    }

    @Override
    public boolean estaOperativoParaMision() {
        return true;
    }
}
