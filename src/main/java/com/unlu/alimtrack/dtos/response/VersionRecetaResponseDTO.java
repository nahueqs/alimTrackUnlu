package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record VersionRecetaResponseDTO(@NotNull Long id, String nombre, String descripcion, Instant fechaCreacion,
                                       String nombreRecetaPadre, String creadaPor) {

}
