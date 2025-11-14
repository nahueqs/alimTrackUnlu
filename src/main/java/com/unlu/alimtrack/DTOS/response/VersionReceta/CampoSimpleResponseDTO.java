package com.unlu.alimtrack.DTOS.response.VersionReceta;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import jakarta.validation.constraints.NotNull;


public record CampoSimpleResponseDTO(
        @NotNull
        Long id,

        @NotNull
        Long idSeccion,

        Long idGrupo,

        @NotNull
        String nombre,

        @NotNull
        TipoDatoCampo tipoDato,

        @NotNull
        Integer orden
) {

}
