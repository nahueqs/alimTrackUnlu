package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.dtos.request.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.exception.RecursoYaExisteException;
import com.unlu.alimtrack.mappers.UsuarioModelMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioModelMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioModelMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public List<UsuarioDto> getAllUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(
                usuarioMapper::usuarioModelToUsuarioDTO).collect(Collectors.toList());
    }

    public UsuarioResponseDTO saveUsuario(UsuarioCreateDTO usuario) {
        UsuarioModel usuarioModel = usuarioMapper.usuarioCreateDTOToModel(usuario);
        // verifica si ya existe un usuario con ese email
        if (usuarioRepository.existsByEmail(usuarioModel.getEmail())){
            throw new RecursoYaExisteException("El email ya ha sido usado por un usuario existente");
        }
        // crea el usuario y devuelve un response
        usuarioRepository.save(usuarioModel);
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioModel);
    }

    public UsuarioDto getUsuarioDtoById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        return usuarioMapper.usuarioModelToUsuarioDTO(usuarioModel);
    }

    public UsuarioModel getUsuarioModelById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        return usuarioModel;
    }

    public void modificarUsuario(UsuarioModel usuarioModel) {
        usuarioRepository.save(usuarioModel);
    }

    public void borrarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public UsuarioDto saveUsuario2(UsuarioDto usuarioDto) {
        UsuarioModel usuarioModel = usuarioRepository.findById(usuarioDto.getId()).orElse(null);
        usuarioMapper.usuarioModelToUsuarioDTO(usuarioModel);
        usuarioRepository.save(usuarioModel);
        return usuarioDto;
    }
}
