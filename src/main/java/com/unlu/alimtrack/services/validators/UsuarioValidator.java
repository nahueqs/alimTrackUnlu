package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.dtos.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.UsuarioModel;
import jakarta.validation.ValidationException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class UsuarioValidator {

    public void validateUsuario(UsuarioModel usuario) {
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario no existente");
        }

        if (!usuario.getEstaActivo()) {
            throw new ValidationException("Usuario username" + usuario.getUsername() + " está inactivo.");
        }
    }

    public void validateListUsuarios(List<UsuarioModel> usuarios) {
        if (usuarios.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen usuarios registrados");
        }
    }

    public void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }

        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("El nombre de usuario debe tener entre 3 y 50 caracteres.");
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("El nombre de usuario sólo puede contener letras, numeros y guiones bajos.");
        }

        if (username.contains("@")) {
            throw new IllegalArgumentException("El nombre de usuario no puede contener @");
        }
    }

    public void validarModificacion(UsuarioModifyDTO modificacion) {

        if (modificacion.nombre() != null || modificacion.contraseña() != null) {
            throw new ModificacionInvalidaException("No se puede realizar la modificacion solicitada");
        }

        if (modificacion.username() != null) {
            validateUsername(modificacion.username());
        }


    }


}
