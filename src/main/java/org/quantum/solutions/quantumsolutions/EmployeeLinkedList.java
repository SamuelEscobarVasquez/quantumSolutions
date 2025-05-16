package org.quantum.solutions.quantumsolutions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

public class EmployeeLinkedList {

    private EmployeeNode head;

    /* ---------- operaciones CRUD ordenadas por nombre ---------- */

    public void add(Employee e) {
        if (head == null || e.getFullName().compareToIgnoreCase(head.data.getFullName()) < 0) {
            EmployeeNode n = new EmployeeNode(e); n.next = head; head = n; return;
        }
        EmployeeNode curr = head;
        while (curr.next != null &&
                curr.next.data.getFullName().compareToIgnoreCase(e.getFullName()) < 0) {
            curr = curr.next;
        }
        EmployeeNode n = new EmployeeNode(e);
        n.next = curr.next;
        curr.next = n;
    }

    public boolean remove(String dpi) {
        if (head == null) return false;
        if (head.data.getDpi().equals(dpi)) { head = head.next; return true; }
        EmployeeNode curr = head;
        while (curr.next != null && !curr.next.data.getDpi().equals(dpi))
            curr = curr.next;
        if (curr.next == null) return false;
        curr.next = curr.next.next;
        return true;
    }

    public boolean update(Employee updated) {
        EmployeeNode n = head;
        remove(updated.getDpi());     // quita el viejo
        add(updated);                 // re-inserta respetando orden
        return true;

    }

    /* ---------- utilidades CSV ---------- */

    private static final String CSV_SEPARATOR = ",";

    public void load(Path file) throws IOException {
        if (!Files.exists(file)) return;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                StringTokenizer t = new StringTokenizer(line, CSV_SEPARATOR);
                if (t.countTokens() != 3) continue;
                add(new Employee(
                        t.nextToken(),
                        t.nextToken(),
                        SupportType.valueOf(t.nextToken())
                ));
            }
        }
    }

    public void save(Path file) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(file)) {
            EmployeeNode curr = head;
            while (curr != null) {
                Employee e = curr.data;
                bw.write(String.join(CSV_SEPARATOR,
                        e.getDpi(),
                        e.getFullName(),
                        e.getSupportType().name()));
                bw.newLine();
                curr = curr.next;
            }
        }
    }

    public ObservableList<Employee> asObservableList() {
        ObservableList<Employee> list = FXCollections.observableArrayList();
        EmployeeNode curr = head;
        while (curr != null) { list.add(curr.data); curr = curr.next; }
        return list;
    }
}