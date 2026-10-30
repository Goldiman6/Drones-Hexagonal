package com.drone.infraestructura.vista;

import com.drone.aplicacion.weather.WeatherResultDTO;
import com.drone.dominio.command.DroneCommand;
import com.drone.dominio.modelo.Agricultura;
import com.drone.dominio.modelo.Drone;
import com.drone.dominio.modelo.Vigilancia;
import com.drone.dominio.strategy.AhorroStrategy;
import com.drone.dominio.strategy.AltoRendimientoStrategy;
import com.drone.dominio.strategy.ConsumoStrategy;
import com.drone.dominio.strategy.NormalStrategy;
import com.drone.dominio.strategy.ResultadoConsumo;
import com.drone.dominio.strategy.Ruta;
import com.drone.dominio.weather.Weather;
import com.drone.dominio.weather.chain.FlightEvaluation;
import com.drone.infraestructura.ui.DroneController;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DroneView extends HBox {

    private final DroneController controller;
    private final TableView<Drone> table;
    private final ObservableList<Drone> droneData;

    // Campos del formulario CRUD
    private TextField idField;
    private ComboBox<String> tipoCombo;
    private TextField serialField;
    private TextField modeloField;
    private TextField fabricanteField;
    private TextField pesoField;
    private Label dinamicoLabel;
    private TextField capacidadField;
    private CheckBox termicaCheck;

    // Botones CRUD
    private Button btnCreate;
    private Button btnUpdate;
    private Button btnDelete;
    private Button btnClear;

    // Componentes Patrones Observer + Chain of Responsibility
    private Button btnSuscribir;
    private Button btnDesuscribir;
    private Label lblSuscritos;
    private ComboBox<String> ciudadCombo;
    private Button btnConsultarClima;

    // Componentes Patrón Strategy
    private ComboBox<Ruta> rutaCombo;
    private ComboBox<ConsumoStrategy> strategyCombo;
    private Button btnCalcularConsumo;

    // Componentes Patrón Command
    private Label lblSecuenciaMision;

    public DroneView(DroneController controller) {
        this.controller = controller;
        this.droneData = FXCollections.observableArrayList();
        this.table = new TableView<>();

        setPadding(new Insets(15));
        setSpacing(15);

        VBox formBox = createFormBox();
        VBox tableBox = createTableBox();

        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setPrefWidth(380);

        getChildren().addAll(scrollPane, tableBox);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        refreshTable();
        actualizarLabelSuscritos();
        cargarRutasEnCombo();
    }

    private VBox createFormBox() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5;");
        vbox.setPrefWidth(350);

        Label title = new Label("Gestión de Drones - Arquitectura Hexagonal");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        idField = new TextField();
        idField.setPromptText("Ej. D-001");

        tipoCombo = new ComboBox<>(FXCollections.observableArrayList("Agricultura", "Vigilancia"));
        tipoCombo.setValue("Agricultura");

        serialField     = new TextField();
        modeloField     = new TextField();
        fabricanteField = new TextField();
        pesoField       = new TextField();

        dinamicoLabel = new Label("Capacidad Tanque (L):");
        capacidadField = new TextField();
        termicaCheck   = new CheckBox("Detección Térmica");
        termicaCheck.setVisible(false);
        termicaCheck.setManaged(false);

        tipoCombo.setOnAction(e -> actualizarCamposDinamicos());

        grid.add(new Label("ID:"),         0, 0); grid.add(idField,          1, 0);
        grid.add(new Label("Tipo:"),       0, 1); grid.add(tipoCombo,        1, 1);
        grid.add(new Label("Serial:"),     0, 2); grid.add(serialField,      1, 2);
        grid.add(new Label("Modelo:"),     0, 3); grid.add(modeloField,      1, 3);
        grid.add(new Label("Fabricante:"), 0, 4); grid.add(fabricanteField,  1, 4);
        grid.add(new Label("Peso (kg):"),  0, 5); grid.add(pesoField,        1, 5);
        grid.add(dinamicoLabel,            0, 6); grid.add(capacidadField,   1, 6);
        grid.add(termicaCheck,             1, 6);

        HBox btnBox = new HBox(10);
        btnCreate = new Button("Crear");
        btnUpdate = new Button("Actualizar");
        btnDelete = new Button("Eliminar");
        btnClear  = new Button("Limpiar");

        btnCreate.setOnAction(e -> crearDrone());
        btnUpdate.setOnAction(e -> actualizarDrone());
        btnDelete.setOnAction(e -> eliminarDrone());
        btnClear.setOnAction(e  -> clearForm());

        btnBox.getChildren().addAll(btnCreate, btnUpdate, btnDelete, btnClear);

        // ── SECCIÓN: OBSERVER & CHAIN OF RESPONSIBILITY ──
        VBox weatherBox = new VBox(8);
        weatherBox.setPadding(new Insets(10));
        weatherBox.setStyle("-fx-border-color: #007acc; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #f7faff;");

        Label weatherTitle = new Label("Monitoreo Climático y Viabilidad de Vuelo");
        weatherTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #005a9e;");

        Label observerHelp = new Label("1. Seleccione un dron en la tabla para suscribir:");
        observerHelp.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        HBox observerBtnBox = new HBox(10);
        btnSuscribir = new Button("Suscribir");
        btnSuscribir.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnDesuscribir = new Button("Desuscribir");
        btnDesuscribir.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        observerBtnBox.getChildren().addAll(btnSuscribir, btnDesuscribir);

        lblSuscritos = new Label("Drones suscritos: [Ninguno]");
        lblSuscritos.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #222;");
        lblSuscritos.setWrapText(true);

        btnSuscribir.setOnAction(e -> suscribirDroneSeleccionado());
        btnDesuscribir.setOnAction(e -> desuscribirDroneSeleccionado());

        Label cityLabel = new Label("2. Seleccione la ciudad de operación:");
        cityLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        ciudadCombo = new ComboBox<>(FXCollections.observableArrayList("Bogota", "Brujas"));
        ciudadCombo.setValue("Bogota");
        ciudadCombo.setMaxWidth(Double.MAX_VALUE);

        btnConsultarClima = new Button("Consultar Clima y Evaluar Vuelo");
        btnConsultarClima.setMaxWidth(Double.MAX_VALUE);
        btnConsultarClima.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnConsultarClima.setOnAction(e -> consultarYEvaluarClima());

        weatherBox.getChildren().addAll(
                weatherTitle,
                observerHelp,
                observerBtnBox,
                lblSuscritos,
                new Separator(),
                cityLabel,
                ciudadCombo,
                btnConsultarClima
        );

        // ── SECCIÓN: PATRÓN STRATEGY (ESTIMACIÓN DE BATERÍA POR RUTA) ──
        VBox strategyBox = new VBox(8);
        strategyBox.setPadding(new Insets(10));
        strategyBox.setStyle("-fx-border-color: #ff8c00; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #fffbf5;");

        Label strategyTitle = new Label("Estimación de Batería (Patrón Strategy)");
        strategyTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #c75b00;");

        Label strategyHelp = new Label("Seleccione un dron en la tabla, una ruta y la estrategia de consumo:");
        strategyHelp.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
        strategyHelp.setWrapText(true);

        Label rutaLabel = new Label("1. Ruta de Vuelo (JSON):");
        rutaLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        rutaCombo = new ComboBox<>();
        rutaCombo.setMaxWidth(Double.MAX_VALUE);

        Label modoLabel = new Label("2. Estrategia de Consumo Energético:");
        modoLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        strategyCombo = new ComboBox<>(FXCollections.observableArrayList(
                new AhorroStrategy(),
                new NormalStrategy(),
                new AltoRendimientoStrategy()
        ));
        strategyCombo.setValue(strategyCombo.getItems().get(0));
        strategyCombo.setMaxWidth(Double.MAX_VALUE);

        btnCalcularConsumo = new Button("Calcular Consumo de Ruta");
        btnCalcularConsumo.setMaxWidth(Double.MAX_VALUE);
        btnCalcularConsumo.setStyle("-fx-background-color: #ff8c00; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnCalcularConsumo.setOnAction(e -> calcularConsumoRuta());

        strategyBox.getChildren().addAll(
                strategyTitle,
                strategyHelp,
                rutaLabel,
                rutaCombo,
                modoLabel,
                strategyCombo,
                btnCalcularConsumo
        );

        // ── SECCIÓN: PATRÓN COMMAND (PROGRAMACIÓN Y EJECUCIÓN DE MISIÓN) ──
        VBox commandBox = new VBox(8);
        commandBox.setPadding(new Insets(10));
        commandBox.setStyle("-fx-border-color: #6f42c1; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #fbf9fe;");

        Label commandTitle = new Label("Programación de Misión (Patrón Command)");
        commandTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #4e228c;");

        Label commandHelp = new Label("Seleccione un dron y arme la secuencia de tareas a ejecutar en orden:");
        commandHelp.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
        commandHelp.setWrapText(true);

        HBox cmdBtnBox = new HBox(6);
        Button btnCmdFoto = new Button("+ Foto");
        btnCmdFoto.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
        Button btnCmdEscaneo = new Button("+ Escaneo");
        btnCmdEscaneo.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
        Button btnCmdTemp = new Button("+ Temperatura");
        btnCmdTemp.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
        cmdBtnBox.getChildren().addAll(btnCmdFoto, btnCmdEscaneo, btnCmdTemp);

        lblSecuenciaMision = new Label("Secuencia programada (0): [Ninguna tarea]");
        lblSecuenciaMision.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #333;");
        lblSecuenciaMision.setWrapText(true);

        HBox actionCmdBox = new HBox(10);
        Button btnLimpiarMision = new Button("Limpiar");
        btnLimpiarMision.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        Button btnEjecutarMision = new Button("Ejecutar Misión");
        btnEjecutarMision.setMaxWidth(Double.MAX_VALUE);
        btnEjecutarMision.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        HBox.setHgrow(btnEjecutarMision, Priority.ALWAYS);
        actionCmdBox.getChildren().addAll(btnLimpiarMision, btnEjecutarMision);

        btnCmdFoto.setOnAction(e -> encolarTarea("FOTO"));
        btnCmdEscaneo.setOnAction(e -> encolarTarea("ESCANEO"));
        btnCmdTemp.setOnAction(e -> encolarTarea("TEMPERATURA"));
        btnLimpiarMision.setOnAction(e -> limpiarMision());
        btnEjecutarMision.setOnAction(e -> ejecutarMisionProgramada());

        commandBox.getChildren().addAll(
                commandTitle,
                commandHelp,
                cmdBtnBox,
                lblSecuenciaMision,
                actionCmdBox
        );

        vbox.getChildren().addAll(
                title, grid, new Separator(), btnBox,
                new Separator(), weatherBox,
                new Separator(), strategyBox,
                new Separator(), commandBox
        );
        return vbox;
    }

    private VBox createTableBox() {
        VBox vbox = new VBox(10);

        TableColumn<Drone, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Drone, String> colSerial = new TableColumn<>("Serial");
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serial"));

        TableColumn<Drone, String> colModelo = new TableColumn<>("Modelo");
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));

        TableColumn<Drone, String> colFabricante = new TableColumn<>("Fabricante");
        colFabricante.setCellValueFactory(new PropertyValueFactory<>("fabricante"));

        TableColumn<Drone, String> colPeso = new TableColumn<>("Peso");
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));

        TableColumn<Drone, String> colAtributo = new TableColumn<>("Atributo Específico");
        colAtributo.setCellValueFactory(cellData -> {
            Drone d = cellData.getValue();
            if (d instanceof Agricultura) {
                return new SimpleStringProperty(((Agricultura) d).getCapacidadTanque() + " L");
            } else if (d instanceof Vigilancia) {
                return new SimpleStringProperty(((Vigilancia) d).isDeteccionTermica() ? "Térmica: Sí" : "Térmica: No");
            }
            return new SimpleStringProperty("N/A");
        });

        table.getColumns().addAll(colId, colSerial, colModelo, colFabricante, colPeso, colAtributo);
        table.setItems(droneData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                cargarDroneEnFormulario(newSel);
            }
        });

        vbox.getChildren().addAll(new Label("Lista de Drones"), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return vbox;
    }

    private void actualizarCamposDinamicos() {
        if ("Agricultura".equals(tipoCombo.getValue())) {
            dinamicoLabel.setText("Capacidad Tanque (L):");
            capacidadField.setVisible(true);
            capacidadField.setManaged(true);
            termicaCheck.setVisible(false);
            termicaCheck.setManaged(false);
        } else {
            dinamicoLabel.setText("Detección Térmica:");
            capacidadField.setVisible(false);
            capacidadField.setManaged(false);
            termicaCheck.setVisible(true);
            termicaCheck.setManaged(true);
        }
    }

    // ── GESTIÓN OBSERVER (SUSCRIBIR / DESUSCRIBIR) ─────────────────────────

    private void suscribirDroneSeleccionado() {
        Drone selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Por favor seleccione un dron de la tabla para suscribirlo.");
            return;
        }

        boolean suscrito = controller.suscribirDrone(selected);
        if (suscrito) {
            actualizarLabelSuscritos();
            showAlert(Alert.AlertType.INFORMATION, "Suscripción Exitosa",
                    "El dron '" + selected.getId() + "' (" + selected.getModelo() + ") ha sido suscrito al sistema de monitoreo climático.\n(Almacenado en memoria mediante el patrón Observer).");
        } else {
            showAlert(Alert.AlertType.WARNING, "Ya Suscrito",
                    "El dron '" + selected.getId() + "' ya se encuentra en la lista de observadores.");
        }
    }

    private void desuscribirDroneSeleccionado() {
        Drone selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Por favor seleccione un dron de la tabla para desuscribirlo.");
            return;
        }

        boolean removido = controller.desuscribirDrone(selected.getId());
        if (removido) {
            actualizarLabelSuscritos();
            showAlert(Alert.AlertType.INFORMATION, "Desuscripción Exitosa",
                    "El dron '" + selected.getId() + "' ha sido eliminado de la lista de observadores climáticos.");
        } else {
            showAlert(Alert.AlertType.WARNING, "No Suscrito",
                    "El dron '" + selected.getId() + "' no estaba suscrito al sistema de monitoreo.");
        }
    }

    private void actualizarLabelSuscritos() {
        List<String> ids = controller.getDronesSuscritosIds();
        if (ids.isEmpty()) {
            lblSuscritos.setText("Drones suscritos: [Ninguno]");
        } else {
            lblSuscritos.setText("Drones suscritos (" + ids.size() + "): " + String.join(", ", ids));
        }
    }

    // ── CONSULTA CLIMÁTICA Y EVALUACIÓN CHAIN OF RESPONSIBILITY ─────────────

    private void consultarYEvaluarClima() {
        String ciudad = ciudadCombo.getValue();
        if (ciudad == null || ciudad.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Seleccione una ciudad para realizar la consulta.");
            return;
        }

        try {
            WeatherResultDTO resultado = controller.consultarYEvaluarClima(ciudad);
            Weather w = resultado.getWeather();
            FlightEvaluation eval = resultado.getEvaluation();
            List<String> suscritos = resultado.getDronesNotificados();

            StringBuilder sb = new StringBuilder();
            sb.append("📍 CIUDAD CONSULTADA: ").append(w.getCiudad()).append("\n\n");
            sb.append("🌤️ CONDICIONES METEOROLÓGICAS ACTUALES (Open-Meteo):\n");
            sb.append(String.format("   • Temperatura:          %.1f °C\n", w.getTemperatura()));
            sb.append(String.format("   • Humedad Relativa:     %.1f %%\n", w.getHumedad()));
            sb.append(String.format("   • Velocidad del Viento: %.1f km/h\n", w.getVelocidadViento()));
            sb.append(String.format("   • Presión Atmosférica:  %.1f hPa\n", w.getPresionAtmosferica()));
            sb.append(String.format("   • Visibilidad:          %.2f km\n\n", w.getVisibilidad()));

            sb.append("⚖️ EVALUACIÓN DE VIABILIDAD (Chain of Responsibility):\n");
            if (eval.isApto()) {
                sb.append("   ✅ ").append(eval.getMensaje()).append("\n\n");
            } else {
                sb.append("   ❌ ").append(eval.getMensaje()).append("\n");
                sb.append("   ⚠️ Regla incumplida: ").append(eval.getReglaIncumplida()).append("\n\n");
            }

            sb.append("📡 DRONES SUSCRITOS NOTIFICADOS (Patrón Observer):\n");
            if (suscritos.isEmpty()) {
                sb.append("   [Ningún dron suscrito actualmente]\n");
                sb.append("   (Tip: Seleccione un dron de la tabla y haga clic en 'Suscribir').\n");
            } else {
                sb.append("   • Flota notificada (").append(suscritos.size()).append("): ")
                  .append(String.join(", ", suscritos)).append("\n");
                sb.append("   • Estado de operación: ")
                  .append(eval.isApto() ? "AUTORIZADO PARA DESPEGUE" : "OPERACIÓN RESTRINGIDA / EN ESPERA")
                  .append("\n");
            }

            Alert.AlertType tipoAlerta = eval.isApto() ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING;
            String encabezado = eval.isApto()
                    ? "CLIMA APTO PARA OPERACIÓN DE DRONES"
                    : "CLIMA NO APTO PARA OPERACIÓN DE DRONES";

            Alert alert = new Alert(tipoAlerta);
            alert.setTitle("Evaluación Climática de Viabilidad de Vuelo");
            alert.setHeaderText(encabezado);
            alert.setContentText(sb.toString());
            alert.getDialogPane().setPrefWidth(550);
            alert.showAndWait();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Consulta Climática",
                    "No se pudo consultar el clima o evaluar la viabilidad: " + e.getMessage());
        }
    }

    // ── ESTIMACIÓN DE BATERÍA (PATRÓN STRATEGY) ───────────────────────────

    private void cargarRutasEnCombo() {
        try {
            List<Ruta> rutas = controller.obtenerRutasDisponibles();
            rutaCombo.setItems(FXCollections.observableArrayList(rutas));
            if (!rutas.isEmpty()) {
                rutaCombo.setValue(rutas.get(0));
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "No se pudieron cargar las rutas del JSON: " + e.getMessage());
        }
    }

    private void calcularConsumoRuta() {
        Drone selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Por favor seleccione un dron de la tabla para calcular la estimación.");
            return;
        }

        Ruta ruta = rutaCombo.getValue();
        if (ruta == null) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Por favor seleccione una ruta de vuelo.");
            return;
        }

        ConsumoStrategy strategy = strategyCombo.getValue();
        if (strategy == null) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Por favor seleccione una estrategia de consumo energético.");
            return;
        }

        try {
            ResultadoConsumo res = controller.calcularConsumo(selected, ruta, strategy);

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("🚁 DRON: %s (%s) | Peso: %.1f kg\n",
                    res.getDrone().getId(), res.getDrone().getModelo(), res.getDrone().getPeso()));
            sb.append(String.format("📍 MISIÓN: %s (%.1f km)\n",
                    res.getRuta().getNombre(), res.getDistanciaKm()));
            sb.append(String.format("   • Origen:  %s\n", res.getRuta().getOrigen().getNombre()));
            sb.append(String.format("   • Destino: %s\n\n", res.getRuta().getDestino().getNombre()));

            sb.append("⚡ ESTRATEGIA DE CONSUMO:\n");
            sb.append(String.format("   • Modo:    %s\n", res.getEstrategiaNombre()));
            sb.append(String.format("   • Batería: %.1f%% requerida (Límite: 100%%)\n\n", res.getPorcentajeConsumo()));

            sb.append("⚖️ EVALUACIÓN OPERACIONAL:\n");
            if (res.isEsViable()) {
                double restante = 100.0 - res.getPorcentajeConsumo();
                sb.append(String.format("   ✅ Vuelo Viable. Batería restante al llegar: %.1f%%\n", restante));
            } else {
                double faltante = res.getPorcentajeConsumo() - 100.0;
                sb.append(String.format("   ❌ No Viable. Falta %.1f%% de batería para completar la ruta.\n", faltante));
                sb.append("   ⚠️ Acción sugerida: Reducir la carga.\n");
            }

            Alert.AlertType alertType = res.isEsViable() ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING;
            String header = res.isEsViable()
                    ? "VUELO VIABLE: BATERÍA SUFICIENTE"
                    : "ALERTA CRÍTICA: BATERÍA INSUFICIENTE";

            Alert alert = new Alert(alertType);
            alert.setTitle("Estimación de Batería (Patrón Strategy)");
            alert.setHeaderText(header);
            alert.setContentText(sb.toString());
            alert.getDialogPane().setPrefWidth(500);
            alert.showAndWait();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Cálculo", "No se pudo realizar el cálculo de consumo: " + e.getMessage());
        }
    }

    // ── GESTIÓN PATRÓN COMMAND (SECUENCIA DE TAREAS / MISIÓN) ───────────────

    private void encolarTarea(String tipo) {
        Drone selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Por favor seleccione un dron en la tabla para asignarle la tarea.");
            return;
        }

        switch (tipo) {
            case "FOTO":
                controller.agregarTareaFotografia(selected);
                break;
            case "ESCANEO":
                controller.agregarTareaEscaneo(selected);
                break;
            case "TEMPERATURA":
                controller.agregarTareaTemperatura(selected);
                break;
        }
        actualizarLabelSecuencia();
    }

    private void limpiarMision() {
        controller.limpiarTareasMision();
        actualizarLabelSecuencia();
    }

    private void actualizarLabelSecuencia() {
        List<DroneCommand> tareas = controller.getTareasMision();
        if (tareas.isEmpty()) {
            lblSecuenciaMision.setText("Secuencia programada (0): [Ninguna tarea]");
        } else {
            List<String> nombres = new ArrayList<>();
            for (int i = 0; i < tareas.size(); i++) {
                nombres.add((i + 1) + ". " + tareas.get(i).getNombre());
            }
            lblSecuenciaMision.setText(String.format("Secuencia programada (%d):\n%s",
                    tareas.size(), String.join(" ➔ ", nombres)));
        }
    }

    private void ejecutarMisionProgramada() {
        Drone selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Atención", "Por favor seleccione el dron asignado para ejecutar la misión.");
            return;
        }

        if (controller.getTareasMision().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Secuencia Vacía", "Agregue al menos una tarea a la secuencia antes de ejecutar.");
            return;
        }

        try {
            int totalTareas = controller.getTareasMision().size();
            List<String> reporte = controller.ejecutarMision();

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("🚁 DRON OPERADOR: %s (%s)\n", selected.getId(), selected.getModelo()));
            sb.append(String.format("   • Especialidad: %s | Peso: %.1f kg\n\n",
                    (selected instanceof Agricultura ? "Agricultura" : "Vigilancia"), selected.getPeso()));
            sb.append(String.format("📋 TAREAS EJECUTADAS EN SECUENCIA: %d\n", totalTareas));
            sb.append("───────────────────────────────────────────────\n\n");

            for (String paso : reporte) {
                sb.append(paso).append("\n\n");
            }

            sb.append("───────────────────────────────────────────────\n");
            sb.append("✅ ESTADO: Misión completada exitosamente en orden.");

            controller.limpiarTareasMision();
            actualizarLabelSecuencia();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reporte de Misión (Patrón Command)");
            alert.setHeaderText("MISIÓN COMPLETADA - SECUENCIA DE TAREAS EJECUTADA");
            alert.setContentText(sb.toString());
            alert.getDialogPane().setPrefWidth(560);
            alert.showAndWait();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error en Misión", "Ocurrió un error al ejecutar la secuencia: " + e.getMessage());
        }
    }

    // ── CRUD ───────────────────────────────────────────────────────────────

    private void crearDrone() {
        try {
            Drone drone = construirDroneDesdeFormulario();
            controller.addDrone(drone);
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Dron creado exitosamente.");
            refreshTable();
            clearForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void actualizarDrone() {
        try {
            Drone drone = construirDroneDesdeFormulario();
            controller.updateDrone(drone);
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Dron actualizado exitosamente.");
            refreshTable();
            clearForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void eliminarDrone() {
        Drone selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Seleccione un dron de la tabla para eliminar.");
            return;
        }
        try {
            controller.deleteDrone(selected.getId());
            controller.desuscribirDrone(selected.getId());
            actualizarLabelSuscritos();

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Dron eliminado exitosamente.");
            refreshTable();
            clearForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private Drone construirDroneDesdeFormulario() throws Exception {
        String id         = idField.getText().trim();
        String serial     = serialField.getText().trim();
        String modelo     = modeloField.getText().trim();
        String fabricante = fabricanteField.getText().trim();

        if (id.isEmpty() || serial.isEmpty() || modelo.isEmpty() || fabricante.isEmpty()) {
            throw new Exception("Todos los campos de texto son obligatorios.");
        }

        double peso;
        try {
            peso = Double.parseDouble(pesoField.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("El peso debe ser un número válido.");
        }

        if ("Agricultura".equals(tipoCombo.getValue())) {
            double capacidad;
            try {
                capacidad = Double.parseDouble(capacidadField.getText().trim());
            } catch (NumberFormatException e) {
                throw new Exception("La capacidad debe ser un número válido.");
            }
            return new Agricultura(id, serial, modelo, fabricante, peso, capacidad);
        } else {
            return new Vigilancia(id, serial, modelo, fabricante, peso, termicaCheck.isSelected());
        }
    }

    private void cargarDroneEnFormulario(Drone d) {
        idField.setText(d.getId());
        serialField.setText(d.getSerial());
        modeloField.setText(d.getModelo());
        fabricanteField.setText(d.getFabricante());
        pesoField.setText(String.valueOf(d.getPeso()));

        if (d instanceof Agricultura) {
            tipoCombo.setValue("Agricultura");
            capacidadField.setText(String.valueOf(((Agricultura) d).getCapacidadTanque()));
        } else if (d instanceof Vigilancia) {
            tipoCombo.setValue("Vigilancia");
            termicaCheck.setSelected(((Vigilancia) d).isDeteccionTermica());
        }
    }

    private void clearForm() {
        idField.clear();
        serialField.clear();
        modeloField.clear();
        fabricanteField.clear();
        pesoField.clear();
        capacidadField.clear();
        termicaCheck.setSelected(false);
        table.getSelectionModel().clearSelection();
    }

    private void refreshTable() {
        try {
            droneData.clear();
            List<Drone> lista = controller.getAllDrones();
            if (lista != null) {
                droneData.addAll(lista);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
