package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.RespuestaCampoResponseDTO;

public interface ProduccionManagementService {

    ProduccionMetadataResponseDTO iniciarProduccion(ProduccionCreateDTO createDTO);

    void updateEstado(String codigoProduccion, ProduccionCambioEstadoRequestDTO nuevoEstadoDTO);

    RespuestaCampoResponseDTO guardarRespuestaCampo(String codigoProduccion, Long idCampo, RespuestaCampoRequestDTO request);

    UltimasRespuestasProduccionResponseDTO getUltimasRespuestas(String codigoProduccion);

    UltimasRespuestasProduccionResponseDTO test();
}
