package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.models.UsuarioModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioModelMapper {

    UsuarioModelMapper mapper = Mappers.getMapper(UsuarioModelMapper.class);

    UsuarioDto usuarioModelToUsuarioDTO(UsuarioModel model);
}
