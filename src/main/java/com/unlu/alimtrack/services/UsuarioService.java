package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.mappers.UsuarioModelToUsuarioDtoMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;

    public List<UsuarioDto> getAllUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        List<UsuarioDto> usuarioDtos = usuarios.stream().map(
                UsuarioModelToUsuarioDtoMapper.mapper::usuarioModelToUsuarioDTO).collect(Collectors.toList());

        return usuarioDtos;
    }

    public UsuarioModel saveUsuario(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    public UsuarioDto getUsuarioDtoById(Long id) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id).orElse(null);
        return UsuarioModelToUsuarioDtoMapper.mapper.usuarioModelToUsuarioDTO(usuarioModel);
    }

}
