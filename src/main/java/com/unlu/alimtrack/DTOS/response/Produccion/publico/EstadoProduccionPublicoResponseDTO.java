package com.unlu.alimtrack.DTOS.response.Produccion.publico;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;

import java.time.LocalDateTime;

public record EstadoProduccionPublicoResponseDTO(
        String codigoProduccion,
        TipoEstadoProduccion estado,
        LocalDateTime ultimaModificacion
) {
}
