package com.unlu.alimtrack.DTOS.response.produccion.protegido;

import com.unlu.alimtrack.DTOS.response.produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.RespuestaCampoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record UltimasRespuestasProduccionResponseDTO(

        ProduccionMetadataResponseDTO produccion,

        List<RespuestaCampoResponseDTO> respuestasCampos,

        List<RespuestaCeldaTablaResponseDTO> respuestasTablas,

        ProgresoProduccionResponseDTO progreso,

        LocalDateTime timestampConsulta
) {
}
