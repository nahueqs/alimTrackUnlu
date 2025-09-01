package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.models.RecetaModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(uses = UsuarioModelMapper.class, componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecetaModelMapper {

  @Mapping(target = "creadaPor", source = "creadoPor.nombre")
  RecetaResponseDTO recetaModeltoRecetaResponseDTO(RecetaModel receta);

  @Mapping(target = "creadoPor.username", source = "usernameCreador")
  RecetaModel recetaCreateDTOtoModel(RecetaCreateDTO receta);

  void updateModelFromModifyDTO(RecetaModifyDTO dto, @MappingTarget RecetaModel model);

  List<RecetaResponseDTO> recetaModelsToRecetaResponseDTOs(List<RecetaModel> recetas);

}
