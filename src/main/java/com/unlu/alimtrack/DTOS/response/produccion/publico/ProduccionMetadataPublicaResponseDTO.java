package com.unlu.alimtrack.DTOS.response.produccion.publico;

import java.time.LocalDateTime;

public record ProduccionMetadataPublicaResponseDTO(
        String codigoProduccion,
        String lote,
        String estado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
) {
}
