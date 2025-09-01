package com.unlu.alimtrack.services.helpers;

import com.unlu.alimtrack.exception.FechaFormatException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

@Component
public class DateHelper {

    private static final String[] FORMATOS_ACEPTADOS = {
            "YYYY-MM-DD", "YYYY-MM-DDTHH:mm:ssZ", "YYYY-MM-DDTHH:mm:ss.SSSZ"
    };

    /**
     * Parsea y valida una fecha string a Instant
     */
    public Instant parseAndValidateFecha(String fechaStr) {
        if (fechaStr == null) return null;

        try {
            if (fechaStr.contains("T")) {
                return Instant.parse(fechaStr);
            } else {
                LocalDate localDate = LocalDate.parse(fechaStr);
                return localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            }
        } catch (DateTimeParseException e) {
            throw new FechaFormatException(
                    fechaStr,
                    String.join(" o ", FORMATOS_ACEPTADOS)
            );
        }
    }

    /**
     * Valida coherencia en rango de fechas
     */
    public void validateRangoFechas(Instant inicio, Instant fin, String paramInicio, String paramFin) {
        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            throw new IllegalArgumentException(
                    String.format("La %s (%s) no puede ser posterior a la %s (%s)",
                            paramInicio, inicio, paramFin, fin)
            );
        }
    }


}