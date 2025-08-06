package com.unlu.alimtrack.dtos.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull String email,
        @NotNull String contraseña
) {
}