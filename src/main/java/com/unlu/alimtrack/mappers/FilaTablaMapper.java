package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.FilaTablaCreateDTO;
import com.unlu.alimtrack.models.FilaTablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FilaTablaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tabla", ignore = true)
    FilaTablaModel toModel(FilaTablaCreateDTO dto);

}

