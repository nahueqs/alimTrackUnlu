package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.RecetaDTO;
import com.unlu.alimtrack.models.RecetaModel;
import org.mapstruct.Mapper;

@Mapper(uses = UsuarioModelToUsuarioDtoMapper.class)
public interface RecetaModelToRecetaDtoMapper {
    RecetaDTO recetaModelToRecetaDTO(RecetaModel receta);
}
