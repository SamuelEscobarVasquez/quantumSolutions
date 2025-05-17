package org.quantum.solutions.quantumsolutions.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.quantum.solutions.quantumsolutions.service.EmpleadoListaEnlazada;
import org.quantum.solutions.quantumsolutions.model.Empleado;
import org.quantum.solutions.quantumsolutions.model.TipoSoporte;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class EmpleadoController {
 
    @FXML private TableView<Empleado> tabla;
    @FXML private TableColumn<Empleado,String> DpiColumn;
    @FXML private TableColumn<Empleado,String> NombreColumn;
    @FXML private TableColumn<Empleado, TipoSoporte> TipoColumn;

    private final EmpleadoListaEnlazada empleados = new EmpleadoListaEnlazada();
    private Path CSVactual = Path.of("empleados.csv");   // fichero por defecto

    /* ---------- Inicialización ---------- */
    @FXML private void initialize() {
        DpiColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getDpi()));
        NombreColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getNombre()));
        TipoColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getTipoSoporte()));
        refreshTable();


        try { empleados.cargar(CSVactual); refreshTable(); }
        catch (IOException ignored) {
            System.out.println("Archivo no encontrado para precargar data...");
        }
    }

    /* ---------- Acciones de menú ---------- */

    @FXML
    private void cargarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar CSV");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));


        File file = fc.showOpenDialog(getStage());
        if (file == null) return;

        Path p = file.toPath();
        try {
            empleados.cargar(p);
            CSVactual = p;
            refreshTable();
        } catch (IOException e) {
            showError("No se pudo cargar el archivo.");
        }
    }

    @FXML
    private void exportarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar CSV como…");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));

        // Sugiere la ruta actual (si existe) y un nombre por defecto
        if (CSVactual != null) {
            fc.setInitialDirectory(CSVactual.toFile().getParentFile());
            fc.setInitialFileName(CSVactual.getFileName().toString());
        } else {
            fc.setInitialFileName("Empleados.csv");
        }

        File file = fc.showSaveDialog(getStage());
        if (file == null) return;

        Path p = file.toPath();
        try {
            empleados.exportar(p);
            CSVactual = p;
            showInfo("Archivo guardado en:\n" + p);
        } catch (IOException e) {
            showError("No se pudo guardar el archivo.");
        }
    }

    @FXML private void agregarEmpleado() {
        Empleado e = showEmployeeDialog(null);
        if (e != null) { empleados.agregar(e); refreshTable(); }
    }

    @FXML private void actualizarEmpleado() {
        Empleado sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { showInfo("Selecciona un empleado primero."); return; }
        Empleado updated = showEmployeeDialog(sel);
        if (updated != null) { empleados.actualizar(updated); refreshTable(); }
    }

    @FXML private void borrarEmpleado() {
        Empleado sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { showInfo("Selecciona un empleado primero."); return; }
        empleados.eliminar(sel.getDpi());
        refreshTable();
    }

    @FXML private void salir() { getStage().close(); }

    /* ---------- utilidades ---------- */

    private void refreshTable() { tabla.setItems(empleados.listarEmpleados()); }

    private Stage getStage() { return (Stage) tabla.getScene().getWindow(); }

    private void showError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }

    private Empleado showEmployeeDialog(Empleado original) {
        Dialog<Empleado> dialog = new Dialog<>();
        dialog.setTitle(original == null ? "Nuevo empleado" : "Actualizar empleado");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField dpiField   = new TextField(original == null ? "" : original.getDpi());
        TextField nameField  = new TextField(original == null ? "" : original.getNombre());
        ComboBox<TipoSoporte> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(TipoSoporte.values());
        typeBox.setValue(original == null ? TipoSoporte.TECNICO_REPARACION_PCS : original.getTipoSoporte());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(0, new Label("DPI:"), dpiField);
        grid.addRow(1, new Label("Nombre:"), nameField);
        grid.addRow(2, new Label("Tipo soporte:"), typeBox);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new Empleado(dpiField.getText().trim(),
                        nameField.getText().trim(),
                        typeBox.getValue());
            }
            return null;
        });
        return dialog.showAndWait().orElse(null);
    }
}