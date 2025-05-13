package org.quantum.solutions.quantumsolutions;

public class Employee {
    private String dpi;
    private String fullName;
    private SupportType supportType;

    public Employee(String dpi, String fullName, SupportType supportType) {
        this.dpi = dpi;
        this.fullName = fullName;
        this.supportType = supportType;
    }
    /* getters y setters ↓ */
    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }
    public String getFullName() { return fullName; }
    public void setFullName(String n) { this.fullName = n; }
    public SupportType getSupportType() { return supportType; }
    public void setSupportType(SupportType s) { this.supportType = s; }
}