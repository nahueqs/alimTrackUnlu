package com.unlu.alimtrack.DTOS.create.secciones;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FilaTablaCreateDTO(
        @NotNull
        @Size(min = 2, max = 255)
        String nombre

) {
}
