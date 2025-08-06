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

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioModelMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponseDTO> getAllUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        if  (usuarios.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay usuarios guardados");
        }
        return usuarios.stream().map(
                usuarioMapper::usuarioModelToUsuarioResponseDTO).collect(Collectors.toList());
    }

    public UsuarioResponseDTO saveUsuario(UsuarioCreateDTO usuario) {
        UsuarioModel usuarioModel = usuarioMapper.usuarioCreateDTOToModel(usuario);
        // verifica si ya existe un usuario con ese email
        if (usuarioRepository.existsByEmail(usuarioModel.getEmail())){
            throw new RecursoYaExisteException("El email ya ha sido usado por un usuario existente");
        }
        // crea el usuario y devuelve un response
        String passwordEncriptada = passwordEncoder.encode(usuarioModel.getContraseña());
        usuarioModel.setContraseña(passwordEncriptada);
        usuarioRepository.save(usuarioModel);
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioModel);
    }

    public UsuarioResponseDTO getUsuarioDtoById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        if (usuarioModel == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }
        return usuarioMapper.usuarioModelToUsuarioResponseDTO(usuarioModel);
    }

    public UsuarioResponseDTO getUsuarioModelById(Long id) {
        UsuarioModel usuarioModel =  usuarioRepository.findById(id).orElse(null);
        return usuarioMapper.usuarioModelToUsuarioResponseDTO(usuarioModel);
    }

    public void modificarUsuario(UsuarioModifyDTO modificacion) {
        if (validarModificacionUsuario(modificacion)){
            throw new ModificacionInvalidaException("No se puede realizar la modificacion solicitada");
        };
        usuarioRepository.save(usuarioMapper.usuarioModifyDTOToModel(modificacion));
    }

    private boolean validarModificacionUsuario(UsuarioModifyDTO modificacion) {
        return modificacion.nombre() != null || modificacion.contraseña() != null;
    }

    public void borrarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

}
