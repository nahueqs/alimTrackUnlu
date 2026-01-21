package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionMetadataModifyRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCeldaTablaResquestDTO;
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

    @Override
    public ProduccionMetadataResponseDTO iniciarProduccion(ProduccionCreateDTO createDTO) {
        log.info("Iniciando nueva producción: {}", createDTO.codigoProduccion());

        productionManagerServiceValidator.verificarCreacionProduccion(createDTO);

        ProduccionModel nuevaProduccion = produccionMapper.createDTOtoModel(createDTO);

        nuevaProduccion.setEstado(TipoEstadoProduccion.EN_PROCESO);
        nuevaProduccion.setFechaInicio(LocalDateTime.now());
        nuevaProduccion.setFechaModificacion(LocalDateTime.now());

        ProduccionModel produccionGuardada = produccionRepository.save(nuevaProduccion);

        produccionEventPublisher.publicarProduccionCreada(
                this,
                produccionGuardada.getCodigoProduccion(),
                produccionGuardada.getVersionReceta().getCodigoVersionReceta(),
                produccionGuardada.getLote(),
                produccionGuardada.getFechaInicio(),
                produccionGuardada.getFechaFin()
        );
        log.info("Producción {} iniciada exitosamente. Eventos publicados.", produccionGuardada.getCodigoProduccion());

        return produccionMapper.modelToResponseDTO(produccionGuardada);
    }

    @Override
    public void updateEstado(String codigoProduccion, ProduccionCambioEstadoRequestDTO nuevoEstadoDTO) {
        log.info("Actualizando estado producción {} a {}", codigoProduccion, nuevoEstadoDTO.valor());

        usuarioValidationService.validarUsuarioAutorizado(nuevoEstadoDTO.emailCreador());
        produccionStateService.cambiarEstado(codigoProduccion, nuevoEstadoDTO);
        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);

        produccionEventPublisher.publicarEstadoCambiado(
                this,
                produccion.getCodigoProduccion(),
                produccion.getEstado(),
                produccion.getFechaFin()
        );

        log.info("Estado de producción {} actualizado a {}. Evento publicado.",
                codigoProduccion, nuevoEstadoDTO.valor());

    }

    @Override
    public RespuestaCampoResponseDTO guardarRespuestaCampo(String codigoProduccion, Long idCampo, RespuestaCampoRequestDTO request) {
        log.debug("Iniciando guardado de respuesta para campo. Producción: {}, Campo ID: {}", codigoProduccion, idCampo);

        // 1. Validar usuario (mantener tu lógica actual)
        usuarioValidationService.validarUsuarioAutorizado(request.getEmailCreador());

        ProduccionModel produccion = productionManagerServiceValidator.validarProduccionParaEdicion(codigoProduccion);
        log.debug("Validación de producción OK. Producción encontrada: {}", produccion.getCodigoProduccion());

        CampoSimpleModel campo = productionManagerServiceValidator.validarCampoExiste(idCampo);
        log.debug("Validación de campo OK. Campo encontrada: {}", campo.getId());

        versionRecetaValidator.validarCampoPerteneceAVersion(produccion, campo);
        log.debug("Validación de pertenencia de campo a versión OK.");

        log.debug("Paso 2: Usando servicio especializado...");
        RespuestaCampoResponseDTO respuesta = respuestaCampoService.guardarRespuestaCampo(
                codigoProduccion, idCampo, request);
        log.debug("Persistencia de respuesta de campo OK.");

        produccionEventPublisher.publicarRespuestaCampoGuardada(
                this,
                produccion.getCodigoProduccion(),
                campo.getId(),
                respuesta.getValor()
        );

        log.debug("Finalizado guardado de respuesta para campo. Producción: {}, Campo ID: {}. Evento publicado.",
                codigoProduccion, idCampo);
        return respuesta;
    }

    @Override
    public RespuestaCeldaTablaResponseDTO guardarRespuestaCeldaTabla(String codigoProduccion, Long idTabla, Long idFila, Long idColumna, RespuestaCeldaTablaResquestDTO request) {

        log.debug("Iniciando guardado de respuesta para celda. Producción: {}, Tabla: {}, Fila: {}, Columna: {}", codigoProduccion, idTabla, idFila, idColumna);


        usuarioValidationService.validarUsuarioAutorizado(request.emailCreador());

        log.debug("Paso 1: Validando contexto...");
        ProduccionModel produccion = productionManagerServiceValidator.validarProduccionParaEdicion(codigoProduccion);
        log.debug("Validación de producción OK. Producción encontrada: {}", produccion.getCodigoProduccion());

        productionManagerServiceValidator.validarTablaPertenceAVersionProduccion(produccion.getVersionReceta(), idTabla);
        log.debug("Validación de pertenencia de tabla a versión OK.");

        productionManagerServiceValidator.combinacionFilaColumnaPerteneceTabla(idFila, idColumna, idTabla);
        log.debug("Validación de combinación fila-columna OK.");

        log.debug("Paso 2: Usando servicio especializado...");
        RespuestaTablaRequestDTO requestTabla = convertirARespuestaTablaRequestDTO(request);

        RespuestaCeldaTablaResponseDTO respuesta = respuestaTablaService.guardarRespuestaTabla(codigoProduccion, idTabla, idFila, idColumna, requestTabla);


        log.debug("Persistencia de respuesta de tabla OK.");

        produccionEventPublisher.publicarRespuestaTablaGuardada(
                this,
                produccion.getCodigoProduccion(),
                idTabla,
                idFila,
                idColumna,
                request.valor()
        );

        log.debug("Finalizado guardado de respuesta para celda. Evento publicado.");

        return respuesta;
    }

    private RespuestaTablaRequestDTO convertirARespuestaTablaRequestDTO(RespuestaCeldaTablaResquestDTO oldRequest) {
        RespuestaTablaRequestDTO newRequest = new RespuestaTablaRequestDTO();
        newRequest.setEmailCreador(oldRequest.emailCreador());

        // Solo asignar valorTexto (para compatibilidad)
        newRequest.setValorTexto(oldRequest.valor());
        // Los otros campos (valorNumerico, valorFecha, valorBooleano) quedan null

        return newRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public UltimasRespuestasProduccionResponseDTO getUltimasRespuestas(String codigoProduccion) {
        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);
        log.debug("ProduccionModel obtenida");

        ProduccionMetadataResponseDTO produccionMetadata = produccionMapper.modelToResponseDTO(produccion);
        log.debug("produccionMetadata obtenida");

        String codigoVersion = produccion.getVersionReceta().getCodigoVersionReceta();

        VersionEstructuraPublicResponseDTO estructura = versionRecetaEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(codigoVersion);
        log.debug("estructura obtenida");
        List<RespuestaCampoModel> respuestasCampos = respuestaCampoService.buscarTodasRespuestasPorProduccion(produccion);
        log.debug("respuestasCampos obtenida");
        List<RespuestaTablaModel> respuestasTablas = respuestaTablaService.buscarTodasRespuestasPorProduccion(produccion);
        log.debug("respuestasTablas obtenida");
        ProgresoProduccionResponseDTO progreso = produccionProgressService.calcularProgreso(estructura.totalCampos(), estructura.totalCeldas(), respuestasCampos, respuestasTablas);
        log.debug("progreso obtenida");

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

    @Override
    public void updateMetadata(String codigoProduccion, ProduccionMetadataModifyRequestDTO request) {

        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);
        log.debug("ProduccionModel obtenida");

        productionManagerServiceValidator.validarUpdateMetadata(codigoProduccion, request);
        log.debug("update de metadata es valido");

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

        produccionEventPublisher.publicarMetadataActualizada(
                this,
                produccion.getCodigoProduccion(),
                produccion.getLote(),
                produccion.getEncargado(),
                produccion.getObservaciones()
        );

        log.debug("Metadata actualizada para la producción {}. Evento publicado.", codigoProduccion);


    }

    private ProduccionModel buscarProduccionPorCodigo(String codigo) {
        return produccionRepository.findByCodigoProduccion(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producción no encontrada: " + codigo));
    }

}
