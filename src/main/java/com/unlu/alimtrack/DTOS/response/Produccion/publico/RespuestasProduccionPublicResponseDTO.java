package com.unlu.alimtrack.DTOS.response.Produccion.publico;

import java.time.LocalDateTime;
import java.util.List;

public record RespuestasProduccionPublicResponseDTO(

        MetadataProduccionPublicaResponseDTO produccion,

        List<RespuestaCampoResponseDTO> respuestasCampos,

        List<RespuestaCeldaTablaResponseDTO> respuestasTablas,

        ProgresoProduccionResponseDTO progreso,

        LocalDateTime timestampConsulta
) {
}
