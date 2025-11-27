package com.unlu.alimtrack.DTOS.response.VersionReceta;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SeccionResponseDTO(

        @NotNull
        Long id,
        @NotNull
        String codigoVersion,
        @NotNull
        String titulo,
        @NotNull
        Integer orden,

        List<CampoSimpleResponseDTO> camposSimples,

        List<GrupoCamposResponseDTO> gruposCampos,

        List<TablaResponseDTO> tablas
) {

}