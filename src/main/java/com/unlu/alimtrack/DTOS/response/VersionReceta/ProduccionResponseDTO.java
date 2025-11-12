package com.unlu.alimtrack.DTOS.response.VersionReceta;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProduccionResponseDTO(
        @NotNull
        String codigoProduccion,
        @NotNull
        String codigoVersion,
        String encargado,
        String usernameCreador,
        String lote,
        @NotNull
        String estado,
        @NotNull
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        String observaciones
) {

}
