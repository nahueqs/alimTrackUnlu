package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.OperacionNoPermitida;
import com.unlu.alimtrack.exception.RecursoDuplicadoException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.UsuarioModelMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import com.unlu.alimtrack.services.validators.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioModelMapper usuarioMapper;
    private final UsuarioValidator usuarioValidator;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> getAllUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        usuarioValidator.validateListUsuarios(usuarios);
        return convertToResponseDTOList(usuarios);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getUsuarioByUsername(String username) {
        usuarioValidator.validateUsername(username);
        UsuarioModel model = getUsuarioModelByUsername(username);
        return convertToResponseDTO(model);
    }

    private UsuarioModel getUsuarioModelByUsername(String username) {
        UsuarioModel model = usuarioRepository.findByUsername(username).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con username" + username));
        usuarioValidator.validateUsuario(model);
        return model;
    }

    @Transactional(readOnly = true)
    public UsuarioModel getUsuarioModelById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        if (usuarioModel == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        }
        return usuarioModel;
    }

    public UsuarioResponseDTO addUsuario(UsuarioCreateDTO usuario) {

        // verifica si ya existe un usuario con ese email
        if (usuarioRepository.existsByEmail(usuario.email())) {
            throw new RecursoDuplicadoException("El email ya ha sido usado por un usuario existente");
        }
        UsuarioModel usuarioModel = usuarioMapper.usuarioCreateDTOToModel(usuario);
        // crea el usuario y devuelve un response
        String passwordEncriptada = passwordEncoder.encode(usuario.contraseña());
        usuarioModel.setContraseña(passwordEncriptada);
        usuarioRepository.save(usuarioModel);
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioModel);
    }

    public void modifyUsuario(String username, UsuarioModifyDTO modificacion) {
        usuarioValidator.validarModificacion(modificacion);
        UsuarioModel usuarioExistente = getUsuarioModelByUsername(username);

        validateCambioEmail(usuarioExistente, modificacion.nombre());
        usuarioExistente.setNombre(modificacion.nombre());
        String passwordEncriptada = passwordEncoder.encode(modificacion.contraseña());
        usuarioExistente.setContraseña(passwordEncriptada);

        usuarioRepository.save(usuarioExistente);
    }

    private void validateCambioEmail(UsuarioModel usuarioExistente, String email) {
        if (email != null && !email.equals(usuarioExistente.getEmail())) {
            if (usuarioRepository.existsByEmail(email)) {
                throw new ModificacionInvalidaException("El email ya está en uso por otro usuario");
            }
        }
    }

    private void validateCambioUsername(UsuarioModel usuarioExistente, String username) {
        if (username != null && !username.equals(usuarioExistente.getUsername())) {
            if (usuarioRepository.existsByUsername(username)) {
                throw new ModificacionInvalidaException("El username ya está en uso por otro usuario");
            }
        }
    }

    private void updateModelFromDTO(UsuarioModifyDTO modificacion, UsuarioModel model) {
        usuarioMapper.updateModelFromModifyDTO(modificacion, model);
    }

    @Transactional
    public void deleteUsuario(String username) {

        UsuarioModel model = getUsuarioModelByUsername(username);

        /*if (recetaService.findAllByCreadoPorUsername(username) == null) {
            throw new OperacionNoPermitida("No se puede borrar el usuario, tiene recetas asociadas.");
        }
        if (versionRecetaModelService.findAllByCreadoPorUsername(username) == null) {
            throw new OperacionNoPermitida("No se puede borrar el usuario, tiene recetas asociadas.");
        }*/

        //habria que agregar que no tenga respuestas asociadas

        // usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO convertToResponseDTO(UsuarioModel model) {
        return usuarioMapper.usuarioToUsuarioResponseDTO(model);
    }

    // convierte una lista de models a otra de responseDTO
    private List<UsuarioResponseDTO> convertToResponseDTOList(List<UsuarioModel> usuarios) {
        return usuarios.stream().map(usuarioMapper::usuarioModelToUsuarioResponseDTO).collect(Collectors.toList());
    }

    public boolean existsByUsername(String username) {
        return false;
    }
}
