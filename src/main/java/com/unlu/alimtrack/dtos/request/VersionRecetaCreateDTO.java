package com.unlu.alimtrack.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

// DTO usado para recibir la peticion de crear una nueva version

public record VersionRecetaCreateDTO(@NotNull String nombre, String descripcion, Instant fechaCreacion,
                                     @NotNull Long idUsuarioCreador) {
}
