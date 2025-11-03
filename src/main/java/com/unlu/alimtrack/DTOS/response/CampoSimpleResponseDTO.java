package com.unlu.alimtrack.DTOS.response;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import jakarta.validation.constraints.NotNull;


public record CampoSimpleResponseDTO(
        @NotNull
        Long id,
        @NotNull
        Long idSeccion,
        Long idGrupoCampos,
        @NotNull
        String nombre,
        @NotNull
        TipoDatoCampo tipoDato,  // ← Usar el enum, no String
        @NotNull
        Integer orden
) {

}
