package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.dtos.request.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.request.UsuarioModifyDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.models.UsuarioModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioModelMapper {

    UsuarioModelMapper mapper = Mappers.getMapper(UsuarioModelMapper.class);

    UsuarioResponseDTO usuarioToUsuarioResponseDTO(UsuarioModel usuario);

    UsuarioModel usuarioCreateDTOToModel(UsuarioCreateDTO usuarioCreateDTO);

    UsuarioDto usuarioModelToUsuarioDTO(UsuarioModel model);

    UsuarioResponseDTO usuarioModelToUsuarioResponseDTO(UsuarioModel usuarioModel);


}
