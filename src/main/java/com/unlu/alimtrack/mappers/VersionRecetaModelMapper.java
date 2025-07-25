package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.request.versionRecetaCreateDto;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VersionRecetaModelMapper {
    VersionRecetaModelMapper mapper = Mappers.getMapper(VersionRecetaModelMapper.class);

    @Mapping(target = "idCreadoPor", source = "creadoPor.id")
    versionRecetaCreateDto toVersionRecetaDto(VersionRecetaModel versionRecetaModel);

    @Mapping(target = "creadoPor.id", source = "idCreadoPor")
    VersionRecetaModel toVersionRecetaModel(versionRecetaCreateDto versionRecetaCreateDto);

}
