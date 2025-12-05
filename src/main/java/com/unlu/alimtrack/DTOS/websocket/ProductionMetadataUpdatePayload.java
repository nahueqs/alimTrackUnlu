package com.unlu.alimtrack.DTOS.websocket;

public record ProductionMetadataUpdatePayload(
        String codigoVersion,
        String lote,
        String encargado,
        String observaciones
) {
}
