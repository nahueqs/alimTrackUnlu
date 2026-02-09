package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionMetadataModifyRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaTablaRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import jakarta.validation.Valid;

public interface ProduccionManagementService {

    ProduccionMetadataResponseDTO iniciarProduccion(ProduccionCreateDTO createDTO);

    ProduccionMetadataResponseDTO updateEstado(String codigoProduccion, ProduccionCambioEstadoRequestDTO nuevoEstadoDTO);

    RespuestaCampoResponseDTO guardarRespuestaCampo(String codigoProduccion, Long idCampo, RespuestaCampoRequestDTO request);

    RespuestaCeldaTablaResponseDTO guardarRespuestaCeldaTabla(String codigoProduccion, Long idFila, Long idColumna, Long idTabla, RespuestaTablaRequestDTO request);

    UltimasRespuestasProduccionResponseDTO getUltimasRespuestas(String codigoProduccion);

    UltimasRespuestasProduccionResponseDTO test();

    ProduccionMetadataResponseDTO updateMetadata(String codigoProduccion, @Valid ProduccionMetadataModifyRequestDTO request);

    void deleteProduccion(String codigoProduccion);
}
