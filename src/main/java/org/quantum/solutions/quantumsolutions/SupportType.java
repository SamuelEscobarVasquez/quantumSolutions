package org.quantum.solutions.quantumsolutions;

public enum SupportType {
    PC_REPAIR("Técnico reparación PCs"),
    APPLICATION_SUPPORT("Técnico aplicativos"),
    NETWORK_SUPPORT("Técnico redes/Internet");

    private final String label;
    SupportType(String l) { this.label = l; }
    @Override public String toString() { return label; }
}