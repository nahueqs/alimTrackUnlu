package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.UsuarioModel;

import java.util.List;
import java.util.Optional;

public interface RespuestaCampoService {

    /**
     * Guarda o actualiza una respuesta de campo
     */
    RespuestaCampoResponseDTO guardarRespuestaCampo(
            String codigoProduccion,
            Long idCampo,
            RespuestaCampoRequestDTO request);

    /**
     * Vacía una respuesta de campo (asigna null a todos los valores)
     */
    RespuestaCampoResponseDTO vaciarRespuestaCampo(
            String codigoProduccion,
            Long idCampo,
            String emailUsuario);

    /**
     * Busca respuesta existente
     */
    Optional<RespuestaCampoModel> buscarRespuestaExistente(
            ProduccionModel produccion,
            CampoSimpleModel campo);

    /**
     * Crea nueva respuesta
     */
    RespuestaCampoModel crearNuevaRespuesta(
            ProduccionModel produccion,
            CampoSimpleModel campo,
            UsuarioModel usuario);

    /**
     * Actualiza fecha de modificación de producción
     */
    void actualizarFechaModificacionProduccion(ProduccionModel produccion);

    /**
     * Busca todas las respuestas de una producción
     */
    List<RespuestaCampoModel> buscarTodasRespuestasPorProduccion(ProduccionModel produccion);

    /**
     * Valida respuesta sin guardarla
     */
    boolean validarRespuestaSinGuardar(Long idCampo, RespuestaCampoRequestDTO request);
}