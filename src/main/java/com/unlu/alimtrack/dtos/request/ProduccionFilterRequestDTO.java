package com.unlu.alimtrack.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


/**
 * DTO para filtros de búsqueda de producciones. Todos los parámetros son opcionales y combinables.
 *
 * @param codigoVersionReceta Código de versión de receta (opcional)
 * @param lote                Número de lote (opcional)
 * @param encargado           Nombre del encargado (opcional)
 * @param fechaInicio         Fecha de inicio para rango (opcional)
 * @param fechaFin            Fecha de fin para rango (opcional)
 * @param estado              Estado de producción (opcional)
 */
@Schema(description = "Filtros para búsqueda de producciones")
public record ProduccionFilterRequestDTO(

        @Schema(description = "Código de versión de receta para filtrar", example = "REC-V1-2024")
        @Size(max = 50, message = "El código de versión no puede exceder 50 caracteres")
        String codigoVersionReceta,

        @Schema(description = "Número de lote para filtrar", example = "LOTE-2024-001")
        @Pattern(regexp = "^[A-Z0-9-]{0,20}$", message = "Formato de lote inválido. Solo mayúsculas, números y guiones")
        String lote,

        @Schema(description = "Nombre del encargado para filtrar", example = "María González")
        @Size(max = 100, message = "El nombre del encargado no puede exceder 100 caracteres")
        String encargado,

        @Schema(description = "Fecha de inicio para rango de búsqueda (YYYY-MM-DD)", example = "2024-01-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        LocalDate fechaInicio,

        @Schema(description = "Fecha de fin para rango de búsqueda (YYYY-MM-DD)", example = "2024-01-31")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
        LocalDate fechaFin,

        @Schema(description = "Estado de producción para filtrar",
                allowableValues = {"EN_PROCESO", "FINALIZADA"},
                example = "EN_PROCESO")
        @Pattern(regexp = "(?i)^(EN_PROCESO|FINALIZADA)?$", message = "Estado debe ser: EN_PROCESO, FINALIZADA")
        String estado) {

    public ProduccionFilterRequestDTO {
        validateDateConsistency(fechaInicio, fechaFin);
    }

    /**
     * Valida consistencia entre fechas de inicio y fin
     */
    private void validateDateConsistency(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
    }
}