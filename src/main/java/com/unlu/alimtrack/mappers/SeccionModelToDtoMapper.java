package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.SeccionDto;
import com.unlu.alimtrack.models.SeccionModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SeccionModelToDtoMapper {
    SeccionModelToDtoMapper mapper = Mappers.getMapper(SeccionModelToDtoMapper.class);

    SeccionDto seccionModelToSeccionDto(SeccionModel model);
}
