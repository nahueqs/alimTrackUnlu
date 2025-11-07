package com.unlu.alimtrack.DTOS.response;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import jakarta.validation.constraints.NotNull;

public record ColumnaTablaResponseDTO(

        @NotNull
        String id,
        @NotNull
        String idTabla,
        @NotNull
        String nombre,
        @NotNull
        Integer orden,
        @NotNull
        TipoDatoCampo tipoDato
) {
}
