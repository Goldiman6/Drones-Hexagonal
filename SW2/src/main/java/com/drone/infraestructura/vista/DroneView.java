package com.drone.infraestructura.vista;

import com.drone.infraestructura.ui.DroneController;
import com.drone.dominio.modelo.Agricultura;
import com.drone.dominio.modelo.Drone;
import com.drone.dominio.modelo.Vigilancia;
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

    // Campos del formulario
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

    public DroneView(DroneController controller) {
        this.controller = controller;
        this.droneData = FXCollections.observableArrayList();
        this.table = new TableView<>();

        setPadding(new Insets(15));
        setSpacing(15);

        VBox formBox = createFormBox();
        VBox tableBox = createTableBox();

        getChildren().addAll(formBox, tableBox);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        refreshTable();
    }

    private VBox createFormBox() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5;");
        vbox.setPrefWidth(350);

        Label title = new Label("Gestion de Drones - Arquitectura Hexagonal");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        idField = new TextField();
        idField.setPromptText("Ej. D-001");

        tipoCombo = new ComboBox<>(FXCollections.observableArrayList("Agricultura", "Vigilancia"));
        tipoCombo.setValue("Agricultura");

        serialField    = new TextField();
        modeloField    = new TextField();
        fabricanteField = new TextField();
        pesoField      = new TextField();

        dinamicoLabel = new Label("Capacidad Tanque (L):");
        capacidadField = new TextField();
        termicaCheck   = new CheckBox("Deteccion Termica");
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
        vbox.getChildren().addAll(title, grid, new Separator(), btnBox);
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

        TableColumn<Drone, String> colAtributo = new TableColumn<>("Atributo Especifico");
        colAtributo.setCellValueFactory(cellData -> {
            Drone d = cellData.getValue();
            if (d instanceof Agricultura) {
                return new SimpleStringProperty(((Agricultura) d).getCapacidadTanque() + " L");
            } else if (d instanceof Vigilancia) {
                return new SimpleStringProperty(((Vigilancia) d).isDeteccionTermica() ? "Termica: Si" : "Termica: No");
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
            dinamicoLabel.setText("Deteccion Termica:");
            capacidadField.setVisible(false);
            capacidadField.setManaged(false);
            termicaCheck.setVisible(true);
            termicaCheck.setManaged(true);
        }
    }

    // ── CRUD ───────────────────────────────────────────────────────────────

    private void crearDrone() {
        try {
            Drone drone = construirDroneDesdeFormulario();
            controller.addDrone(drone);
            showAlert(Alert.AlertType.INFORMATION, "Exito", "Dron creado exitosamente.");
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
            showAlert(Alert.AlertType.INFORMATION, "Exito", "Dron actualizado exitosamente.");
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
            showAlert(Alert.AlertType.INFORMATION, "Exito", "Dron eliminado exitosamente.");
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
            throw new Exception("El peso debe ser un numero valido.");
        }

        if ("Agricultura".equals(tipoCombo.getValue())) {
            double capacidad;
            try {
                capacidad = Double.parseDouble(capacidadField.getText().trim());
            } catch (NumberFormatException e) {
                throw new Exception("La capacidad debe ser un numero valido.");
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
            showAlert(Alert.AlertType.ERROR, "Error de Conexion", "No se pudieron cargar los datos: " + e.getMessage());
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