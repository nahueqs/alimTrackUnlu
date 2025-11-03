package com.unlu.alimtrack.DTOS.create.secciones;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Objects;


/**
 * DTO para la creación de una nueva sección
 */
@Builder
public record SeccionCreateDTO(

        @NotBlank(message = "El codigoVersionRecetaPadre de la sección es obligatorio")
        @NotNull
        String codigoVersionRecetaPadre,

        @NotNull
        @NotBlank(message = "El usernameCreador de la sección es obligatorio")
        String usernameCreador,

        @NotBlank(message = "El título de la sección es obligatorio")
        @NotNull
        String titulo,

        @NotBlank(message = "El tipo de la sección es obligatorio")
        @NotNull
        String tipo,

        @NotBlank(message = "El orden de la sección es obligatorio")
        @NotNull
        Integer orden,

        @UniqueElements(message = "Los subtítulos de los grupos de campos deben ser únicos")
        List<GrupoCamposCreateDTO> gruposCampos,

        @UniqueElements(message = "Los nombres de los campos simples deben ser únicos")
        List<CampoSimpleCreateDTO> camposSimples,

        @UniqueElements(message = "Los nombres de las tablas deben ser únicos")
        List<TablaCreateDTO> tablas


) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeccionCreateDTO that = (SeccionCreateDTO) o;
        return Objects.equals(titulo, that.titulo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo);
    }
}
