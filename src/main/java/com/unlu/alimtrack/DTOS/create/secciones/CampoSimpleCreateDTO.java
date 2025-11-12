package com.unlu.alimtrack.DTOS.create.secciones;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record CampoSimpleCreateDTO(
        @NotNull
        @Size(min = 2, max = 255)
        String nombre,

        @NotNull
        @Size(min = 2, max = 100)
        String tipoDato,

        Integer idGrupo,

        @NotNull
        Integer orden
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampoSimpleCreateDTO that = (CampoSimpleCreateDTO) o;
        return Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
