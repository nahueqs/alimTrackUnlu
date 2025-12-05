package com.unlu.alimtrack.DTOS.websocket;

// Payload for table cell updates
public record TableCellUpdatePayload( // Made public
                                      Long idTabla,
                                      Long idFila,
                                      Long idColumna,
                                      String valor
) {
}
