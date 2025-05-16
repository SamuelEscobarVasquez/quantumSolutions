package org.quantum.solutions.quantumsolutions.model;

public class NodoEmpleado {
    public Empleado data;
    public NodoEmpleado siguiente;

    public NodoEmpleado(Empleado e) { this.data = e; }
}