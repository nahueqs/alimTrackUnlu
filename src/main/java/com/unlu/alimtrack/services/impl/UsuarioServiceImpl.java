package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.exceptions.BorradoFallidoException;
import com.unlu.alimtrack.exceptions.ModificacionInvalidaException;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.UsuarioMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import com.unlu.alimtrack.services.RecetaQueryService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para la gestión de usuarios.
 * Maneja operaciones CRUD y validaciones relacionadas con los usuarios del sistema.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioValidator usuarioValidator;
    private final PasswordEncoder passwordEncoder;
    @Lazy
    private final RecetaQueryService recetaQueryService;
    @Lazy
    private final VersionRecetaQueryService versionRecetaQueryService;

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * @return Lista de DTOs con la información de los usuarios.
     * @throws RecursoNoEncontradoException Si no existen usuarios registrados.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios del sistema.");
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        
        if (usuarios.isEmpty()) {
            log.warn("No se encontraron usuarios en la base de datos.");
            throw new RecursoNoEncontradoException("No existen usuarios registrados");
        }
        
        log.debug("Retornando {} usuarios.", usuarios.size());
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param usuarioCreateDTO DTO con los datos del nuevo usuario.
     * @return DTO con la información del usuario creado.
     * @throws RecursoDuplicadoException Si el email ya está registrado.
     */
    @Override
    @Transactional
    public UsuarioResponseDTO addUsuario(UsuarioCreateDTO usuarioCreateDTO) {
        log.info("Iniciando creación de nuevo usuario con email: {}", usuarioCreateDTO.email());
        
        if (usuarioRepository.existsByEmail(usuarioCreateDTO.email())) {
            log.warn("Intento de crear usuario con email duplicado: {}", usuarioCreateDTO.email());
            throw new RecursoDuplicadoException("El email ya ha sido usado por un usuario existente: " + usuarioCreateDTO.email());
        }

        UsuarioModel usuarioModel = crearNuevoUsuarioByCreateDTO(usuarioCreateDTO);
        usuarioRepository.save(usuarioModel);
        
        log.info("Usuario {} creado y guardado exitosamente con ID: {}", usuarioModel.getEmail(), usuarioModel.getId());
        return usuarioMapper.toResponseDTO(usuarioModel);
    }

    private UsuarioModel crearNuevoUsuarioByCreateDTO(UsuarioCreateDTO usuario) {
        log.debug("Mapeando DTO a modelo y encriptando contraseña para {}", usuario.email());
        UsuarioModel usuarioModel = usuarioMapper.toModel(usuario);
        String passwordEncriptada = passwordEncoder.encode(usuario.contraseña());
        usuarioModel.setPassword(passwordEncriptada);
        return usuarioModel;
    }

    /**
     * Modifica los datos de un usuario existente.
     *
     * @param email Email del usuario a modificar.
     * @param modificacion DTO con los datos a modificar.
     * @throws ModificacionInvalidaException Si el nuevo email ya está en uso.
     * @throws RecursoNoEncontradoException Si el usuario no existe.
     */
    @Override
    @Transactional
    public void modifyUsuario(String email, UsuarioModifyDTO modificacion) {
        log.info("Modificando usuario con email: {}", email);
        
        usuarioValidator.validarModificacion(modificacion);
        UsuarioModel usuarioExistente = getUsuarioModelByEmail(email);
        
        validateUnicidadDatos(usuarioExistente, modificacion);
        aplicarModificaciones(usuarioExistente, modificacion);
        
        usuarioRepository.save(usuarioExistente);
        log.info("Usuario {} modificado exitosamente.", email);
    }

    private void aplicarModificaciones(UsuarioModel usuarioExistente, UsuarioModifyDTO modificacion) {
        log.debug("Aplicando modificaciones al modelo de usuario para {}", usuarioExistente.getEmail());
        usuarioMapper.updateModelFromModifyDTO(modificacion, usuarioExistente);
        
        if (modificacion.contraseña() != null) {
            log.debug("Actualizando contraseña para el usuario {}", usuarioExistente.getEmail());
            String passwordEncriptada = passwordEncoder.encode(modificacion.contraseña());
            usuarioExistente.setPassword(passwordEncriptada);
        }
    }

    private void validateUnicidadDatos(UsuarioModel usuarioExistente, UsuarioModifyDTO modificacion) {
        validateUnicidadEmail(usuarioExistente, modificacion.email());
    }

    private void validateUnicidadEmail(UsuarioModel usuarioExistente, String nuevoEmail) {
        if (nuevoEmail != null && !nuevoEmail.equals(usuarioExistente.getEmail())) {
            log.debug("Validando unicidad del nuevo email: {}", nuevoEmail);
            if (usuarioRepository.existsByEmail(nuevoEmail)) {
                log.warn("Intento de cambio de email a uno ya existente: {}", nuevoEmail);
                throw new ModificacionInvalidaException("El email " + nuevoEmail + " ya está en uso por otro usuario");
            }
        }
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param email Email del usuario a eliminar.
     * @throws BorradoFallidoException Si el usuario tiene dependencias (recetas o versiones) y no puede ser eliminado.
     * @throws RecursoNoEncontradoException Si el usuario no existe.
     */
    @Override
    @Transactional
    public void deleteUsuario(String email) {
        log.info("Intentando eliminar usuario con email: {}", email);
        
        // Valida que exista, lanzará RecursoNoEncontradoException si no
        UsuarioModel model = getUsuarioModelByEmail(email); 
        
        if (!usuarioPuedeSerEliminado(email)) {
            log.warn("Intento de eliminar usuario {} fallido: tiene dependencias asociadas.", email);
            throw new BorradoFallidoException("No se puede borrar el usuario " + email + ", tiene recetas o versiones asociadas.");
        }
        
        usuarioRepository.deleteByEmail(email);
        log.info("Usuario {} eliminado exitosamente.", email);
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario a buscar.
     * @return DTO con la información del usuario.
     * @throws RecursoNoEncontradoException Si el usuario no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO getUsuarioByEmail(String email) {
        log.info("Buscando usuario con email: {}", email);
        usuarioValidator.validateEmail(email);
        UsuarioModel model = getUsuarioModelByEmail(email);
        log.debug("Usuario {} encontrado. Mapeando a DTO.", email);
        return usuarioMapper.toResponseDTO(model);
    }

    /**
     * Método auxiliar para obtener el modelo de usuario por email.
     *
     * @param email Email del usuario.
     * @return El modelo del usuario.
     * @throws RecursoNoEncontradoException Si el usuario no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public UsuarioModel getUsuarioModelByEmail(String email) {
        log.debug("Buscando UsuarioModel en repositorio con email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con email: {}", email);
                    return new RecursoNoEncontradoException("Usuario no encontrado con email: " + email);
                });
    }

    /**
     * Verifica si un usuario puede ser eliminado (no tiene dependencias).
     *
     * @param email Email del usuario.
     * @return true si puede ser eliminado, false en caso contrario.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean usuarioPuedeSerEliminado(String email) {
        log.debug("Verificando restricciones de borrado para el usuario {}", email);
        boolean tieneDependencias = usuarioTieneRecetasAsociadas(email) || usuarioTieneVersionesRecetasAsociadas(email);
        log.debug("El usuario {} tiene dependencias: {}", email, tieneDependencias);
        return !tieneDependencias;
    }

    private boolean usuarioTieneRecetasAsociadas(String email) {
        return recetaQueryService.existsByCreadoPorEmail(email);
    }

    private boolean usuarioTieneVersionesRecetasAsociadas(String email) {
        return versionRecetaQueryService.existsByCreadaPorEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.debug("Verificando existencia de usuario con email: {}", email);
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaActivoByEmail(String email) {
        log.debug("Verificando estado activo del usuario {}", email);
        Optional<UsuarioModel> usuario = usuarioRepository.findByEmail(email);
        boolean activo = usuario.map(UsuarioModel::getEstaActivo).orElse(false);
        log.debug("Usuario {} activo: {}", email, activo);
        return activo;
    }
}
