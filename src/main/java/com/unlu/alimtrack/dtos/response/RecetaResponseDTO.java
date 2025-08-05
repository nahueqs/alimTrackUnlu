package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record RecetaResponseDTO(@NotNull Long id, String nombre,  String descripcion, Instant fechaCreacion, String creadaPor) {
}
