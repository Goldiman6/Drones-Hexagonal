package com.drone.infraestructura.persistencia;

import com.drone.dominio.modelo.Agricultura;
import com.drone.dominio.modelo.Drone;
import com.drone.dominio.modelo.Vigilancia;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresDroneRepository implements DroneRepository {

    private static final Logger LOGGER = Logger.getLogger(PostgresDroneRepository.class.getName());

    @Override
    public void guardar(Drone drone) throws Exception {
        Connection conn = Singleton.getInstance().getConnection();
        if (conn == null) throw new Exception("Error crítico: No hay conexión a la base de datos.");

        String insertDrone = "INSERT INTO drones (id, serial, modelo, fabricante, peso) VALUES (?, ?, ?, ?, ?)";
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertDrone)) {
                ps.setString(1, drone.getId());
                ps.setString(2, drone.getSerial());
                ps.setString(3, drone.getModelo());
                ps.setString(4, drone.getFabricante());
                ps.setDouble(5, drone.getPeso());
                ps.executeUpdate();
            }

            persistirSubtipo(conn, drone, false);
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn, "guardar");
            manejarErrorSql(e);
        } finally {
            restoreAutoCommitQuietly(conn, "guardar");
        }
    }

    @Override
    public List<Drone> listar() throws Exception {
        Connection conn = Singleton.getInstance().getConnection();
        if (conn == null) throw new Exception("Error crítico: No hay conexión a la base de datos.");
        
        List<Drone> lista = new ArrayList<>();
        String sql = "SELECT d.id, d.serial, d.modelo, d.fabricante, d.peso, " +
                     "a.capacidad_tanque, v.deteccion_termica " +
                     "FROM drones d " +
                     "LEFT JOIN drones_agricultura a ON d.id = a.id_drone " +
                     "LEFT JOIN drones_vigilancia v ON d.id = v.id_drone";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String serial = rs.getString("serial");
                String modelo = rs.getString("modelo");
                String fabricante = rs.getString("fabricante");
                double peso = rs.getDouble("peso");

                double capacidad = rs.getDouble("capacidad_tanque");
                boolean esAgricultura = !rs.wasNull();

                if (esAgricultura) {
                    lista.add(new Agricultura(id, serial, modelo, fabricante, peso, capacidad));
                } else {
                    boolean termica = rs.getBoolean("deteccion_termica");
                    lista.add(new Vigilancia(id, serial, modelo, fabricante, peso, termica));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al consultar la base de datos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void actualizar(Drone drone) throws Exception {
        Connection conn = Singleton.getInstance().getConnection();
        if (conn == null) throw new Exception("Error crítico: No hay conexión a la base de datos.");

        String updateDrone = "UPDATE drones SET serial=?, modelo=?, fabricante=?, peso=? WHERE id=?";
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateDrone)) {
                ps.setString(1, drone.getSerial());
                ps.setString(2, drone.getModelo());
                ps.setString(3, drone.getFabricante());
                ps.setDouble(4, drone.getPeso());
                ps.setString(5, drone.getId());
                ps.executeUpdate();
            }

            persistirSubtipo(conn, drone, true);
            conn.commit();
        } catch (SQLException e) {
            rollbackQuietly(conn, "actualizar");
            manejarErrorSql(e);
        } finally {
            restoreAutoCommitQuietly(conn, "actualizar");
        }
    }

    @Override
    public void eliminar(String id) throws Exception {
        Connection conn = Singleton.getInstance().getConnection();
        if (conn == null) throw new Exception("Error crítico: No hay conexión a la base de datos.");

        String sql = "DELETE FROM drones WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) throw new Exception("El dron con ID " + id + " no existe o ya fue eliminado.");
        } catch (SQLException e) {
            throw new Exception("Error al eliminar el dron: " + e.getMessage());
        }
    }

    private void persistirSubtipo(Connection conn, Drone drone, boolean esActualizacion) throws SQLException {
        if (drone instanceof Agricultura) {
            Agricultura agri = (Agricultura) drone;
            String sql = esActualizacion
                    ? "UPDATE drones_agricultura SET capacidad_tanque=? WHERE id_drone=?"
                    : "INSERT INTO drones_agricultura (id_drone, capacidad_tanque) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (esActualizacion) {
                    ps.setDouble(1, agri.getCapacidadTanque());
                    ps.setString(2, drone.getId());
                } else {
                    ps.setString(1, drone.getId());
                    ps.setDouble(2, agri.getCapacidadTanque());
                }
                ps.executeUpdate();
            }
        } else if (drone instanceof Vigilancia) {
            Vigilancia vigi = (Vigilancia) drone;
            String sql = esActualizacion
                    ? "UPDATE drones_vigilancia SET deteccion_termica=? WHERE id_drone=?"
                    : "INSERT INTO drones_vigilancia (id_drone, deteccion_termica) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (esActualizacion) {
                    ps.setBoolean(1, vigi.isDeteccionTermica());
                    ps.setString(2, drone.getId());
                } else {
                    ps.setString(1, drone.getId());
                    ps.setBoolean(2, vigi.isDeteccionTermica());
                }
                ps.executeUpdate();
            }
        }
    }

    private void rollbackQuietly(Connection conn, String operacion) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Error al realizar rollback en " + operacion + "()", ex);
            }
        }
    }

    private void restoreAutoCommitQuietly(Connection conn, String operacion) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Error al restaurar autoCommit en " + operacion + "()", ex);
            }
        }
    }
    
    // Método auxiliar para evitar repetir código al manejar excepciones de Constraint Violation
    private void manejarErrorSql(SQLException e) throws Exception {
        if ("23505".equals(e.getSQLState())) {
            if (e.getMessage() != null && e.getMessage().contains("serial")) {
                throw new Exception("El número de Serial ingresado ya le pertenece a otro dron.");
            } else if (e.getMessage() != null && (e.getMessage().contains("id") || e.getMessage().contains("pkey"))) {
                throw new Exception("El ID ingresado ya existe en la base de datos.");
            } else {
                throw new Exception("Registro duplicado detectado.");
            }
        }
        throw new Exception("Error en base de datos: " + e.getMessage());
    }
}
