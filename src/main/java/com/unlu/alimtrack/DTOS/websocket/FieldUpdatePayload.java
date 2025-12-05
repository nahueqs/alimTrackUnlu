package com.unlu.alimtrack.DTOS.websocket;

// Payload for field updates
public record FieldUpdatePayload( // Made public
                                  Long idCampo,
                                  String valor
) {
}
