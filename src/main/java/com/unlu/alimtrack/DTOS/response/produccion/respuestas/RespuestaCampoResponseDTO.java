package com.unlu.alimtrack.DTOS.response.produccion.respuestas;

import java.time.LocalDateTime;

public record RespuestaCampoResponseDTO(
        Long idRespuesta,
        Long idCampo,
        String valor,
        LocalDateTime timestamp
) {
}
