package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.OperacionNoPermitida;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.exception.RecursoYaExisteException;
import com.unlu.alimtrack.mappers.UsuarioModelMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioModelMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RecetaService recetaService;
    private final VersionRecetaService versionRecetaModelService;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioModelMapper usuarioMapper, PasswordEncoder passwordEncoder, @Lazy RecetaService recetaModelService, @Lazy VersionRecetaService versionRecetaModelService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.recetaService = recetaModelService;
        this.versionRecetaModelService = versionRecetaModelService;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> getAllUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay usuarios guardados");
        }
        return usuarios.stream().map(
                usuarioMapper::usuarioModelToUsuarioResponseDTO).collect(Collectors.toList());
    }

    public UsuarioResponseDTO addUsuario(UsuarioCreateDTO usuario) {

        // verifica si ya existe un usuario con ese email
        if (usuarioRepository.existsByEmail(usuario.email())) {
            throw new RecursoYaExisteException("El email ya ha sido usado por un usuario existente");
        }
        UsuarioModel usuarioModel = usuarioMapper.usuarioCreateDTOToModel(usuario);
        // crea el usuario y devuelve un response
        String passwordEncriptada = passwordEncoder.encode(usuario.contraseña());
        usuarioModel.setContraseña(passwordEncriptada);
        usuarioRepository.save(usuarioModel);
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioModel);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getUsuarioResponseDTOById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        if (usuarioModel == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        }
        return usuarioMapper.usuarioModelToUsuarioResponseDTO(usuarioModel);
    }

    @Transactional(readOnly = true)
    public UsuarioModel getUsuarioModelById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        if (usuarioModel == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        }
        return usuarioModel;
    }

    public void modifyUsuario(Long id, UsuarioModifyDTO modificacion) {
        if (!validarModificacionUsuario(modificacion)) {
            throw new ModificacionInvalidaException("No se puede realizar la modificacion solicitada");
        }
        UsuarioModel usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        }

        usuario.setNombre(modificacion.nombre());
        String passwordEncriptada = passwordEncoder.encode(modificacion.contraseña());
        usuario.setContraseña(passwordEncriptada);

        usuarioRepository.save(usuario);
    }

    private boolean validarModificacionUsuario(UsuarioModifyDTO modificacion) {
        return modificacion.nombre() != null || modificacion.contraseña() != null;
    }

    public void deleteUsuario(Long id) {
        if (recetaService.findAllByCreadoPorId(id) == null) {
            throw new OperacionNoPermitida("No se puede borrar el usuario, tiene recetas asociadas.");
        }
        if (versionRecetaModelService.findAllByCreadoPorId(id) == null ) {
            throw new OperacionNoPermitida("No se puede borrar el usuario, tiene recetas asociadas.");
        }

        //habria que agregar que no tenga respuestas asociadas

        usuarioRepository.deleteById(id);
    }

}
