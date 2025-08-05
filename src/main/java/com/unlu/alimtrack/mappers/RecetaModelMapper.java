package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.models.RecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UsuarioModelMapper.class, componentModel = "spring")
public interface RecetaModelMapper {

    RecetaModelMapper mapper = Mappers.getMapper(RecetaModelMapper.class);

    RecetaDto recetaModelToRecetaDTO(RecetaModel receta);

    RecetaModel recetaDTOToRecetaModel(RecetaDto receta);
    @Mapping(target = "creadaPor", source = "creadoPor.nombre")
    RecetaResponseDTO recetaModeltoRecetaResponseDTO(RecetaModel receta);

}
