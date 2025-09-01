package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.models.VersionRecetaModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VersionRecetaMapper {

  VersionRecetaMapper mapper = Mappers.getMapper(VersionRecetaMapper.class);

  @Mapping(target = "creadoPor.username", source = "usernameCreador")
  VersionRecetaModel toVersionRecetaModel(VersionRecetaCreateDTO versionRecetaCreateDto);

  @Mapping(target = "creadaPor", source = "creadoPor.nombre")
  @Mapping(target = "nombreRecetaPadre", source = "recetaPadre.nombre")
  VersionRecetaResponseDTO toVersionRecetaResponseDTO(VersionRecetaModel versionRecetaModel);

  List<VersionRecetaResponseDTO> toVersionRecetaResponseDTOList(
      List<VersionRecetaModel> versionRecetaModels);

}
