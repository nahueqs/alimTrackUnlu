package com.unlu.alimtrack.DTOS.response.produccion.publico;

import java.time.LocalDateTime;

public record RespuestaCeldaTablaResponseDTO(
        Long idTabla,
        Long idFila,
        Long idColumna,
        String tipoDatoColumna,
        String nombreFila,
        String nombreColumna,
        String valor,
        LocalDateTime timestampRespuesta
) {
}