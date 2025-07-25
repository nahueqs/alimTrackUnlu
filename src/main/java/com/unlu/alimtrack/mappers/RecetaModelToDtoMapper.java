package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.models.RecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UsuarioModelToDtoMapper.class, componentModel = "spring")
public interface RecetaModelToDtoMapper {

    RecetaModelToDtoMapper mapper = Mappers.getMapper(RecetaModelToDtoMapper.class);

    RecetaDto recetaModelToRecetaDTO(RecetaModel receta);
    RecetaModel recetaDTOToRecetaModel(RecetaDto receta);

}
