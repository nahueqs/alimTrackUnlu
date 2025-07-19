package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.UsuarioDTO;
import com.unlu.alimtrack.models.UsuarioModel;
import org.mapstruct.Mapper;

@Mapper
public interface UsuarioModelToUsuarioDtoMapper {
    UsuarioDTO usuarioModelToUsuarioDTO(UsuarioModel model);
}
