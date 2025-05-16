package org.quantum.solutions.quantumsolutions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

public class EmployeeLinkedList {

    private Nodoempleado Encabezado;

    /* ---------- operaciones CRUD ordenadas por nombre ---------- */

    public void agregar(empleado e) {
        if (Encabezado == null || e.getNombre().compareToIgnoreCase(Encabezado.data.getNombre()) < 0) {
            Nodoempleado n = new Nodoempleado(e); n.siguiente = Encabezado; Encabezado = n; return;
        }
        Nodoempleado curr = Encabezado;
        while (curr.siguiente != null &&
                curr.siguiente.data.getNombre().compareToIgnoreCase(e.getNombre()) < 0) {
            curr = curr.siguiente;
        }
        Nodoempleado n = new Nodoempleado(e);
        n.siguiente = curr.siguiente;
        curr.siguiente = n;
    }

    public boolean eliminar(String dpi) {
        if (Encabezado == null) return false;
        if (Encabezado.data.getDpi().equals(dpi)) { Encabezado = Encabezado.siguiente; return true; }
        Nodoempleado curr = Encabezado;
        while (curr.siguiente != null && !curr.siguiente.data.getDpi().equals(dpi))
            curr = curr.siguiente;
        if (curr.siguiente == null) return false;
        curr.siguiente = curr.siguiente.siguiente;
        return true;
    }

    public boolean actualizar(empleado actualizado) {
        Nodoempleado n = Encabezado;
        eliminar(actualizado.getDpi());     // quita el viejo
        agregar(actualizado);                 // re-inserta respetando orden
        return true;

    }

    /* ---------- utilidades CSV ---------- */

    private static final String SeparadorCSV = ",";

    public void cargar(Path file) throws IOException {
        if (!Files.exists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer t = new StringTokenizer(line, SeparadorCSV);
                if (t.countTokens() != 3) continue;
                agregar(new empleado(
                        t.nextToken(),
                        t.nextToken(),
                        SupportType.valueOf(t.nextToken())
                ));
            }
        }
    }

    public void guardar(Path file) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            Nodoempleado curr = Encabezado;
            while (curr != null) {
                empleado e = curr.data;
                bw.write(String.join(SeparadorCSV,
                        e.getDpi(),
                        e.getNombre(),
                        e.getPsoporte().name()));
                bw.newLine();
                curr = curr.siguiente;
            }
        }
    }

    public ObservableList<empleado> ListaV() {
        ObservableList<empleado> list = FXCollections.observableArrayList();
        Nodoempleado curr = Encabezado;
        while (curr != null) { list.add(curr.data); curr = curr.siguiente; }
        return list;
    }
}