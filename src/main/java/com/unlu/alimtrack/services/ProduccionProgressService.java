// ProduccionProgressService.java
package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;

import java.util.List;

public interface ProduccionProgressService {
    /**
     * Calcula el progreso general basado en respuestas de campo y tabla
     */
    ProgresoProduccionResponseDTO calcularProgreso(
            Integer totalCampos, Integer totalCeldas,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas);

    /**
     * Calcula campos respondidos únicos
     */
    long calcularCamposRespondidos(List<RespuestaCampoModel> respuestasCampos);

    /**
     * Calcula celdas respondidas únicas
     */
    long calcularCeldasRespondidas(List<RespuestaTablaModel> respuestasTablas);
}