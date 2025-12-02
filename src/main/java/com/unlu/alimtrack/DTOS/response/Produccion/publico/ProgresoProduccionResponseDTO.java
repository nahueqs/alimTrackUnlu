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
    public ProgresoProduccionResponseDTO {
        porcentajeCompletado = Math.round(porcentajeCompletado * 100.0) / 100.0;
    }

    public double getPorcentajeCampos() {
        return totalCampos > 0 ? Math.round((camposRespondidos * 100.0) / totalCampos * 100.0) / 100.0 : 0.0;
    }

    public double getPorcentajeTablas() {
        return totalCeldasTablas > 0 ? Math.round((celdasRespondidas * 100.0) / totalCeldasTablas * 100.0) / 100.0 : 0.0;
    }
}