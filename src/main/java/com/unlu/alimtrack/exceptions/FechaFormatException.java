package com.unlu.alimtrack.exceptions;

public class FechaFormatException extends RuntimeException {

    public FechaFormatException(String fecha, String formatoEsperado) {
        super("Formato de fecha inválido: " + fecha + ". Formato esperado: " + formatoEsperado +
                "FECHA_FORMAT_INVALID");
    }

}
