package com.drone.dominio.modelo;

import java.io.Serializable;

/**
 * Entidad abstracta base del dominio que representa un Vehículo Aéreo No Tripulado.
 *
 * <p>Esta clase es un modelo de dominio puro: solo contiene los atributos de identidad
 * y datos del dron que se persisten en base de datos. No contiene ninguna logica
 * de estado operativo (patron State).</p>
 *
 * <p>La gestion del ciclo operativo (APAGADO, EN_ESPERA, VOLANDO, ATERRIZANDO) se
 * delega al {@code DroneWrapper} (contexto del patron State), que envuelve a esta
 * entidad en memoria sin modificarla.</p>
 */
public abstract class Drone implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String serial;
    private String modelo;
    private String fabricante;
    private double peso;

    public Drone() {}

    public Drone(String id, String serial, String modelo, String fabricante, double peso) {
        this.id = id;
        this.serial = serial;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.peso = peso;
    }

    // ── Getters / Setters de identidad ───────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
}
