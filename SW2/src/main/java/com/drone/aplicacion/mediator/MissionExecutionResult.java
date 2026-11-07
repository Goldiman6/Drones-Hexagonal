package com.drone.aplicacion.mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Objeto de transferencia que encapsula el resultado y la bitacora paso a paso
 * generada por la coordinacion entre los componentes a traves del mediador.
 */
public class MissionExecutionResult {

    private final boolean exitoso;
    private final String sector;
    private final List<String> pasos;
    private final String mensajeFinal;

    public MissionExecutionResult(boolean exitoso, String sector, List<String> pasos, String mensajeFinal) {
        this.exitoso = exitoso;
        this.sector = sector;
        this.pasos = pasos != null ? new ArrayList<>(pasos) : new ArrayList<>();
        this.mensajeFinal = mensajeFinal;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getSector() {
        return sector;
    }

    public List<String> getPasos() {
        return Collections.unmodifiableList(pasos);
    }

    public String getMensajeFinal() {
        return mensajeFinal;
    }
}
