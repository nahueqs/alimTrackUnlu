package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.create.SeccionCreateDTO;
import com.unlu.alimtrack.dtos.response.SeccionResponseDTO;
import com.unlu.alimtrack.models.SeccionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeccionModelMapper {

    @Mapping(target = "versionRecetaPadre.id", source = "idVersionRecetaPadre")
    SeccionModel createDTOToModel(SeccionCreateDTO seccionCreateDTO);

    SeccionResponseDTO toResponseDTO(SeccionModel model);
}
