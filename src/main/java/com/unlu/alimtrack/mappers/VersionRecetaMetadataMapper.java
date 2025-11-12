package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VersionRecetaMetadataMapper {

    VersionRecetaMetadataMapper mapper = Mappers.getMapper(VersionRecetaMetadataMapper.class);

    @Mapping(target = "creadoPor.username", source = "usernameCreador")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "recetaPadre.codigoReceta", source = "codigoRecetaPadre")
    VersionRecetaModel toVersionRecetaModel(VersionRecetaCreateDTO versionRecetaCreateDto);

    @Mapping(target = "creadaPor", source = "creadoPor.nombre")
    @Mapping(target = "nombreRecetaPadre", source = "recetaPadre.nombre")
    @Mapping(target = "codigoRecetaPadre", source = "recetaPadre.codigoReceta")
    VersionRecetaMetadataResponseDTO toVersionRecetaResponseDTO(VersionRecetaModel versionRecetaModel);

    List<VersionRecetaMetadataResponseDTO> toVersionRecetaResponseDTOList(
            List<VersionRecetaModel> versionRecetaModels);

    void updateModelFromModifyDTO(VersionRecetaModifyDTO modificacion, @MappingTarget VersionRecetaModel model);
}
