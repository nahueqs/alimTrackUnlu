// UsuarioValidationServiceImpl.java
package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.UsuarioValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioValidationServiceImpl implements UsuarioValidationService {

    private final UsuarioService usuarioService;

    @Override
    public UsuarioModel validarUsuarioAutorizado(String email) {
        log.debug("Validando usuario autorizado con email: {}", email);

        UsuarioModel usuario = obtenerUsuario(email);
        validarUsuarioActivo(usuario);

        log.debug("Usuario {} validado exitosamente", email);
        return usuario;
    }

    @Override
    public UsuarioModel validarUsuarioActivo(UsuarioModel usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        if (!usuario.getEstaActivo()) {
            throw new OperacionNoPermitida(
                    String.format("El usuario %s no está activo", usuario.getEmail()));
        }

        return usuario;
    }

    private UsuarioModel obtenerUsuario(String email) {
        try {
            return usuarioService.getUsuarioModelByEmail(email);
        } catch (Exception e) {
            log.error("Error al obtener usuario con email: {}", email, e);
            throw new RecursoNoEncontradoException(
                    String.format("Usuario no encontrado con email: %s", email));
        }
    }
}