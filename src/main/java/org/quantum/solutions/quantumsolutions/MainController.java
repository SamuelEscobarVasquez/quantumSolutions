// MainController.java
package org.quantum.solutions.quantumsolutions;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MainController {
 
    @FXML private TableView<empleado> tabla;
    @FXML private TableColumn<empleado,String> DpiColumn;
    @FXML private TableColumn<empleado,String> NombreColumn;
    @FXML private TableColumn<empleado,SupportType> TipoColumn;

    private final EmployeeLinkedList empleados = new EmployeeLinkedList();
    private Path CSVactual = Path.of("empleados.csv");   // fichero por defecto

    /* ---------- inicialización ---------- */
    @FXML private void initialize() {
        DpiColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getDpi()));
        NombreColumn.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getNombre()));
        TipoColumn.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getPsoporte()));
        refreshTable();

        // intenta precargar
        try { empleados.cargar(CSVactual); refreshTable(); }
        catch (IOException ignored) { /* primera ejecución → archivo inexistente */ }
    }

    /* ---------- acciones de menú ---------- */

    @FXML
    private void cargarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar CSV");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));

        // devuelto por el diálogo:
        File file = fc.showOpenDialog(getStage());
        if (file == null) return;               // usuario canceló

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
    private void GuardarCSV() {
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
        if (file == null) return;          // usuario canceló

        Path p = file.toPath();
        try {
            empleados.guardar(p);             // escribe la lista en disco
            CSVactual = p;                // recordamos la nueva ruta
            showInfo("Archivo guardado en:\n" + p);
        } catch (IOException e) {
            showError("No se pudo guardar el archivo.");
        }
    }

    @FXML private void AgregarE() {
        empleado e = showEmployeeDialog(null);
        if (e != null) { empleados.agregar(e); refreshTable(); }
    }

    @FXML private void ActualizarE() {
        empleado sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { showInfo("Selecciona un empleado primero."); return; }
        empleado updated = showEmployeeDialog(sel);
        if (updated != null) { empleados.actualizar(updated); refreshTable(); }
    }

    @FXML private void BorrarE() {
        empleado sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { showInfo("Selecciona un empleado primero."); return; }
        empleados.eliminar(sel.getDpi());
        refreshTable();
    }

    @FXML private void Salir() { getStage().close(); }

    /* ---------- utilidades ---------- */

    private void refreshTable() { tabla.setItems(empleados.ListaV()); }

    private Stage getStage() { return (Stage) tabla.getScene().getWindow(); }

    private void showError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }

    private empleado showEmployeeDialog(empleado original) {
        Dialog<empleado> dialog = new Dialog<>();
        dialog.setTitle(original == null ? "Nuevo empleado" : "Actualizar empleado");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField dpiField   = new TextField(original == null ? "" : original.getDpi());
        TextField nameField  = new TextField(original == null ? "" : original.getNombre());
        ComboBox<SupportType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(SupportType.values());
        typeBox.setValue(original == null ? SupportType.PC_REPAIR : original.getPsoporte());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(0, new Label("DPI:"), dpiField);
        grid.addRow(1, new Label("Nombre:"), nameField);
        grid.addRow(2, new Label("Tipo soporte:"), typeBox);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new empleado(dpiField.getText().trim(),
                        nameField.getText().trim(),
                        typeBox.getValue());
            }
            return null;
        });
        return dialog.showAndWait().orElse(null);
    }
}