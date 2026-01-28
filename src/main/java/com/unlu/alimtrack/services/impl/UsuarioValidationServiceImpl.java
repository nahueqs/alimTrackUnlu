package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.UsuarioValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de validación de usuarios.
 * Centraliza la lógica para verificar si un usuario existe y está autorizado (activo) para realizar operaciones.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioValidationServiceImpl implements UsuarioValidationService {

    private final UsuarioService usuarioService;

    /**
     * Valida que un usuario exista y esté activo.
     *
     * @param email Email del usuario a validar.
     * @return El modelo del usuario validado.
     * @throws RecursoNoEncontradoException Si el usuario no existe.
     * @throws OperacionNoPermitida Si el usuario no está activo.
     */
    @Override
    public UsuarioModel validarUsuarioAutorizado(String email) {
        log.debug("Iniciando validación de autorización para usuario: {}", email);

        UsuarioModel usuario = obtenerUsuario(email);
        validarUsuarioActivo(usuario);

        log.debug("Usuario {} validado y autorizado exitosamente.", email);
        return usuario;
    }

    /**
     * Verifica si un modelo de usuario está activo.
     *
     * @param usuario El modelo del usuario.
     * @return El mismo modelo si está activo.
     * @throws IllegalArgumentException Si el usuario es nulo.
     * @throws OperacionNoPermitida Si el usuario no está activo.
     */
    @Override
    public UsuarioModel validarUsuarioActivo(UsuarioModel usuario) {
        if (usuario == null) {
            log.error("Intento de validar usuario nulo.");
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        if (!usuario.getEstaActivo()) {
            log.warn("Usuario inactivo intentando realizar operación: {}", usuario.getEmail());
            throw new OperacionNoPermitida(
                    String.format("El usuario %s no está activo", usuario.getEmail()));
        }

        return usuario;
    }

    private UsuarioModel obtenerUsuario(String email) {
        try {
            return usuarioService.getUsuarioModelByEmail(email);
        } catch (RecursoNoEncontradoException e) {
            log.warn("Usuario no encontrado durante validación: {}", email);
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener usuario con email: {}", email, e);
            throw new RecursoNoEncontradoException(
                    String.format("Error al buscar usuario con email: %s", email));
        }
    }
}
