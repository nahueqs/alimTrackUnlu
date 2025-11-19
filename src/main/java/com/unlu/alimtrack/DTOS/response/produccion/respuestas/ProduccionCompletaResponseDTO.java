package com.unlu.alimtrack.DTOS.response.produccion.respuestas;

import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaCompletaResponseDTO;

import java.time.LocalDateTime;
import java.util.Map;

public record ProduccionCompletaResponseDTO(
        String estado,

        VersionRecetaCompletaResponseDTO estructura,

        Map<Long, String> respuestasCampos,

        Map<String, Map<String, String>> respuestasTablas,

        ProgresoProduccionResponseDTO progreso,

        LocalDateTime timestampConsulta
) {
}

