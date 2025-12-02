package com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura;

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
        String tipoDato,

        @NotNull
        Integer orden
) {

}
