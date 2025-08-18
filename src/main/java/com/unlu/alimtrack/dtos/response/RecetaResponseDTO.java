package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record RecetaResponseDTO(@NotNull String codigoReceta, String nombre, String descripcion,
                                Instant fechaCreacion, String creadaPor) {
}
