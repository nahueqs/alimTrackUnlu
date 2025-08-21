package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ProduccionModel.class, componentModel = "spring")
public interface ProduccionModelMapper {

        ProduccionModelMapper produccionModelMapper = Mappers.getMapper(ProduccionModelMapper.class);

        @Mapping(target = "codigoVersion", source = "versionReceta.codigoVersionReceta")
        ProduccionResponseDTO produccionToProduccionResponseDTO(ProduccionModel produccion);



}

