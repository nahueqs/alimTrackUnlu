package com.unlu.alimtrack.DTOS.response;

import jakarta.validation.constraints.NotNull;

public record UsuarioResponseDTO(
        @NotNull String username,
        @NotNull String nombre,
        @NotNull String email,
        @NotNull String rol

) {
}
