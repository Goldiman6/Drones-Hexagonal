package co.edu.poli.SW2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.drone.aplicacion.puerto.entrada.ActualizarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.CrearDroneUseCase;
import com.drone.aplicacion.puerto.entrada.EliminarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ListarDronesUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import com.drone.aplicacion.servicio.ActualizarDroneServicio;
import com.drone.aplicacion.servicio.CrearDroneServicio;
import com.drone.aplicacion.servicio.EliminarDroneServicio;
import com.drone.aplicacion.servicio.ListarDronesServicio;
import com.drone.infraestructura.persistencia.PostgresDroneRepository;
import com.drone.infraestructura.ui.DroneController;
import com.drone.infraestructura.vista.DroneView;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Instanciar Adaptador de Salida (Infraestructura -> Persistencia)
        // El Singleton de BD es utilizado internamente por PostgresDroneRepository
        DroneRepository droneRepository = new PostgresDroneRepository();

        // 2. Instanciar Servicios (Inyectando el repositorio)
        CrearDroneUseCase crearUC = new CrearDroneServicio(droneRepository);
        ListarDronesUseCase listarUC = new ListarDronesServicio(droneRepository);
        ActualizarDroneUseCase actualizarUC = new ActualizarDroneServicio(droneRepository);
        EliminarDroneUseCase eliminarUC = new EliminarDroneServicio(droneRepository);

        // 3. Instanciar Adaptador de Entrada (Inyectando los Casos de Uso)
        DroneController controller = new DroneController(crearUC, listarUC, actualizarUC, eliminarUC);

        // 4. Iniciar la UI (JavaFX)
        DroneView root = new DroneView(controller);
        Scene scene = new Scene(root, 1100, 700);

        stage.setTitle("Gestor de Drones - Arquitectura Hexagonal");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}