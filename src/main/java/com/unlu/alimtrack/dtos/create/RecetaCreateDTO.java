package com.unlu.alimtrack.dtos.create;

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

    @NotNull
    @Size(min = 2, max = 50)
    String usernameCreador) {

}
