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

    @Override
    public void apagar(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' ya se encuentra APAGADO."
        );
    }

    @Override
    public void despegar(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta APAGADO. Debe encenderse antes de poder despegar."
        );
    }

    @Override
    public void iniciarAterrizaje(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta APAGADO en tierra. No puede iniciar aterrizaje."
        );
    }

    @Override
    public void finalizarAterrizaje(DroneWrapper context) {
        throw new IllegalStateException(
            "Transicion invalida: El dron '" + context.getId() + "' esta APAGADO. No hay proceso de aterrizaje activo que finalizar."
        );
    }

    // ── Permisos polimorficos de actividad ────────────────────────────────────

    @Override
    public boolean permiteEncender() {
        return true;
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
        return false;
    }

    @Override
    public boolean estaOperativoParaMision() {
        return false;
    }
}
