package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.produccion.respuestas.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RespuestaCampoModel.class)
public interface RespuestaCampoMapper {

    @Mapping(target = "idRespuesta", source = "id")
    @Mapping(target = "idCampo", source = "idCampo.id")
    RespuestaCampoResponseDTO respuestaCampoToResponseDTO(RespuestaCampoModel respuesta);
}
