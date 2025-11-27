package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionEstadoPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionMetadataPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.RespuestasProduccionPublicResponseDTO;

import java.util.List;

public interface PublicRequestsService {

    List<ProduccionMetadataPublicaResponseDTO> getAllProduccionesMetadataPublico();

    RespuestasProduccionPublicResponseDTO getEstadoActualProduccionPublico(String codigoProduccion);

    ProduccionEstadoPublicaResponseDTO getProduccionPublic(String codigoProduccion);

}
