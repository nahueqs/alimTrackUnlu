package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.models.RecetaModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UsuarioModelToUsuarioDtoMapper.class)
public interface RecetaModelToRecetaDtoMapper {

    RecetaModelToRecetaDtoMapper mapper = Mappers.getMapper(RecetaModelToRecetaDtoMapper.class);

    RecetaDto recetaModelToRecetaDTO(RecetaModel receta);
}
