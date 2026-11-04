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
    public void encender(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta ATERRIZANDO. Debe finalizar el aterrizaje antes de ejecutar otra operacion."
        );
    }

    @Override
    public void apagar(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta ATERRIZANDO. No puede apagarse durante el descenso."
        );
    }

    @Override
    public void despegar(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta ATERRIZANDO. No puede iniciar un nuevo vuelo hasta completar el aterrizaje."
        );
    }

    @Override
    public void iniciarAterrizaje(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' ya esta ejecutando un proceso de ATERRIZAJE activo."
        );
    }

    @Override
    public void finalizarAterrizaje(DroneWrapper context) {
        // Transicion valida: ATERRIZANDO → EN_ESPERA; actualiza el estado en el contexto
        context.setEstado(new ReadyState());
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
        return false;
    }

    @Override
    public boolean permiteFinalizarAterrizaje() {
        return true;
    }

    @Override
    public boolean estaOperativoParaMision() {
        return false;
    }
}
