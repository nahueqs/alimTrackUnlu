package com.unlu.alimtrack.DTOS.response.Produccion.publico;

import java.time.LocalDateTime;

public record MetadataProduccionPublicaResponseDTO(
        String codigoProduccion,
        String codigoVersion,
        String lote,
        String estado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        LocalDateTime fechaModificacion
) {
}
