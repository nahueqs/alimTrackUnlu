package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.request.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VersionRecetaModelMapper {
    VersionRecetaModelMapper mapper = Mappers.getMapper(VersionRecetaModelMapper.class);

    @Mapping(target = "idCreadoPor", source = "creadoPor.id")
    VersionRecetaCreateDTO toVersionRecetaDto(VersionRecetaModel versionRecetaModel);

    @Mapping(target = "creadoPor.id", source = "idCreadoPor")
    VersionRecetaModel toVersionRecetaModel(VersionRecetaCreateDTO versionRecetaCreateDto);

    @Mapping(target = "usuarioCreador", source = "creadoPor.nombre")
    @Mapping(target = "nombreRecetaPadre", source = "recetaPadre.nombre")
    VersionRecetaResponseDTO toVersionRecetaResponseDTO(VersionRecetaModel versionRecetaModel);



}
