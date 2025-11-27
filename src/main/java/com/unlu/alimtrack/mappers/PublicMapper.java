package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionMetadataPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.RespuestasProduccionPublicResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PublicMapper {

    // Mapeo de DTO completo a DTO público
    @Named("metadataProduccionToPublicDTO")
    ProduccionMetadataPublicaResponseDTO metadataProduccionToPublicDTO(ProduccionMetadataResponseDTO produccion);


    @Mapping(target = "produccion", source = "produccion", qualifiedByName = "metadataProduccionToPublicDTO")
    RespuestasProduccionPublicResponseDTO respuestasToPublicDTO(UltimasRespuestasProduccionResponseDTO respuestas);


}
