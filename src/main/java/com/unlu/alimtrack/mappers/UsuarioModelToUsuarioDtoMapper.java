package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.models.UsuarioModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UsuarioModelToUsuarioDtoMapper {

    UsuarioModelToUsuarioDtoMapper mapper = Mappers.getMapper(UsuarioModelToUsuarioDtoMapper.class);

    UsuarioDto usuarioModelToUsuarioDTO(UsuarioModel model);
}
