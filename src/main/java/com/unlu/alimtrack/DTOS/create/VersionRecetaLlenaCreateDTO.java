package com.unlu.alimtrack.DTOS.create;

import com.unlu.alimtrack.DTOS.create.secciones.SeccionCreateDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

public record VersionRecetaLlenaCreateDTO(
        @NotNull
        @Size(min = 2, max = 255, message = "El codigoRecetaPadre debe tener entre 2 y 255 caracteres")
        String codigoRecetaPadre,

        @NotNull
        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
        String codigoVersionReceta,

        @NotNull
        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
        String nombre,

        @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres")
        String descripcion,

        @NotNull
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String usernameCreador,

        @NotNull
        @UniqueElements(message = "Los títulos de las secciones deben ser únicos")
        List<SeccionCreateDTO> secciones
) {
}
