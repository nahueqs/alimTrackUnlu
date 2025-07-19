package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.RecetaDTO;
import com.unlu.alimtrack.dtos.RecetaDTO2;
import com.unlu.alimtrack.models.RecetaModel;
import org.mapstruct.Mapper;

@Mapper(uses = UsuarioModelToUsuarioDtoMapper.class)
public interface RecetaModelToRecetaDtoMapper2 {
    RecetaDTO2 recetaModelToRecetaDTO2(RecetaModel receta);
}
