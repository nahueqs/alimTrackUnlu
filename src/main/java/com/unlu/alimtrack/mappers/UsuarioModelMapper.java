package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.create.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.models.UsuarioModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioModelMapper {

    UsuarioResponseDTO usuarioToUsuarioResponseDTO(UsuarioModel usuario);

    UsuarioModel usuarioCreateDTOToModel(UsuarioCreateDTO usuarioCreateDTO);

    UsuarioResponseDTO usuarioModelToUsuarioResponseDTO(UsuarioModel usuarioModel);

    void updateModelFromModifyDTO(UsuarioModifyDTO dto, @MappingTarget UsuarioModel model);


}
