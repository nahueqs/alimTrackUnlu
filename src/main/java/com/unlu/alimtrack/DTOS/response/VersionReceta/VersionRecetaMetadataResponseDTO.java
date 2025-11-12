package com.unlu.alimtrack.DTOS.response.VersionReceta;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VersionRecetaMetadataResponseDTO(
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
        LocalDateTime fechaCreacion,

        Integer totalSecciones,

        Integer totalCampos,

        Integer totalTablas

) {

}
