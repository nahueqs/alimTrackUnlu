package com.unlu.alimtrack.DTOS.response;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VersionRecetaResponseDTO(
        @NotNull
        String codigoVersionReceta,

        @NotNull
        String codigoRecetaPadre,

        @NotNull
        String nombreRecetaPadre,

        @NotNull
        String nombre,

        String descripcion,

        @NotNull
        String creadaPor,

        @NotNull
        LocalDateTime fechaCreacion
) {

}
