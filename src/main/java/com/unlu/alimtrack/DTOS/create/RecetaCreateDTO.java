package com.unlu.alimtrack.DTOS.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO usado para recibir la peticion de crear una nueva Receta

public record RecetaCreateDTO(

        @NotNull
        @Size(min = 2, max = 255)
        String codigoReceta,

        @NotNull
        @Size(min = 2, max = 255)
        String nombre,

        @Size(max = 255)
        String descripcion,

        @NotNull @Size(min = 1, max = 50)
        @Schema(description = "email del creador de la producción", example = "JuanPerez1@mail.com")
        String emailCreador) {


}
