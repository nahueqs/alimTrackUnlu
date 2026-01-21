// ProduccionStateService.java
package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.models.ProduccionModel;

public interface ProduccionStateService {
    /**
     * Cambia el estado de una producción con todas las validaciones
     */
    void cambiarEstado(String codigoProduccion, ProduccionCambioEstadoRequestDTO request);

    /**
     * Valida si una transición de estado es permitida
     */
    void validarTransicionEstado(ProduccionModel produccion, TipoEstadoProduccion nuevoEstado);

    /**
     * Verifica si un estado es considerado final
     */
    boolean esEstadoFinal(TipoEstadoProduccion estado);

    /**
     * Obtiene el estado actual de una producción
     */
    TipoEstadoProduccion obtenerEstadoActual(String codigoProduccion);
}