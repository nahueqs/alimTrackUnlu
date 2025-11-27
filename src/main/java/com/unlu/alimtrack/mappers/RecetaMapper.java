package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.RecetaMetadataResponseDTO;
import com.unlu.alimtrack.models.RecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(uses = UsuarioMapper.class, componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecetaMapper {

    @Mapping(target = "creadaPor", source = "creadoPor.nombre")
    RecetaMetadataResponseDTO recetaModeltoRecetaResponseDTO(RecetaModel receta);

    @Mapping(target = "creadoPor.email", source = "emailCreador")
    RecetaModel recetaCreateDTOtoModel(RecetaCreateDTO receta);

    void updateModelFromModifyDTO(RecetaModifyDTO dto, @MappingTarget RecetaModel model);

    List<RecetaMetadataResponseDTO> recetaModelsToRecetaResponseDTOs(List<RecetaModel> recetas);

}
