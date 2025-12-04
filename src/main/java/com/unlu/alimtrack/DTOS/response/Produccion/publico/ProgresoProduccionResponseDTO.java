package com.unlu.alimtrack.DTOS.response.Produccion.publico;

public record ProgresoProduccionResponseDTO(
        int totalCampos,
        int camposRespondidos,
        int totalCeldasTablas,
        int celdasRespondidas,
        int totalElementos,
        int elementosRespondidos,
        double porcentajeCompletado
) {
    public ProgresoProduccionResponseDTO(int totalCampos, int camposRespondidos, int totalCeldasTablas, int celdasRespondidas, int totalElementos, int elementosRespondidos) {
        this(totalCampos, camposRespondidos, totalCeldasTablas, celdasRespondidas, totalElementos, elementosRespondidos,
                totalElementos > 0 ? Math.round((elementosRespondidos * 100.0) / totalElementos * 100.0) / 100.0 : 0.0);
    }


}