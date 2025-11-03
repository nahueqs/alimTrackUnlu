package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.UsuarioResponseDTO;
import com.unlu.alimtrack.models.UsuarioModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {

    UsuarioModel usuarioCreateDTOToModel(UsuarioCreateDTO usuarioCreateDTO);

    void updateModelFromModifyDTO(UsuarioModifyDTO dto, @MappingTarget UsuarioModel model);

    @Mapping(target = "rol", source = "rol.toString")
    List<UsuarioResponseDTO> convertToResponseDTOList(List<UsuarioModel> usuarios);

    UsuarioResponseDTO convertToResponseDTO(UsuarioModel model);
}
