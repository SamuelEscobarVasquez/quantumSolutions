package org.quantum.solutions.quantumsolutions.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.quantum.solutions.quantumsolutions.model.Empleado;
import org.quantum.solutions.quantumsolutions.model.NodoEmpleado;
import org.quantum.solutions.quantumsolutions.model.TipoSoporte;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

public class EmpleadoListaEnlazada {

    private NodoEmpleado encabezado;
    private ValidateData validateInfo = new ValidateData();

    /* ---------- Operaciones CRUD Ordenadas por Nombre ---------- */

    public void agregar(Empleado emp) {
        try {
            String nombreValido = validateInfo.normalizeAndValidateName(emp.getNombre());
            emp.setNombre(nombreValido);
        } catch (IllegalArgumentException e) {
                validateInfo.showError("Nombre inválido: " + e.getMessage());
            return;
        }

        try {
            String dpiValido = validateInfo.validarDpi(emp.getDpi());
            emp.setDpi(dpiValido);
        } catch (IllegalArgumentException e) {
            validateInfo.showError("DPI inválido: " + e.getMessage());
            return;
        }

        try {
            TipoSoporte tipoSoporteValido = validateInfo.validarTipoSoporte(emp.getTipoSoporte().toString());
            emp.setTipoSoporte(tipoSoporteValido);
        } catch (IllegalArgumentException e) {
            validateInfo.showError("Tipo soporte inválido: " + e.getMessage());
            return;
        }

        if (encabezado == null || emp.getNombre().compareToIgnoreCase(encabezado.data.getNombre()) < 0) {
            NodoEmpleado n = new NodoEmpleado(emp); n.siguiente = encabezado; encabezado = n; return;
        }
        NodoEmpleado curr = encabezado;

        while (
                curr.siguiente != null &&
                curr.siguiente.data.getNombre().compareToIgnoreCase(emp.getNombre()) < 0
        ) {
            curr = curr.siguiente;
        }

        NodoEmpleado n = new NodoEmpleado(emp);
        n.siguiente = curr.siguiente;
        curr.siguiente = n;
    }

    public ObservableList<Empleado> listarEmpleados() {
        ObservableList<Empleado> list = FXCollections.observableArrayList();
        NodoEmpleado curr = encabezado;
        while (curr != null) { list.add(curr.data); curr = curr.siguiente; }
        return list;
    }

    public boolean eliminar(String dpi) {
        if (encabezado == null) {
            return false;
        };

        if (encabezado.data.getDpi().equals(dpi)) {
            encabezado = encabezado.siguiente; return true;
        }

        NodoEmpleado curr = encabezado;
        while (
                curr.siguiente != null &&
                !curr.siguiente.data.getDpi().equals(dpi)
        ) {
            curr = curr.siguiente;
        }

        if (curr.siguiente == null) {
            return false;
        };

        curr.siguiente = curr.siguiente.siguiente;
        return true;
    }

    public boolean actualizar(Empleado actualizado) {
        try {
            String nombreValido = validateInfo.normalizeAndValidateName(actualizado.getNombre());
            actualizado.setNombre(nombreValido);
        } catch (IllegalArgumentException e) {
            validateInfo.showError("Nombre inválido: " + e.getMessage());
            return false;
        }

        try {
            String dpiValido = validateInfo.validarDpi(actualizado.getDpi());
            actualizado.setDpi(dpiValido);
        } catch (IllegalArgumentException e) {
            validateInfo.showError("DPI inválido: " + e.getMessage());
            return false;
        }

        try {
            TipoSoporte tipoSoporteValido = validateInfo.validarTipoSoporte(actualizado.getTipoSoporte().toString());
            actualizado.setTipoSoporte(tipoSoporteValido);
        } catch (IllegalArgumentException e) {
            validateInfo.showError("Tipo soporte inválido: " + e.getMessage());
            return false;
        }

        eliminar(actualizado.getDpi());
        agregar(actualizado);
        return true;
    }

    /* ---------- Funcionalidades CSV ---------- */

    private static final String SeparadorCSV = ",";

    public void cargar(Path file) throws IOException {
        if (!Files.exists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer t = new StringTokenizer(line, SeparadorCSV);
                if (t.countTokens() != 3) continue;
                String dpi = t.nextToken();
                String nombre = t.nextToken();
                TipoSoporte tipoSoporte;

                try {
                    tipoSoporte = validateInfo.validarTipoSoporte(t.nextToken());
                } catch (IllegalArgumentException err) {
                    validateInfo.showError(err.getMessage());
                    continue;
                }

                agregar(new Empleado(
                        dpi,
                        nombre,
                        tipoSoporte
                ));
            }
        }
    }

    public void exportar(Path file) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            NodoEmpleado curr = encabezado;
            while (curr != null) {
                Empleado e = curr.data;
                bw.write(String.join(SeparadorCSV,
                        e.getDpi(),
                        e.getNombre(),
                        e.getTipoSoporte().toString()));
                bw.newLine();
                curr = curr.siguiente;
            }
        }
    }
}