package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestasProduccionPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionMetadataPublicResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PublicMapper {

    // Mapeo de DTO completo a DTO público
    @Named("metadataProduccionToPublicDTO")
    MetadataProduccionPublicaResponseDTO metadataProduccionToPublicDTO(ProduccionMetadataResponseDTO produccion);

    VersionMetadataPublicResponseDTO metadataVersionToPublicDTO(VersionMetadataResponseDTO version);


    @Mapping(target = "produccion", source = "produccion", qualifiedByName = "metadataProduccionToPublicDTO")
    RespuestasProduccionPublicResponseDTO respuestasToPublicDTO(UltimasRespuestasProduccionResponseDTO respuestas);


}
