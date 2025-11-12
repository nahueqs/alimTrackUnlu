package com.unlu.alimtrack.DTOS.response.produccion.respuestas;

public record ProgresoProduccionResponseDTO(
        int camposRespondidos,
        int camposTotales,
        int tablasRespondidas,
        int tablasTotales,
        double porcentajeCompletado,
        int elementosTotales,
        int elementosCompletados
) {
}
