package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;

public record UsuarioResponseDTO(
        @NotNull String username,
        @NotNull String nombre,
        @NotNull String email,
        @NotNull String rol

) {
}
