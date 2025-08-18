package com.unlu.alimtrack.dtos.modify;

import jakarta.validation.constraints.Size;

public record UsuarioModifyDTO(@Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
                               String nombre,
                               @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
                               String contraseña) {
}

