package com.unlu.alimtrack.DTOS.response.produccion.respuestas;

import com.unlu.alimtrack.DTOS.response.VersionReceta.ProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaCompletaResponseDTO;

import java.time.LocalDateTime;
import java.util.Map;

public record EstadoActualProduccionResponseDTO(
        ProduccionResponseDTO produccion,
        VersionRecetaCompletaResponseDTO estructura,
        Map<Long, String> respuestasCampos,
        //        Map<String, Map<String, String>> respuestasTablas, // tablaId -> { "fila_columna": valor }
        // ProgresoProduccionResponseDTO progreso,
        LocalDateTime timestampConsulta
) {
}