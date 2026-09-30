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

public class PostgresDroneRepository implements DroneRepository {

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

            if (drone instanceof Agricultura) {
                String insertAgri = "INSERT INTO drones_agricultura (id_drone, capacidad_tanque) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertAgri)) {
                    ps.setString(1, drone.getId());
                    ps.setDouble(2, ((Agricultura) drone).getCapacidadTanque());
                    ps.executeUpdate();
                }
            } else if (drone instanceof Vigilancia) {
                String insertVigi = "INSERT INTO drones_vigilancia (id_drone, deteccion_termica) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertVigi)) {
                    ps.setString(1, drone.getId());
                    ps.setBoolean(2, ((Vigilancia) drone).isDeteccionTermica());
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            manejarErrorSql(e);
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
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

                if (rs.getObject("capacidad_tanque") != null) {
                    double capacidad = rs.getDouble("capacidad_tanque");
                    lista.add(new Agricultura(id, serial, modelo, fabricante, peso, capacidad));
                } else if (rs.getObject("deteccion_termica") != null) {
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
                int filas = ps.executeUpdate();
                if (filas == 0) throw new Exception("El dron con ID " + drone.getId() + " no existe.");
            }

            if (drone instanceof Agricultura) {
                String updateAgri = "UPDATE drones_agricultura SET capacidad_tanque=? WHERE id_drone=?";
                try (PreparedStatement ps = conn.prepareStatement(updateAgri)) {
                    ps.setDouble(1, ((Agricultura) drone).getCapacidadTanque());
                    ps.setString(2, drone.getId());
                    ps.executeUpdate();
                }
            } else if (drone instanceof Vigilancia) {
                String updateVigi = "UPDATE drones_vigilancia SET deteccion_termica=? WHERE id_drone=?";
                try (PreparedStatement ps = conn.prepareStatement(updateVigi)) {
                    ps.setBoolean(1, ((Vigilancia) drone).isDeteccionTermica());
                    ps.setString(2, drone.getId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            manejarErrorSql(e);
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
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