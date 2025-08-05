package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.mappers.UsuarioModelMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioModelMapper mapper;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioModelMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    public List<UsuarioDto> getAllUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(
                mapper::usuarioModelToUsuarioDTO).collect(Collectors.toList());
    }

    public UsuarioModel saveUsuario(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    public UsuarioDto getUsuarioDtoById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        return mapper.usuarioModelToUsuarioDTO(usuarioModel);
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
        mapper.usuarioModelToUsuarioDTO(usuarioModel);
        usuarioRepository.save(usuarioModel);
        return usuarioDto;
    }
}
