package com.unlu.alimtrack.DTOS.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record SeccionResponseDTO(
        @NotNull
        Long idSeccion,
        @NotNull
        String codigoVersion,
        @NotNull
        String usernameCreador,
        @NotNull
        String titulo,
        @NotNull
        Integer orden,
        List<CampoSimpleResponseDTO> camposSimples,
        List<GrupoCamposResponseDTO> gruposCampos,
        List<TablaResponseDTO> tablas
) {

}