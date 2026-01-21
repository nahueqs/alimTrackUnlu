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

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios");
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new RecursoNoEncontradoException("No existen usuarios registrados");
        }
        log.debug("Retornando {} usuarios", usuarios.size());
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO addUsuario(UsuarioCreateDTO usuarioCreateDTO) {
        log.info("Creando nuevo usuario con email: {}", usuarioCreateDTO.email());
        if (usuarioRepository.existsByEmail(usuarioCreateDTO.email())) {
            throw new RecursoDuplicadoException("El email ya ha sido usado por un usuario existente: " + usuarioCreateDTO.email());
        }

        UsuarioModel usuarioModel = crearNuevoUsuarioByCreateDTO(usuarioCreateDTO);
        usuarioRepository.save(usuarioModel);
        log.info("Usuario {} creado y guardado exitosamente", usuarioModel.getEmail());
        return usuarioMapper.toResponseDTO(usuarioModel);
    }

    private UsuarioModel crearNuevoUsuarioByCreateDTO(UsuarioCreateDTO usuario) {
        log.debug("Mapeando UsuarioCreateDTO a UsuarioModel y encriptando contraseña para {}", usuario.email());
        UsuarioModel usuarioModel = usuarioMapper.toModel(usuario);
        String passwordEncriptada = passwordEncoder.encode(usuario.contraseña());
        usuarioModel.setPassword(passwordEncriptada);
        return usuarioModel;
    }

    @Override
    @Transactional
    public void modifyUsuario(String email, UsuarioModifyDTO modificacion) {
        log.info("Modificando usuario con email: {}", email);
        usuarioValidator.validarModificacion(modificacion);
        UsuarioModel usuarioExistente = getUsuarioModelByEmail(email);
        validateUnicidadDatos(usuarioExistente, modificacion);
        aplicarModificaciones(usuarioExistente, modificacion);
        usuarioRepository.save(usuarioExistente);
        log.info("Usuario {} modificado exitosamente", email);
    }

    private void aplicarModificaciones(UsuarioModel usuarioExistente, UsuarioModifyDTO modificacion) {
        log.debug("Aplicando modificaciones al modelo de usuario para {}", usuarioExistente.getEmail());
        usuarioMapper.updateModelFromModifyDTO(modificacion, usuarioExistente);
        if (modificacion.contraseña() != null) {
            log.debug("Encriptando y actualizando nueva contraseña para {}", usuarioExistente.getEmail());
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
                throw new ModificacionInvalidaException("El email " + nuevoEmail + " ya está en uso por otro usuario");
            }
        }
    }

    @Override
    @Transactional
    public void deleteUsuario(String email) {
        log.info("Intentando eliminar usuario con email: {}", email);
        UsuarioModel model = getUsuarioModelByEmail(email); // Valida que exista
        if (!usuarioPuedeSerEliminado(email)) {
            throw new BorradoFallidoException("No se puede borrar el usuario " + email + ", tiene recetas o versiones asociadas.");
        }
        usuarioRepository.deleteByEmail(email);
        log.info("Usuario {} eliminado exitosamente", email);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO getUsuarioByEmail(String email) {
        log.info("Buscando usuario con email: {}", email);
        usuarioValidator.validateEmail(email);
        UsuarioModel model = getUsuarioModelByEmail(email);
        log.debug("Usuario {} encontrado. Mapeando a DTO.", email);
        return usuarioMapper.toResponseDTO(model);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioModel getUsuarioModelByEmail(String email) {
        log.debug("Buscando UsuarioModel con email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean usuarioPuedeSerEliminado(String email) {
        log.debug("Verificando si el usuario {} puede ser eliminado", email);
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
        log.debug("Verificando si existe un usuario con email: {}", email);
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaActivoByEmail(String email) {
        log.debug("Verificando si el usuario {} está activo", email);
        Optional<UsuarioModel> usuario = usuarioRepository.findByEmail(email);
        return usuario.map(UsuarioModel::getEstaActivo).orElse(false);
    }
}
