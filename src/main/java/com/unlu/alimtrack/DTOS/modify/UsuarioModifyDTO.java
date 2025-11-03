package com.unlu.alimtrack.DTOS.modify;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioModifyDTO(

        @Size(min = 2, max = 50, message = "El nombre de usuario debe tener entre 2 y 50 caracteres")
        String username,

        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @Size(min = 8, message = "La password debe tener al menos 8 caracteres")
        String contraseña,

        @Email @Size(min = 2, max = 100, message = "El email debe tener entre 2 y 100 caracteres")
        String email
) {

}

