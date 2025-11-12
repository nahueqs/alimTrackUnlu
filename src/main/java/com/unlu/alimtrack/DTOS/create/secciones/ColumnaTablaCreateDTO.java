package com.unlu.alimtrack.DTOS.create.secciones;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ColumnaTablaCreateDTO(

        @NotNull
        @Size(min = 2, max = 255)
        String nombre,

        @NotNull
        @Size(min = 2, max = 100)
        String tipoDato,

        @NotNull Integer orden

) {
}
