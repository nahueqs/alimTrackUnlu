package com.unlu.alimtrack.DTOS.response.produccion.respuestas;

public record ProgresoProduccionResponseDTO(
        Integer seccionesFinalizadas,
        Integer camposSimplesRespondidos,
        Integer celdasTablasRespondidas,
        double porcentajeCompletado
) {
}
