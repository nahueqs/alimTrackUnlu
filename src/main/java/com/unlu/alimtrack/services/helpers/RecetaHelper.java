package com.unlu.alimtrack.services.helpers;

public class RecetaHelper {

    private String generarCodigoUnicoReceta() {
        // RC- + 4 dígitos aleatorios
        return "RC-" + String.format("%04d", (int) (Math.random() * 10000));
    }
}
