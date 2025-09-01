package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;

public record UsuarioResponseDTO(@NotNull Long id, @NotNull String nombre, @NotNull String email) {

}
