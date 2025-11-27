package com.unlu.alimtrack.DTOS.response;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RecetaMetadataResponseDTO(
        @NotNull
        String codigoReceta,
        @NotNull
        String descripcion,
        @NotNull
        String nombre,
        String creadaPor,
        @NotNull
        LocalDateTime fechaCreacion
) {

}
