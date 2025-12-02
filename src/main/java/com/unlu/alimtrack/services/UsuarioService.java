package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.models.UsuarioModel;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponseDTO> getAllUsuarios();

    UsuarioResponseDTO addUsuario(UsuarioCreateDTO usuarioCreateDTO);

    void modifyUsuario(String email, UsuarioModifyDTO modificacion);

    void deleteUsuario(String email);

    UsuarioResponseDTO getUsuarioByEmail(String email);

    UsuarioModel getUsuarioModelByEmail(String email);

    boolean usuarioPuedeSerEliminado(String email);

    boolean existsByEmail(String email);

    boolean estaActivoByEmail(String email);
}
