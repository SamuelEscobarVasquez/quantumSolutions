package org.quantum.solutions.quantumsolutions.model;

public class Empleado {
    private String dpi;
    private String nombre;
    private TipoSoporte tipoSoporte;

    public Empleado(String pDPI, String pNombre, TipoSoporte pTipoSoporte) {
        this.dpi = pDPI;
        this.nombre = pNombre;
        this.tipoSoporte = pTipoSoporte;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoSoporte getTipoSoporte() {
        return tipoSoporte;
    }

    public void setTipoSoporte(TipoSoporte tipoSoporte) {
        this.tipoSoporte = tipoSoporte;
    }
}