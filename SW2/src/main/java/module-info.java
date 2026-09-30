module co.edu.poli.SW2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    // Paquete principal
    opens co.edu.poli.SW2 to javafx.fxml;
    exports co.edu.poli.SW2;

    // Dominio
    opens com.drone.dominio.modelo to javafx.base, com.fasterxml.jackson.databind;
    exports com.drone.dominio.modelo;

    // Aplicacion
    exports com.drone.aplicacion.puerto.entrada;
    exports com.drone.aplicacion.puerto.salida;
    exports com.drone.aplicacion.servicio;

    // Infraestructura
    opens com.drone.infraestructura.ui to javafx.fxml;
    opens com.drone.infraestructura.persistencia to com.fasterxml.jackson.databind;
    exports com.drone.infraestructura.ui;
    opens com.drone.infraestructura.vista to javafx.fxml;
    exports com.drone.infraestructura.vista;
    exports com.drone.infraestructura.persistencia;
}