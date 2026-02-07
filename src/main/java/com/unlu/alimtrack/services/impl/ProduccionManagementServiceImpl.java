package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionMetadataModifyRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaTablaRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.eventos.ProduccionEventPublisher;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.mappers.RespuestaCampoMapper;
import com.unlu.alimtrack.mappers.RespuestaTablaMapper;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.*;
import com.unlu.alimtrack.services.validators.ProductionManagerServiceValidator;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de gestión de producciones.
 * Orquesta las operaciones principales de una producción: inicio, cambio de estado,
 * registro de respuestas y actualización de metadatos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProduccionManagementServiceImpl implements ProduccionManagementService {

    private final ProduccionEventPublisher produccionEventPublisher;
    private final ProduccionRepository produccionRepository;
    private final VersionRecetaEstructuraService versionRecetaEstructuraService;
    private final UsuarioValidationService usuarioValidationService;
    private final ProductionManagerServiceValidator productionManagerServiceValidator;
    private final VersionRecetaValidator versionRecetaValidator;
    private final ProduccionMapper produccionMapper;
    private final RespuestaCampoMapper respuestaCampoMapper;
    private final RespuestaTablaMapper respuestaTablasMapper;
    private final RespuestaCampoService respuestaCampoService;
    private final RespuestaTablaService respuestaTablaService;
    private final ProduccionStateService produccionStateService;
    private final ProduccionProgressService produccionProgressService;

    /**
     * Inicia una nueva producción basada en una versión de receta.
     *
     * @param createDTO DTO con los datos para crear la producción.
     * @return DTO con la metadata de la producción creada.
     */
    @Override
    public ProduccionMetadataResponseDTO iniciarProduccion(ProduccionCreateDTO createDTO) {
        log.info("Iniciando nueva producción con código: {}", createDTO.codigoProduccion());

        productionManagerServiceValidator.verificarCreacionProduccion(createDTO);

        ProduccionModel nuevaProduccion = produccionMapper.createDTOtoModel(createDTO);
        nuevaProduccion.setEstado(TipoEstadoProduccion.EN_PROCESO);
        nuevaProduccion.setFechaInicio(LocalDateTime.now());
        nuevaProduccion.setFechaModificacion(LocalDateTime.now());

        ProduccionModel produccionGuardada = produccionRepository.save(nuevaProduccion);
        log.info("Producción {} guardada en base de datos.", produccionGuardada.getCodigoProduccion());

        produccionEventPublisher.publicarProduccionCreada(
                this,
                produccionGuardada.getCodigoProduccion(),
                produccionGuardada.getVersionReceta().getCodigoVersionReceta(),
                produccionGuardada.getLote(),
                produccionGuardada.getFechaInicio(),
                produccionGuardada.getFechaFin()
        );
        log.debug("Evento de producción creada publicado para: {}", produccionGuardada.getCodigoProduccion());

        return produccionMapper.modelToResponseDTO(produccionGuardada);
    }

    /**
     * Actualiza el estado de una producción existente.
     *
     * @param codigoProduccion Código de la producción.
     * @param nuevoEstadoDTO DTO con el nuevo estado y usuario responsable.
     */
    @Override
    public void updateEstado(String codigoProduccion, ProduccionCambioEstadoRequestDTO nuevoEstadoDTO) {
        log.info("Solicitud de cambio de estado para producción {} a {}", codigoProduccion, nuevoEstadoDTO.valor());

        usuarioValidationService.validarUsuarioAutorizado(nuevoEstadoDTO.emailCreador());
        produccionStateService.cambiarEstado(codigoProduccion, nuevoEstadoDTO);
        
        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);

        produccionEventPublisher.publicarEstadoCambiado(
                this,
                produccion.getCodigoProduccion(),
                produccion.getEstado(),
                produccion.getFechaFin()
        );

        log.info("Estado de producción {} actualizado exitosamente a {}. Evento publicado.",
                codigoProduccion, nuevoEstadoDTO.valor());
    }

    /**
     * Guarda la respuesta a un campo simple en una producción.
     *
     * @param codigoProduccion Código de la producción.
     * @param idCampo ID del campo.
     * @param request DTO con el valor de la respuesta.
     * @return DTO con la respuesta guardada.
     */
    @Override
    public RespuestaCampoResponseDTO guardarRespuestaCampo(String codigoProduccion, Long idCampo, RespuestaCampoRequestDTO request) {
        log.debug("Iniciando guardado de respuesta para campo ID: {} en producción: {}", idCampo, codigoProduccion);

        // 1. Validar usuario
        usuarioValidationService.validarUsuarioAutorizado(request.getEmailCreador());

        // 2. Validar contexto
        ProduccionModel produccion = productionManagerServiceValidator.validarProduccionParaEdicion(codigoProduccion);
        CampoSimpleModel campo = productionManagerServiceValidator.validarCampoExiste(idCampo);
        versionRecetaValidator.validarCampoPerteneceAVersion(produccion, campo);
        
        log.debug("Validaciones de contexto exitosas para campo ID: {}", idCampo);

        // 3. Guardar respuesta
        RespuestaCampoResponseDTO respuesta = respuestaCampoService.guardarRespuestaCampo(
                codigoProduccion, idCampo, request);
        
        log.info("Respuesta de campo guardada correctamente.");

        // 4. Publicar evento
        produccionEventPublisher.publicarRespuestaCampoGuardada(
                this,
                produccion.getCodigoProduccion(),
                campo.getId(),
                respuesta.getValor()
        );

        return respuesta;
    }

    /**
     * Guarda la respuesta a una celda de tabla en una producción.
     *
     * @param codigoProduccion Código de la producción.
     * @param idTabla ID de la tabla.
     * @param idFila ID de la fila.
     * @param idColumna ID de la columna.
     * @param request DTO con el valor de la respuesta.
     * @return DTO con la respuesta guardada.
     */
    @Override
    public RespuestaCeldaTablaResponseDTO guardarRespuestaCeldaTabla(String codigoProduccion, Long idTabla, Long idFila, Long idColumna, RespuestaTablaRequestDTO request) {
        log.debug("Iniciando guardado de respuesta para celda [T:{}, F:{}, C:{}] en producción: {}", idTabla, idFila, idColumna, codigoProduccion);

        usuarioValidationService.validarUsuarioAutorizado(request.getEmailCreador());

        // 1. Validar contexto
        ProduccionModel produccion = productionManagerServiceValidator.validarProduccionParaEdicion(codigoProduccion);
        productionManagerServiceValidator.validarTablaPertenceAVersionProduccion(produccion.getVersionReceta(), idTabla);
        productionManagerServiceValidator.combinacionFilaColumnaPerteneceTabla(idFila, idColumna, idTabla);
        
        log.debug("Validaciones de contexto exitosas para celda de tabla.");

        // 2. Guardar respuesta
        RespuestaCeldaTablaResponseDTO respuesta = respuestaTablaService.guardarRespuestaTabla(codigoProduccion, idTabla, idFila, idColumna, request);
        
        log.info("Respuesta de celda de tabla guardada correctamente.");

        // 3. Publicar evento
        produccionEventPublisher.publicarRespuestaTablaGuardada(
                this,
                produccion.getCodigoProduccion(),
                idTabla,
                idFila,
                idColumna,
                respuesta.valor()
        );

        return respuesta;
    }

    /**
     * Obtiene las últimas respuestas registradas para una producción, junto con su progreso.
     *
     * @param codigoProduccion Código de la producción.
     * @return DTO con las respuestas y el progreso.
     */
    @Override
    @Transactional(readOnly = true)
    public UltimasRespuestasProduccionResponseDTO getUltimasRespuestas(String codigoProduccion) {
        log.info("Recuperando últimas respuestas para producción: {}", codigoProduccion);
        
        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);
        ProduccionMetadataResponseDTO produccionMetadata = produccionMapper.modelToResponseDTO(produccion);

        String codigoVersion = produccion.getVersionReceta().getCodigoVersionReceta();
        VersionEstructuraPublicResponseDTO estructura = versionRecetaEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(codigoVersion);

        List<RespuestaCampoModel> respuestasCampos = respuestaCampoService.buscarTodasRespuestasPorProduccion(produccion);
        List<RespuestaTablaModel> respuestasTablas = respuestaTablaService.buscarTodasRespuestasPorProduccion(produccion);
        
        log.debug("Calculando progreso con {} respuestas de campo y {} respuestas de tabla.", respuestasCampos.size(), respuestasTablas.size());
        ProgresoProduccionResponseDTO progreso = produccionProgressService.calcularProgreso(estructura.totalCampos(), estructura.totalCeldas(), respuestasCampos, respuestasTablas);

        return new UltimasRespuestasProduccionResponseDTO(
                produccionMetadata,
                respuestaCampoMapper.toResponseDTOList(respuestasCampos),
                respuestaTablasMapper.toResponseDTOList(respuestasTablas),
                progreso,
                LocalDateTime.now()
        );
    }

    @Override
    public UltimasRespuestasProduccionResponseDTO test() {
        return null;
    }

    /**
     * Actualiza la metadata (lote, encargado, observaciones) de una producción.
     *
     * @param codigoProduccion Código de la producción.
     * @param request DTO con los datos a actualizar.
     */
    @Override
    public void updateMetadata(String codigoProduccion, ProduccionMetadataModifyRequestDTO request) {
        log.info("Actualizando metadata para producción: {}", codigoProduccion);

        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);
        productionManagerServiceValidator.validarUpdateMetadata(codigoProduccion, request);

        if (request.encargado() != null) {
            produccion.setEncargado(request.encargado());
        }
        if (request.lote() != null) {
            produccion.setLote(request.lote());
        }
        if (request.observaciones() != null) {
            produccion.setObservaciones(request.observaciones());
        }

        produccionRepository.save(produccion);
        log.info("Metadata actualizada correctamente en base de datos.");

        produccionEventPublisher.publicarMetadataActualizada(
                this,
                produccion.getCodigoProduccion(),
                produccion.getLote(),
                produccion.getEncargado(),
                produccion.getObservaciones()
        );
        log.debug("Evento de metadata actualizada publicado.");
    }

    /**
     * Elimina una producción del sistema.
     *
     * @param codigoProduccion Código de la producción a eliminar.
     */
    @Override
    public void deleteProduccion(String codigoProduccion) {
        log.info("Intentando eliminar producción con código: {}", codigoProduccion);
        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);
        produccionRepository.delete(produccion);
        log.info("Producción {} eliminada exitosamente.", codigoProduccion);

        produccionEventPublisher.publicarProduccionEliminada(this, codigoProduccion);
        log.debug("Evento de producción eliminada publicado.");
    }

    private ProduccionModel buscarProduccionPorCodigo(String codigo) {
        return produccionRepository.findByCodigoProduccion(codigo)
                .orElseThrow(() -> {
                    log.error("Producción no encontrada: {}", codigo);
                    return new RecursoNoEncontradoException("Producción no encontrada: " + codigo);
                });
    }
}
