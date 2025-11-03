package com.unlu.alimtrack.DTOS.response;

import jakarta.validation.constraints.NotNull;

public record FilaTablaResponseDTO(
        @NotNull
        String id,
        @NotNull
        String idTabla,
        @NotNull
        String nombre,
        @NotNull
        int orden
) {
}
