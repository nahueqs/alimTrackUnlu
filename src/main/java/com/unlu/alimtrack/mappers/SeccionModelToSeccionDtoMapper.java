package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.SeccionDto;
import com.unlu.alimtrack.models.SeccionModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SeccionModelToSeccionDtoMapper {
    SeccionModelToSeccionDtoMapper mapper = Mappers.getMapper(SeccionModelToSeccionDtoMapper.class);
    SeccionDto  seccionModelToSeccionDto(SeccionModel model);
}
