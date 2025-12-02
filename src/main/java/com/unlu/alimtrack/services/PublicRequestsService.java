package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestasProduccionPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;

import java.util.List;

public interface PublicRequestsService {

    List<MetadataProduccionPublicaResponseDTO> getAllProduccionesMetadataPublico();

    RespuestasProduccionPublicResponseDTO getEstadoActualProduccionPublico(String codigoProduccion);

    EstadoProduccionPublicoResponseDTO getProduccionPublic(String codigoProduccion);

    VersionEstructuraPublicResponseDTO getEstructuraVersionPublica(String codigoVersionReceta);

}
