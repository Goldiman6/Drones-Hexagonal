package com.drone.infraestructura.persistencia;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Patron Singleton aplicado a la conexion de Base de Datos.
 *
 * Garantiza que durante toda la ejecucion del programa exista una UNICA
 * instancia de esta clase y, por lo tanto, una UNICA conexion (Connection)
 * compartida con PostgreSQL. Esto optimiza el uso de recursos de memoria
 * y evita saturar el servidor de base de datos con multiples conexiones.
 *
 * Las credenciales de conexion se leen desde el archivo externo:
 * src/main/resources/database.properties
 * de esta forma no quedan expuestas en el codigo fuente.
 */
public class Singleton {

    private static final Logger LOGGER = Logger.getLogger(Singleton.class.getName());

    static {
        // Redirige los logs a System.out (blanco en consola) en lugar de System.err (rojo)
        LOGGER.setUseParentHandlers(false);
        java.util.logging.StreamHandler stdoutHandler = new java.util.logging.StreamHandler(
            System.out,
            new java.util.logging.Formatter() {
                @Override
                public String format(java.util.logging.LogRecord record) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[").append(record.getLevel()).append("] ")
                      .append(record.getMessage())
                      .append(System.lineSeparator());
                    if (record.getThrown() != null) {
                        java.io.StringWriter sw = new java.io.StringWriter();
                        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                        record.getThrown().printStackTrace(pw);
                        sb.append(sw);
                    }
                    return sb.toString();
                }
            }
        ) {
            @Override
            public synchronized void publish(java.util.logging.LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        LOGGER.addHandler(stdoutHandler);
    }

    /** La unica instancia de esta clase. Es estatica para que sea global. */
    private static Singleton instancia;

    /** El objeto de conexion JDBC compartido por toda la aplicacion. */
    private Connection conexion;

    // Credenciales cargadas desde database.properties (no estan en el codigo fuente)
    private String url;
    private String usuario;
    private String clave;

    /**
     * Constructor privado.
     * Al ser privado, ningun otro objeto puede hacer 'new Singleton()'.
     * Solo esta clase puede instanciarse a si misma, garantizando la unicidad.
     */
    private Singleton() {
        cargarPropiedades();
        try {
            // Intentamos conectar con las credenciales cargadas
            conexion = DriverManager.getConnection(url, usuario, clave);
            LOGGER.info("Conexion a base de datos (PostgreSQL) establecida exitosamente.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar a la base de datos. Verifica database.properties.", e);
        }
    }

    /**
     * Lee las credenciales de conexion desde el archivo database.properties
     * ubicado en src/main/resources. Este archivo debe contener:
     *   db.url, db.user, db.password
     */
    private void cargarPropiedades() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                LOGGER.warning("No se pudo encontrar database.properties en resources.");
                return;
            }
            props.load(input);
            this.url      = props.getProperty("db.url");
            this.usuario  = props.getProperty("db.user");
            this.clave    = props.getProperty("db.password");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar el archivo database.properties", ex);
        }
    }

    /**
     * Metodo de acceso global a la instancia Singleton.
     * Si la instancia no existe aun, la crea. Si ya existe, retorna la misma.
     *
     * @return La unica instancia de Singleton.
     */
    public static Singleton getInstance() {
        if (instancia == null) {
            instancia = new Singleton();
        }
        return instancia;
    }

    /**
     * Retorna el objeto de conexion JDBC activo hacia PostgreSQL.
     * Todos los DAOs deben usar esta conexion en lugar de crear una propia.
     *
     * @return Connection activo, o null si la conexion fallo al iniciar.
     */
    public Connection getConnection() {
        return conexion;
    }
}
