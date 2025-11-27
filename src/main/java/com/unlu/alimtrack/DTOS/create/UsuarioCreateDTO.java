package com.unlu.alimtrack.DTOS.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(

        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @NotNull
        String nombre,

        @NotNull @Size(min = 1, max = 50)
        @Schema(description = "email del creador de la producción", example = "JuanPerez1@mail.com")
        String email,

        @Size(min = 6, max = 61, message = "La password debe tener al menos 6 caracteres")
        @NotNull
        String contraseña) {

}
