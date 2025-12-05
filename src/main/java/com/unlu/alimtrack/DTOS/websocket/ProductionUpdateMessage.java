package com.unlu.alimtrack.DTOS.websocket;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;

import java.time.LocalDateTime;

// Main wrapper message for all production updates
public record ProductionUpdateMessage(
        String type, // e.g., "FIELD_UPDATED", "TABLE_CELL_UPDATED", "STATE_CHANGED", "PRODUCTION_METADATA_UPDATED"
        String codigoProduccion,
        LocalDateTime timestamp,
        Object payload // Specific payload for each type of update
) {

    public static ProductionUpdateMessage fieldUpdated(String codigoProduccion, FieldUpdatePayload payload) {
        return new ProductionUpdateMessage("FIELD_UPDATED", codigoProduccion, LocalDateTime.now(), payload);
    }

    public static ProductionUpdateMessage tableCellUpdated(String codigoProduccion, TableCellUpdatePayload payload) {
        return new ProductionUpdateMessage("TABLE_CELL_UPDATED", codigoProduccion, LocalDateTime.now(), payload);
    }

    public static ProductionUpdateMessage stateChanged(String codigoProduccion, ProductionStateUpdatePayload payload) {
        return new ProductionUpdateMessage("STATE_CHANGED", codigoProduccion, LocalDateTime.now(), payload);
    }

    public static ProductionUpdateMessage metadataUpdated(String codigoProduccion, ProductionMetadataUpdatePayload payload) {
        return new ProductionUpdateMessage("PRODUCTION_METADATA_UPDATED", codigoProduccion, LocalDateTime.now(), payload);
    }
}



