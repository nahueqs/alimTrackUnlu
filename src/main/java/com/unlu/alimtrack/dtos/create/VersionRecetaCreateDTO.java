package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;

// DTO usado para recibir la peticion de crear una nueva version

public record VersionRecetaCreateDTO(String codigoVersionReceta, @NotNull String nombre, String descripcion,
                                     @NotNull Long idUsuarioCreador) {
}
