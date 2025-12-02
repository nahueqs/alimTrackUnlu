package com.unlu.alimtrack.DTOS.response.Produccion.publico;

import java.time.LocalDateTime;

public record RespuestaCampoResponseDTO(
        Long idRespuesta,
        Long idCampo,
        String valor,
        LocalDateTime timestamp
) {
}
