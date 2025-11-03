package com.unlu.alimtrack.DTOS.response;

import jakarta.validation.constraints.NotNull;

public record TablaResponseDTO(
        @NotNull
        Long idTabla,
        @NotNull
        Long idSeccion,
        @NotNull
        String nombre,
        String descripcion,
        @NotNull
        Integer orden


) {
}
