package org.quantum.solutions.quantumsolutions.model;

import java.util.Arrays;

public enum TipoSoporte {
    TECNICO_REPARACION_PCS("Tecnico reparacion PCs"),
    TECNICO_APLICATIVOS("Tecnico aplicativos"),
    TECNICO_REDES_INTERNET("Tecnico redes/Internet");

    private final String label;

    TipoSoporte(String l) { this.label = l; }

    @Override public String toString() {
        return label;
    }

    public static boolean isValid(String tipoSoporte) {
        System.out.println(tipoSoporte);
        return ofNullable(tipoSoporte) != null;
    }

    public static TipoSoporte ofNullable(String nombre) {
        if (nombre == null) return null;
        return Arrays.stream(values())
                .filter(t -> {
                    System.out.println(t.label);
                    System.out.println(nombre.trim());
                    System.out.println(t.label.equalsIgnoreCase(nombre.trim()));
                    return t.label.equalsIgnoreCase(nombre.trim());
                })
                .findFirst()
                .orElse(null);
    }
}