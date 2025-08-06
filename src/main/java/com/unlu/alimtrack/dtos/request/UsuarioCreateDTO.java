package com.unlu.alimtrack.dtos.request;

import jakarta.validation.constraints.NotNull;

public record UsuarioCreateDTO(@NotNull String nombre, @NotNull String email, @NotNull String contraseña,
                               @NotNull Boolean esAdmin) {
}
