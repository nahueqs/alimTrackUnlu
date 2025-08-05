package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.SeccionDto;
import com.unlu.alimtrack.models.SeccionModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SeccionModelMapper {
    SeccionModelMapper mapper = Mappers.getMapper(SeccionModelMapper.class);

    SeccionDto seccionModelToSeccionDto(SeccionModel model);
}
