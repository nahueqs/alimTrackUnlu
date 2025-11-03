package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VersionRecetaMapper {

    VersionRecetaMapper mapper = Mappers.getMapper(VersionRecetaMapper.class);

    @Mapping(target = "creadoPor.username", source = "usernameCreador")
    VersionRecetaModel toVersionRecetaModel(VersionRecetaCreateDTO versionRecetaCreateDto);

    @Mapping(target = "creadaPor", source = "creadoPor.nombre")
    @Mapping(target = "nombreRecetaPadre", source = "recetaPadre.nombre")
    VersionRecetaResponseDTO toVersionRecetaResponseDTO(VersionRecetaModel versionRecetaModel);

    List<VersionRecetaResponseDTO> toVersionRecetaResponseDTOList(
            List<VersionRecetaModel> versionRecetaModels);

    void updateModelFromModifyDTO(VersionRecetaModifyDTO modificacion, @MappingTarget VersionRecetaModel model);
}
