package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(@Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
                               @NotNull String nombre,
                               @NotNull String email,
                               @Size(min = 6, max=61, message = "La contraseña debe tener al menos 6 caracteres")
                               @NotNull String contraseña,
                               @NotNull Boolean esAdmin) {
}
