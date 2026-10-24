package co.edu.poli.SW2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.drone.aplicacion.puerto.entrada.ActualizarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ConsultarClimaUseCase;
import com.drone.aplicacion.puerto.entrada.CrearDroneUseCase;
import com.drone.aplicacion.puerto.entrada.EliminarDroneUseCase;
import com.drone.aplicacion.puerto.entrada.ListarDronesUseCase;
import com.drone.aplicacion.puerto.salida.DroneRepository;
import com.drone.aplicacion.puerto.salida.WeatherApiClientPort;
import com.drone.aplicacion.servicio.ActualizarDroneServicio;
import com.drone.aplicacion.servicio.ConsultarClimaServicio;
import com.drone.aplicacion.servicio.CrearDroneServicio;
import com.drone.aplicacion.servicio.EliminarDroneServicio;
import com.drone.aplicacion.servicio.ListarDronesServicio;
import com.drone.dominio.weather.WeatherSubject;
import com.drone.infraestructura.persistencia.PostgresDroneRepository;
import com.drone.infraestructura.ui.DroneController;
import com.drone.infraestructura.vista.DroneView;
import com.drone.infraestructura.weather.OpenMeteoAdapter;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Instanciar Adaptadores de Salida (Infraestructura)
        DroneRepository droneRepository = new PostgresDroneRepository();
        WeatherApiClientPort weatherApiClient = new OpenMeteoAdapter();

        // 2. Instanciar Sujeto Observer (En memoria)
        WeatherSubject weatherSubject = new WeatherSubject();

        // 3. Instanciar Servicios (Casos de Uso)
        CrearDroneUseCase crearUC = new CrearDroneServicio(droneRepository);
        ListarDronesUseCase listarUC = new ListarDronesServicio(droneRepository);
        ActualizarDroneUseCase actualizarUC = new ActualizarDroneServicio(droneRepository);
        EliminarDroneUseCase eliminarUC = new EliminarDroneServicio(droneRepository);
        ConsultarClimaUseCase consultarClimaUC = new ConsultarClimaServicio(weatherApiClient, weatherSubject);

        // 4. Instanciar Adaptador de Entrada (Controller)
        DroneController controller = new DroneController(
                crearUC, listarUC, actualizarUC, eliminarUC, weatherSubject, consultarClimaUC);

        // 5. Iniciar la UI (JavaFX)
        DroneView root = new DroneView(controller);
        Scene scene = new Scene(root, 1150, 780);

        stage.setTitle("Gestor de Drones - Arquitectura Hexagonal");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
