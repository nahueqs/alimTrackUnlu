package com.unlu.alimtrack.DTOS.response.VersionReceta;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GrupoCamposResponseDTO(
        @NotNull
        Long id,
        @NotNull
        Long idSeccion,

        @NotNull
        String subtitulo,
        @NotNull
        Integer orden,
        @NotNull
        List<CampoSimpleResponseDTO> campos

) {
}
