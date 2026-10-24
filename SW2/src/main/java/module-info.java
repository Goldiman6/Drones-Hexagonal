module co.edu.poli.SW2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires java.net.http;

    // Paquete principal
    opens co.edu.poli.SW2 to javafx.fxml;
    exports co.edu.poli.SW2;

    // Dominio
    opens com.drone.dominio.modelo to javafx.base, com.fasterxml.jackson.databind;
    exports com.drone.dominio.modelo;
    opens com.drone.dominio.weather to javafx.base, com.fasterxml.jackson.databind;
    exports com.drone.dominio.weather;
    exports com.drone.dominio.weather.chain;

    // Aplicacion
    exports com.drone.aplicacion.puerto.entrada;
    exports com.drone.aplicacion.puerto.salida;
    exports com.drone.aplicacion.servicio;
    exports com.drone.aplicacion.weather;
    exports com.drone.aplicacion.weather.observer;

    // Infraestructura
    opens com.drone.infraestructura.ui to javafx.fxml;
    opens com.drone.infraestructura.persistencia to com.fasterxml.jackson.databind;
    exports com.drone.infraestructura.ui;
    opens com.drone.infraestructura.vista to javafx.fxml;
    exports com.drone.infraestructura.vista;
    exports com.drone.infraestructura.persistencia;
    exports com.drone.infraestructura.weather;
}
