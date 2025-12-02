package com.unlu.alimtrack.DTOS.response.Produccion.protegido;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;

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
