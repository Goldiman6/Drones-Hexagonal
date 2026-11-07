package com.drone.aplicacion.mediator;

/**
 * Interfaz del patron de diseño GoF Mediator.
 *
 * <p>Define el contrato de comunicacion centralizado entre los componentes
 * encargados de gestionar una operacion de drones (bateria, comunicacion,
 * navegacion y sensores).</p>
 *
 * <p><b>Principio de desacoplamiento:</b> Los componentes (Colleagues) no se comunican
 * directamente entre si; en su lugar, notifican a esta interfaz cualquier evento relevante
 * mediante {@link #notificar(Object, String)}, permitiendo que el mediador concreto
 * orqueste la logica de control y flujo de la mision sin acoplar los componentes entre si.</p>
 */
public interface DroneMediator {

    /**
     * Metodo invocado por cualquier componente colega para notificar al mediador
     * un cambio de estado, resultado de telemetria o evento completado.
     *
     * <p><b>Flujo Componente -> Mediador:</b> El componente emisor envia su propia referencia
     * ({@code this}) y una cadena descriptiva con el evento o veredicto tecnico.</p>
     *
     * @param remitente el componente que genera la notificacion (ej. {@code BatterySystem}, {@code NavigationSystem})
     * @param evento    identificador o descripcion del evento ocurrido
     */
    void notificar(Object remitente, String evento);
}
