package com.unlu.alimtrack.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(


        @Size(min = 2, max = 50, message = "El usuario debe tener entre 2 y 50 caracteres")
        @NotNull
        String username,

        @NotNull(message = "El nombre debe tener entre 2 y 100 caracteres")
        @Email
        String email,

        @Size(min = 6, max = 61, message = "La password debe tener al menos 6 caracteres")
        @NotNull
        String password,

        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        @NotNull
        String nombre
) {
}
