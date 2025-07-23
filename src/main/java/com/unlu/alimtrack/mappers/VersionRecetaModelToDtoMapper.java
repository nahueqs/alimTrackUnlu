package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.VersionRecetaDto;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VersionRecetaModelToDtoMapper {
    VersionRecetaModelToDtoMapper mapper = Mappers.getMapper(VersionRecetaModelToDtoMapper.class);

    @Mapping(target = "idCreadoPor", source = "creadoPor.id")
    @Mapping(target = "idRecetaPadre", source = "recetaPadre.id")
    VersionRecetaDto versionRecetaModelToVersionRecetaDto(VersionRecetaModel versionRecetaModel);
}
