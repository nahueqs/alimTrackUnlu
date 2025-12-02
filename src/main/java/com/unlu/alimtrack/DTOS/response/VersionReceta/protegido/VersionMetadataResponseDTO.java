package com.unlu.alimtrack.DTOS.response.VersionReceta.protegido;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VersionMetadataResponseDTO(
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
