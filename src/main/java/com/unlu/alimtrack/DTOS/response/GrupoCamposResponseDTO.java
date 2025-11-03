package com.unlu.alimtrack.DTOS.response;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GrupoCamposResponseDTO(
        @NotNull
        Integer id,
        @NotNull
        Integer idSeccion,
        @NotNull
        String nombre,
        @NotNull
        String subtitulo,
        @NotNull
        Integer orden,
        @NotNull
        List<CampoSimpleResponseDTO> campos

) {
}
