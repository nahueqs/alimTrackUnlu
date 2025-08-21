package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.models.RecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UsuarioModelMapper.class, componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecetaModelMapper {



    RecetaModelMapper mapper = Mappers.getMapper(RecetaModelMapper.class);

    @Mapping(target = "creadaPor", source = "creadoPor.nombre")
    RecetaResponseDTO recetaModeltoRecetaResponseDTO(RecetaModel receta);

    @Mapping(target = "creadoPor.id", source = "idUsuarioCreador")
    RecetaModel recetaCreateDTOtoModel(RecetaCreateDTO receta);

    void updateModelFromModifyDTO(RecetaModifyDTO dto, @MappingTarget RecetaModel model);


}
