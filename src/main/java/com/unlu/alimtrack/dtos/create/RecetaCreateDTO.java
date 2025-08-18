package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;

// DTO usado para recibir la peticion de crear una nueva Receta

public record RecetaCreateDTO(@NotNull String codigoReceta, @NotNull String nombre, String descripcion, @NotNull Long idUsuarioCreador) {
}
