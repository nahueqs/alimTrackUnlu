package com.unlu.alimtrack.DTOS.response.produccion.publico;

import java.time.LocalDateTime;
import java.util.List;

public record RespuestasProduccionPublicResponseDTO(
        ProduccionMetadataPublicaResponseDTO produccion,

        List<RespuestaCampoResponseDTO> respuestasCampos,

        List<RespuestaCeldaTablaResponseDTO> respuestasTablas,

        ProgresoProduccionResponseDTO progreso,

        LocalDateTime timestampConsulta
) {
}
