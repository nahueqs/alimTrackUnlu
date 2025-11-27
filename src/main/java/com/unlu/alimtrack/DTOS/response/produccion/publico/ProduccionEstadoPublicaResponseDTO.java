package com.unlu.alimtrack.DTOS.response.produccion.publico;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;

import java.time.LocalDateTime;

public record ProduccionEstadoPublicaResponseDTO(
        String codigoProduccion,
        TipoEstadoProduccion estado,
        LocalDateTime fechaUltimaModificacion
) {
}
