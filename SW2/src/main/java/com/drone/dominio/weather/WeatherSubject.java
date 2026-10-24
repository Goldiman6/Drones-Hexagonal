package com.drone.dominio.weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sujeto (Subject) del patron Observer para el monitoreo climatico de drones.
 *
 * <p>Gestiona la lista de observadores suscritos ({@link WeatherObserver}) en
 * memoria durante la sesion de la aplicacion. Cuando se obtienen datos
 * meteorologicos actualizados, notifica automaticamente a todos los drones
 * suscritos invocando su metodo {@link WeatherObserver#actualizarClima(Weather)}.</p>
 *
 * <p><b>Rol en el patron Observer:</b> Subject / Observable.</p>
 *
 * <p><b>Importante:</b> Las suscripciones NO se persisten en base de datos.
 * Cada vez que la aplicacion se cierra y se vuelve a abrir, la lista de
 * observadores comienza vacia. Esto es intencional por diseno.</p>
 *
 * <p>Los metodos de esta clase son sincronizados ({@code synchronized}) para
 * garantizar seguridad en entornos multihilo.</p>
 *
 * @author SW2
 * @version 1.0
 * @see WeatherObserver
 * @see Weather
 */
public class WeatherSubject {

    /** Lista interna de observadores suscritos al monitoreo climatico. */
    private final List<WeatherObserver> observadores = new ArrayList<>();

    /**
     * Suscribe un nuevo observador al sistema de monitoreo climatico.
     *
     * <p>No permite suscripciones duplicadas: si ya existe un observador con
     * el mismo identificador, el metodo retorna {@code false} sin modificar la lista.</p>
     *
     * @param observer observador a suscribir; no debe ser nulo ni tener identificador vacio
     * @return {@code true} si la suscripcion fue exitosa;
     *         {@code false} si el observador ya estaba suscrito o es invalido
     */
    public synchronized boolean suscribir(WeatherObserver observer) {
        if (observer == null || observer.getIdentificador() == null
                || observer.getIdentificador().trim().isEmpty()) {
            return false;
        }
        for (WeatherObserver o : observadores) {
            if (o.getIdentificador().equalsIgnoreCase(observer.getIdentificador())) {
                return false;
            }
        }
        return observadores.add(observer);
    }

    /**
     * Elimina un observador de la lista de suscritos mediante su identificador.
     *
     * @param identificador ID del observador a remover; por ejemplo, el ID del dron
     * @return {@code true} si el observador fue encontrado y eliminado;
     *         {@code false} si no existia en la lista
     */
    public synchronized boolean desuscribir(String identificador) {
        if (identificador == null) return false;
        return observadores.removeIf(
                o -> o.getIdentificador().equalsIgnoreCase(identificador.trim()));
    }

    /**
     * Verifica si un observador con el identificador dado se encuentra actualmente suscrito.
     *
     * @param identificador ID del observador a consultar
     * @return {@code true} si el observador esta suscrito; {@code false} en caso contrario
     */
    public synchronized boolean estaSuscrito(String identificador) {
        if (identificador == null) return false;
        return observadores.stream()
                .anyMatch(o -> o.getIdentificador().equalsIgnoreCase(identificador.trim()));
    }

    /**
     * Notifica a todos los observadores suscritos con los nuevos datos meteorologicos.
     *
     * <p>Invoca {@link WeatherObserver#actualizarClima(Weather)} en cada observador
     * registrado. Este metodo es llamado por el {@code ConsultarClimaServicio}
     * luego de obtener los datos de la API y ejecutar la cadena de validacion.</p>
     *
     * @param weather objeto con los datos meteorologicos actualizados a difundir
     */
    public synchronized void notificar(Weather weather) {
        for (WeatherObserver o : observadores) {
            o.actualizarClima(weather);
        }
    }

    /**
     * Retorna una copia inmutable de la lista de observadores suscritos.
     *
     * <p>Se retorna una copia para proteger la lista interna de modificaciones externas.</p>
     *
     * @return lista no modificable de observadores actualmente suscritos
     */
    public synchronized List<WeatherObserver> getObservadores() {
        return Collections.unmodifiableList(new ArrayList<>(observadores));
    }

    /**
     * Retorna la lista de identificadores de todos los observadores suscritos.
     *
     * <p>Util para mostrar en la interfaz de usuario el estado actual de las suscripciones.</p>
     *
     * @return lista con los identificadores de los observadores suscritos;
     *         lista vacia si no hay ninguno
     */
    public synchronized List<String> getIdentificadoresSuscritos() {
        List<String> ids = new ArrayList<>();
        for (WeatherObserver o : observadores) {
            ids.add(o.getIdentificador());
        }
        return ids;
    }
}
