package com.unlu.alimtrack.DTOS.websocket;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;

import java.time.LocalDateTime;

// Payload for production state changes
public record ProductionStateUpdatePayload( // Made public
                                            TipoEstadoProduccion estado,
                                            LocalDateTime fechaFin // Only relevant for FINALIZADA or CANCELADA
) {
}
