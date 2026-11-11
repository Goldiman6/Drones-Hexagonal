package com.drone.dominio.state;

/**
 * Estado concreto EN_ESPERA del patron State.
 *
 * <p>Representa al dron encendido, en tierra y preparado para operar.
 * Recibe el contexto {@link DroneWrapper} y permite transicionar hacia {@link FlyingState}
 * al despegar, o hacia {@link OffState} al apagarse.</p>
 */
public class ReadyState implements DroneState {

    @Override
    public String getNombre() {
        return "EN_ESPERA";
    }

    @Override
    public void apagar(DroneWrapper context) {
        // Transicion valida: EN_ESPERA → APAGADO; actualiza el estado en el contexto
        context.setEstado(new OffState());
    }

    @Override
    public void despegar(DroneWrapper context) {
        // Transicion valida: EN_ESPERA → VOLANDO; actualiza el estado en el contexto
        context.setEstado(new FlyingState());
    }

    // ── Permisos polimorficos de actividad ────────────────────────────────────

    @Override
    public boolean permiteApagar() {
        return true;
    }

    @Override
    public boolean permiteDespegar() {
        return true;
    }

    @Override
    public boolean estaOperativoParaMision() {
        return true;
    }
}
