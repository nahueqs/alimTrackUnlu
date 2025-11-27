package com.unlu.alimtrack.DTOS.response.produccion.protegido;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProduccionMetadataResponseDTO(
        @NotNull
        String codigoProduccion,
        @NotNull
        String codigoVersion,
        String encargado,
        String emailCreador,
        String lote,
        @NotNull
        String estado,
        @NotNull
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        String observaciones
) {

}
