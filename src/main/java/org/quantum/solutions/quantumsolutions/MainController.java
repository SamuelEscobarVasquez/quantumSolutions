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

    @FXML private TableView<Employee> table;
    @FXML private TableColumn<Employee,String> dpiCol;
    @FXML private TableColumn<Employee,String> nameCol;
    @FXML private TableColumn<Employee,SupportType> typeCol;

    private final EmployeeLinkedList employees = new EmployeeLinkedList();
    private Path currentCsv = Path.of("employees.csv");   // fichero por defecto

    /* ---------- inicialización ---------- */
    @FXML private void initialize() {
        dpiCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getDpi()));
        nameCol.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getFullName()));
        typeCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().getSupportType()));
        refreshTable();

        // intenta precargar
        try { employees.load(currentCsv); refreshTable(); }
        catch (IOException ignored) { /* primera ejecución → archivo inexistente */ }
    }

    /* ---------- acciones de menú ---------- */

    @FXML
    private void onLoadCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar CSV");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));

        // devuelto por el diálogo:
        File file = fc.showOpenDialog(getStage());
        if (file == null) return;               // usuario canceló

        Path p = file.toPath();
        try {
            employees.load(p);
            currentCsv = p;
            refreshTable();
        } catch (IOException e) {
            showError("No se pudo cargar el archivo.");
        }
    }

    @FXML
    private void onSaveCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar CSV como…");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));

        // Sugiere la ruta actual (si existe) y un nombre por defecto
        if (currentCsv != null) {
            fc.setInitialDirectory(currentCsv.toFile().getParentFile());
            fc.setInitialFileName(currentCsv.getFileName().toString());
        } else {
            fc.setInitialFileName("employees.csv");
        }

        File file = fc.showSaveDialog(getStage());
        if (file == null) return;          // usuario canceló

        Path p = file.toPath();
        try {
            employees.save(p);             // escribe la lista en disco
            currentCsv = p;                // recordamos la nueva ruta
            showInfo("Archivo guardado en:\n" + p);
        } catch (IOException e) {
            showError("No se pudo guardar el archivo.");
        }
    }

    @FXML private void onAdd() {
        Employee e = showEmployeeDialog(null);
        if (e != null) { employees.add(e); refreshTable(); }
    }

    @FXML private void onUpdate() {
        Employee sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showInfo("Selecciona un empleado primero."); return; }
        Employee updated = showEmployeeDialog(sel);
        if (updated != null) { employees.update(updated); refreshTable(); }
    }

    @FXML private void onDelete() {
        Employee sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showInfo("Selecciona un empleado primero."); return; }
        employees.remove(sel.getDpi());
        refreshTable();
    }

    @FXML private void onExit() { getStage().close(); }

    /* ---------- utilidades ---------- */

    private void refreshTable() { table.setItems(employees.asObservableList()); }

    private Stage getStage() { return (Stage) table.getScene().getWindow(); }

    private void showError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }

    private Employee showEmployeeDialog(Employee original) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(original == null ? "Nuevo empleado" : "Actualizar empleado");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField dpiField   = new TextField(original == null ? "" : original.getDpi());
        TextField nameField  = new TextField(original == null ? "" : original.getFullName());
        ComboBox<SupportType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(SupportType.values());
        typeBox.setValue(original == null ? SupportType.PC_REPAIR : original.getSupportType());

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.addRow(0, new Label("DPI:"), dpiField);
        grid.addRow(1, new Label("Nombre:"), nameField);
        grid.addRow(2, new Label("Tipo soporte:"), typeBox);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new Employee(dpiField.getText().trim(),
                        nameField.getText().trim(),
                        typeBox.getValue());
            }
            return null;
        });
        return dialog.showAndWait().orElse(null);
    }
}