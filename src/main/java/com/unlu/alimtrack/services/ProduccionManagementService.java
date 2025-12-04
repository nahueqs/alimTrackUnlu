package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCeldaTablaResquestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;

public interface ProduccionManagementService {

    ProduccionMetadataResponseDTO iniciarProduccion(ProduccionCreateDTO createDTO);

    void updateEstado(String codigoProduccion, ProduccionCambioEstadoRequestDTO nuevoEstadoDTO);

    RespuestaCampoResponseDTO guardarRespuestaCampo(String codigoProduccion, Long idCampo, RespuestaCampoRequestDTO request);

    RespuestaCeldaTablaResponseDTO guardarRespuestaCeldaTabla(String codigoProduccion, Long idFila, Long idColumna, Long idTabla, RespuestaCeldaTablaResquestDTO request);

    UltimasRespuestasProduccionResponseDTO getUltimasRespuestas(String codigoProduccion);

    UltimasRespuestasProduccionResponseDTO test();
}
