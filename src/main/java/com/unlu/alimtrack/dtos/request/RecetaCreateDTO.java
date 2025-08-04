package com.unlu.alimtrack.dtos.request;

import jakarta.validation.constraints.NotNull;

// DTO usado para recibir la peticion de crear una nueva Receta

public record RecetaCreateDTO ( @NotNull Long id, String nombre, String descripcion) {
}
