package com.unlu.alimtrack.DTOS.websocket;

import java.time.LocalDateTime;

// Payload for general production metadata updates
public record ProductionMetadataUpdatePayload( // Made public
                                               String codigoVersion,
                                               String lote,
                                               LocalDateTime fechaInicio,
                                               LocalDateTime fechaFin
) {
}
