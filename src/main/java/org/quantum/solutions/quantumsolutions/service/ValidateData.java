package org.quantum.solutions.quantumsolutions.service;

import javafx.scene.control.Alert;
import org.quantum.solutions.quantumsolutions.model.TipoSoporte;

public class ValidateData {

    public String normalizeAndValidateName(String rawName) throws IllegalArgumentException {
        String nombre = rawName.trim().replaceAll("\\s+", " ");
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException("El nombre solo debe contener letras y espacios.");
        }
        return nombre;
    }

    public String validarDpi(String pDPI) {
        if (pDPI == null || pDPI.isBlank()) {
            throw new IllegalArgumentException("El DPI no puede estar vacío.");
        }

        String dpi = pDPI.trim();
        if (!dpi.matches("\\d{13}")) {
            throw new IllegalArgumentException("El DPI debe contener exactamente 13 dígitos.");
        }

        validaDigitoVerificador(dpi);

        return dpi;
    }

    private String validaDigitoVerificador(String pDPI) {
        // Calcula la suma acumulada de los primeros 8 dígitos
        int suma = 0;
        for (int i = 0; i < 8; i++) {
            int digito = Character.getNumericValue(pDPI.charAt(i));
            int num   = 9 - i;
            suma += digito * num;
        }

        // Aplica el módulo y la resta a 11
        int resto = suma % 11;
        int calculado = 11 - resto;
        if (calculado == 11) {
            calculado = 0;
        }

        int dvReal = Character.getNumericValue(pDPI.charAt(8));

        if (calculado != dvReal) {
            throw new IllegalArgumentException( "DPI inválido: dígito verificador incorrecto (esperado " + calculado + " pero se encontró " + dvReal + ").");
        }

        return pDPI;
    }

    public TipoSoporte validarTipoSoporte(String tipoSoporte) {
        if (tipoSoporte == null || tipoSoporte.toString().isBlank()) {
            throw new IllegalArgumentException("Debe especificar el tipo de soporte.");
        }

        String tipo = tipoSoporte.toString().trim();
        if (!TipoSoporte.isValid(tipo)) {
            throw new IllegalArgumentException("Tipo de soporte no válido: " + tipo);
        }

        return TipoSoporte.ofNullable(tipoSoporte);
    }

    public void showError(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}