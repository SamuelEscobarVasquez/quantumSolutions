package org.quantum.solutions.quantumsolutions;

public class empleado {
    private String dpi;
    private String Nombre;
    private SupportType Psoporte;

    public empleado(String dpi, String Nombre, SupportType Psoporte) {
        this.dpi = dpi;
        this.Nombre = Nombre;
        this.Psoporte = Psoporte;
    }
    /* getters y setters ↓ */
    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }
    public String getNombre() { return Nombre; }
    public void setNombre(String n) { this.Nombre = n; }
    public SupportType getPsoporte() { return Psoporte; }
    public void setPsoporte(SupportType s) { this.Psoporte = s; }
}