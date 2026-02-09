package com.unlu.alimtrack.DTOS.create.secciones;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Objects;

public record GrupoCamposCreateDTO(

        @NotNull
        @Size(min = 2, max = 255)
        String subtitulo,

        @NotNull
        Integer orden,

        @UniqueElements(message = "Los nombres de los campos simples deben ser únicos")
        List<CampoSimpleCreateDTO> camposSimples


) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrupoCamposCreateDTO that = (GrupoCamposCreateDTO) o;
        return Objects.equals(subtitulo, that.subtitulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtitulo);
    }

}
