package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaTablaRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;

import java.util.List;

public interface RespuestaTablaService {

    /**
     * Guarda o actualiza una respuesta de tabla
     */
    RespuestaCeldaTablaResponseDTO guardarRespuestaTabla(
            String codigoProduccion,
            Long idTabla,
            Long idFila,
            Long idColumna,
            RespuestaTablaRequestDTO request);

    /**
     * Vacía una respuesta de tabla (asigna null a todos los valores)
     */
    RespuestaCeldaTablaResponseDTO vaciarRespuestaTabla(
            String codigoProduccion,
            Long idTabla,
            Long idFila,
            Long idColumna,
            String emailUsuario);

    /**
     * Busca todas las respuestas de una producción
     */
    List<RespuestaTablaModel> buscarTodasRespuestasPorProduccion(ProduccionModel produccion);

    /**
     * Busca respuestas por tabla específica
     */
    List<RespuestaTablaModel> buscarRespuestasPorTabla(
            ProduccionModel produccion,
            Long idTabla);

    /**
     * Valida si un valor es válido para una columna sin guardarlo
     */
    boolean validarValorParaColumna(Long idColumna, String valor);
}