package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.create.SeccionCreateDTO;
import com.unlu.alimtrack.dtos.response.SeccionResponseDTO;
import com.unlu.alimtrack.models.SeccionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SeccionModelMapper {
    SeccionModelMapper mapper = Mappers.getMapper(SeccionModelMapper.class);

    @Mapping(target = "idVersionRecetaPadre.id", source = "idVersionRecetaPadre")
    SeccionModel createDTOToModel(SeccionCreateDTO seccionCreateDTO);

    SeccionResponseDTO toResponseDTO(SeccionModel model);
}
