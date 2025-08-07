package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.request.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.request.UsuarioModifyDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.exception.RecursoYaExisteException;
import com.unlu.alimtrack.mappers.UsuarioModelMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioModelMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RecetaService recetaService;
    private final VersionRecetaService versionRecetaModelService;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioModelMapper usuarioMapper, PasswordEncoder passwordEncoder, RecetaModelRepository recetaModelRepository, RecetaService recetaModelService, VersionRecetaService versionRecetaModelService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.recetaService = recetaModelService;
        this.versionRecetaModelService = versionRecetaModelService;
    }

    public List<UsuarioResponseDTO> getAllUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay usuarios guardados");
        }
        return usuarios.stream().map(
                usuarioMapper::usuarioModelToUsuarioResponseDTO).collect(Collectors.toList());
    }

    public UsuarioResponseDTO saveUsuario(UsuarioCreateDTO usuario) {
        UsuarioModel usuarioModel = usuarioMapper.usuarioCreateDTOToModel(usuario);
        // verifica si ya existe un usuario con ese email
        if (usuarioRepository.existsByEmail(usuarioModel.getEmail())) {
            throw new RecursoYaExisteException("El email ya ha sido usado por un usuario existente");
        }
        // crea el usuario y devuelve un response
        String passwordEncriptada = passwordEncoder.encode(usuarioModel.getContraseña());
        usuarioModel.setContraseña(passwordEncriptada);
        usuarioRepository.save(usuarioModel);
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioModel);
    }

    public UsuarioResponseDTO getUsuarioResponseDTOById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        if (usuarioModel == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        }
        return usuarioMapper.usuarioModelToUsuarioResponseDTO(usuarioModel);
    }

    public UsuarioModel getUsuarioModelById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        if (usuarioModel == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        }
        return usuarioModel;
    }

    public void modificarUsuario(Long id, UsuarioModifyDTO modificacion) {
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

    public void borrarUsuario(Long id) {
        if (recetaService.getRecetaModelById(id) == null) {
            throw new IllegalStateException("No se puede borrar el usuario, tiene recetas asociadas.");
        }
        if (versionRecetaModelService.getVersionById(id) == null ) {
            throw new IllegalStateException("No se puede borrar el usuario, tiene recetas asociadas.");
        }

        usuarioRepository.deleteById(id);
    }

}
