package com.unlu.alimtrack.DTOS.response.Usuario;

import jakarta.validation.constraints.NotNull;

public record UsuarioResponseDTO(
        @NotNull String email,
        @NotNull String nombre,
        @NotNull String rol

) {
}
