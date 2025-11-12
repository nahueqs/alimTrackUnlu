package com.unlu.alimtrack.DTOS.response.produccion.respuestas;

import java.time.LocalDateTime;
import java.util.Map;

public record RespuestaTablaResponseDTO(
        Long idTabla,
        Map<String, String> respuestas,  // "filaId_columnaId" -> valor
        LocalDateTime timestampUltimaActualizacion
) {
}
