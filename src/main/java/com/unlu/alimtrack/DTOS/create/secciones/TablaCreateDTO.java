package com.unlu.alimtrack.DTOS.create.secciones;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Objects;

public record TablaCreateDTO(
        @NotNull
        @Size(min = 2, max = 255)
        String nombre,

        @Size(min = 2, max = 255)
        String descripcion,

        @UniqueElements(message = "Los nombres de las filas deben ser únicos")
        List<FilaTablaCreateDTO> filas,

        @UniqueElements(message = "Los nombres de las columnas deben ser únicos")
        List<ColumnaTablaCreateDTO> columnas,

        @NotNull Integer orden

) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TablaCreateDTO that = (TablaCreateDTO) o;
        return Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
