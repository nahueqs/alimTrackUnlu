package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCeldaTablaResquestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.CambioEstadoProduccionInvalido;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.mappers.RespuestaCampoMapper;
import com.unlu.alimtrack.mappers.RespuestaTablaMapper;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.*;
import com.unlu.alimtrack.services.AutoSaveService;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import com.unlu.alimtrack.services.validators.ProductionManagerServiceValidator;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProduccionManagementServiceImpl implements ProduccionManagementService {

    private final ProduccionRepository produccionRepository;
    private final RespuestaCampoRepository respuestaCampoRepository;
    private final RespuestaTablaRepository respuestaTablaRepository;

    private final ProductionManagerServiceValidator productionManagerServiceValidator;
    private final VersionRecetaValidator versionRecetaValidator;
    private final VersionRecetaEstructuraService versionRecetaEstructuraService;
    private final AutoSaveService autoSaveService;

    private final ProduccionMapper produccionMapper;
    private final RespuestaCampoMapper respuestaCampoMapper;
    private final UsuarioService usuarioService;
    private final RespuestaTablaMapper respuestaTablasMapper;
    private final TablaRepository tablaRepository;
    private final FilaTablaRepository filaTablaRepository;
    private final ColumnaTablaRepository columnaTablaRepository;


    @Override
    public ProduccionMetadataResponseDTO iniciarProduccion(ProduccionCreateDTO createDTO) {
        log.info("Iniciando nueva producción: {}", createDTO.codigoProduccion());

        productionManagerServiceValidator.verificarCreacionProduccion(createDTO);

        ProduccionModel nuevaProduccion = produccionMapper.createDTOtoModel(createDTO);
        nuevaProduccion.setEstado(TipoEstadoProduccion.EN_PROCESO);
        nuevaProduccion.setFechaInicio(LocalDateTime.now());
        nuevaProduccion.setFechaModificacion(LocalDateTime.now());

        return produccionMapper.modelToResponseDTO(produccionRepository.save(nuevaProduccion));
    }

    @Override
    public void updateEstado(String codigoProduccion, ProduccionCambioEstadoRequestDTO nuevoEstadoDTO) {
        log.info("Actualizando estado producción {} a {}", codigoProduccion, nuevoEstadoDTO.valor());

        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);
        validarTransicionDeEstado(produccion, nuevoEstadoDTO);

        TipoEstadoProduccion nuevoEstado = TipoEstadoProduccion.valueOf(nuevoEstadoDTO.valor());
        produccion.setEstado(nuevoEstado);

        if (esEstadoFinal(nuevoEstado)) {
            produccion.setFechaFin(LocalDateTime.now());
        }

        autoSaveService.ejecutarAutoSaveInmediato(produccion.getProduccion());
        produccionRepository.save(produccion);
    }

    @Override
    public RespuestaCampoResponseDTO guardarRespuestaCampo(String codigoProduccion, Long idCampo, RespuestaCampoRequestDTO request) {
        log.debug("Iniciando guardado de respuesta para campo. Producción: {}, Campo ID: {}", codigoProduccion, idCampo);

        log.debug("Paso 1: Validando contexto...");
        ProduccionModel produccion = productionManagerServiceValidator.validarProduccionParaEdicion(codigoProduccion);
        log.debug("Validación de producción OK. Producción encontrada: {}", produccion.getCodigoProduccion());

        CampoSimpleModel campo = productionManagerServiceValidator.validarCampoExiste(idCampo);
        log.debug("Validación de campo OK. Campo encontrado: {}", campo.getId());

        versionRecetaValidator.validarCampoPerteneceAVersion(produccion, campo);
        log.debug("Validación de pertenencia de campo a versión OK.");

        log.debug("Paso 2: Persistiendo respuesta...");
        RespuestaCampoModel respuesta = guardarOActualizarRespuestaCampo(produccion, campo, request);
        log.debug("Persistencia de respuesta de campo OK. ID Respuesta: {}", respuesta.getId());

        log.debug("Paso 3: Iniciando efectos secundarios (Auto-Save)...");
        autoSaveService.ejecutarAutoSaveInmediato(produccion.getProduccion());
        log.debug("Auto-Save para producción {} iniciado asíncronamente.", produccion.getCodigoProduccion());

        log.debug("Finalizado guardado de respuesta para campo. Producción: {}, Campo ID: {}", codigoProduccion, idCampo);
        return respuestaCampoMapper.toResponseDTO(respuesta);
    }


    @Override
    public RespuestaCeldaTablaResponseDTO guardarRespuestaCeldaTabla(String codigoProduccion, Long idTabla, Long idFila, Long idColumna, RespuestaCeldaTablaResquestDTO request) {
        log.debug("Iniciando guardado de respuesta para celda. Producción: {}, Tabla: {}, Fila: {}, Columna: {}", codigoProduccion, idTabla, idFila, idColumna);

        log.debug("Paso 1: Validando contexto...");
        ProduccionModel produccion = productionManagerServiceValidator.validarProduccionParaEdicion(codigoProduccion);
        log.debug("Validación de producción OK. Producción encontrada: {}", produccion.getCodigoProduccion());

        productionManagerServiceValidator.validarTablaPertenceAVersionProduccion(produccion.getVersionReceta(), idTabla);
        log.debug("Validación de pertenencia de tabla a versión OK.");

        productionManagerServiceValidator.combinacionFilaColumnaPerteneceTabla(idFila, idColumna, idTabla);
        log.debug("Validación de combinación fila-columna OK.");

        log.debug("Paso 2: Persistiendo respuesta...");
        RespuestaTablaModel respuesta = guardarOActualizarRespuestaTabla(produccion, idTabla, idFila, idColumna, request);
        log.debug("Persistencia de respuesta de tabla OK. ID Respuesta: {}", respuesta.getId());

        log.debug("Paso 3: Iniciando efectos secundarios (Auto-Save)...");
        autoSaveService.ejecutarAutoSaveInmediato(produccion.getProduccion());
        log.debug("Auto-Save para producción {} iniciado asíncronamente.", produccion.getCodigoProduccion());

        log.debug("Finalizado guardado de respuesta para celda. Producción: {}, Tabla: {}, Fila: {}, Columna: {}", codigoProduccion, idTabla, idFila, idColumna);
        return respuestaTablasMapper.toResponseDTO(respuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public UltimasRespuestasProduccionResponseDTO getUltimasRespuestas(String codigoProduccion) {
        ProduccionModel produccion = buscarProduccionPorCodigo(codigoProduccion);
        log.debug("ProduccionModel obtenida");

        ProduccionMetadataResponseDTO produccionMetadata = produccionMapper.modelToResponseDTO(produccion);
        log.debug("produccionMetadata obtenida");

        String codigoVersion = produccion.getVersionReceta().getCodigoVersionReceta();

        //1. Recuperar estructura (esqueleto de la receta)
        VersionEstructuraPublicResponseDTO estructura = versionRecetaEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(codigoVersion);
        log.debug("estructura obtenida");
        List<RespuestaCampoModel> respuestasCampos = respuestaCampoRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
        log.debug("respuestasCampos obtenida");
        List<RespuestaTablaModel> respuestasTablas = respuestaTablaRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
        log.debug("respuestasTablas obtenida");
        ProgresoProduccionResponseDTO progreso = calcularProgresoGeneral(estructura.totalCampos(), estructura.totalCeldas(), respuestasCampos, respuestasTablas);
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

    // ============================================================================================
    // 3. HELPERS Y LÓGICA DE SOPORTE
    // ============================================================================================
    private ProgresoProduccionResponseDTO calcularProgresoGeneral(
            Integer totalCampos, Integer totalCeldas,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas) {


        long camposRespondidos = respuestasCampos.stream()
                .filter(rc -> rc.getValor() != null && !rc.getValor().trim().isEmpty()) // <-- AÑADIR ESTA LÍNEA
                .map(RespuestaCampoModel::getIdCampo)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        long celdasRespondidas = respuestasTablas.stream()
                .map(respuesta -> new CeldaKey(
                        respuesta.getIdTabla() != null ? respuesta.getIdTabla().getId() : null,
                        respuesta.getFila() != null ? respuesta.getFila().getId() : null,
                        respuesta.getColumna() != null ? respuesta.getColumna().getId() : null
                ))
                .filter(key -> key.idTabla() != null && key.idFila() != null && key.idColumna() != null)
                .distinct()
                .count();

        int totalGlobal = totalCampos + totalCeldas;
        long respondidoGlobal = camposRespondidos + celdasRespondidas;

        double porcentaje = totalGlobal > 0 ? (respondidoGlobal * 100.0) / totalGlobal : 0.0;

        return new ProgresoProduccionResponseDTO(
                totalCampos, (int) camposRespondidos,
                totalCeldas, (int) celdasRespondidas,
                totalGlobal, (int) respondidoGlobal,
                porcentaje
        );
    }

    private ProduccionModel buscarProduccionPorCodigo(String codigo) {
        return produccionRepository.findByCodigoProduccion(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producción no encontrada: " + codigo));
    }

    private RespuestaCampoModel guardarOActualizarRespuestaCampo(ProduccionModel produccion, CampoSimpleModel campo, RespuestaCampoRequestDTO request) {
        log.debug("Buscando respuesta existente para Producción ID {} y Campo ID {}", produccion.getProduccion(), campo.getId());
        RespuestaCampoModel respuesta = respuestaCampoRepository.findByIdProduccionAndIdCampo(produccion, campo);
        UsuarioModel usuarioCreador = usuarioService.getUsuarioModelByEmail(request.emailCreador());

        if (respuesta == null) {
            log.debug("No se encontró respuesta existente. Creando una nueva.");
            respuesta = new RespuestaCampoModel();
            respuesta.setIdProduccion(produccion);
            respuesta.setIdCampo(campo);
            respuesta.setCreadoPor(usuarioCreador);
        } else {
            log.debug("Respuesta existente encontrada. ID: {}. Se actualizará.", respuesta.getId());
        }

        respuesta.setValor(request.valor());
        respuesta.setTimestamp(LocalDateTime.now());
        actualizarFechaModificacion(produccion, LocalDateTime.now());
        log.debug("Fecha de modificación de la producción actualizada.");

        return respuestaCampoRepository.save(respuesta);
    }

    private RespuestaTablaModel guardarOActualizarRespuestaTabla(
            ProduccionModel produccion, Long idTabla, Long idFila, Long idColumna,
            RespuestaCeldaTablaResquestDTO request) {

        log.debug("Buscando respuesta de tabla existente para Producción ID {}, Tabla ID {}, Fila ID {}, Columna ID {}", produccion.getProduccion(), idTabla, idFila, idColumna);
        RespuestaTablaModel respuesta = respuestaTablaRepository.findByProduccionAndIdTablaIdAndFilaIdAndColumnaId(produccion, idTabla, idFila, idColumna).orElse(null);
        UsuarioModel usuarioCreador = usuarioService.getUsuarioModelByEmail(request.emailCreador());

        if (respuesta == null) {
            log.debug("No se encontró respuesta de tabla existente. Creando una nueva.");
            TablaModel tabla = tablaRepository.findById(idTabla).orElseThrow(() -> new RecursoNoEncontradoException("Tabla no encontrada con ID: " + idTabla));
            FilaTablaModel fila = filaTablaRepository.findById(idFila).orElseThrow(() -> new RecursoNoEncontradoException("Fila no encontrada con ID: " + idFila));
            ColumnaTablaModel columna = columnaTablaRepository.findById(idColumna).orElseThrow(() -> new RecursoNoEncontradoException("Columna no encontrada con ID: " + idColumna));

            respuesta = new RespuestaTablaModel();
            respuesta.setProduccion(produccion);
            respuesta.setIdTabla(tabla);
            respuesta.setCreadoPor(usuarioCreador);
            respuesta.setFila(fila);
            respuesta.setColumna(columna);
        } else {
            log.debug("Respuesta de tabla existente encontrada. ID: {}. Se actualizará.", respuesta.getId());
        }

        respuesta.setValor(request.valor());
        respuesta.setTimestamp(LocalDateTime.now());
        actualizarFechaModificacion(produccion, LocalDateTime.now());
        log.debug("Fecha de modificación de la producción actualizada.");

        return respuestaTablaRepository.save(respuesta);
    }


    // ============================================================================================
    // 4. ESTADOS Y PROGRESO
    // ============================================================================================

    private void validarTransicionDeEstado(ProduccionModel produccion, ProduccionCambioEstadoRequestDTO nuevoEstado) {
        TipoEstadoProduccion actual = produccion.getEstado();
        TipoEstadoProduccion destino = TipoEstadoProduccion.valueOf(nuevoEstado.valor());

        if (actual == destino) {
            throw new CambioEstadoProduccionInvalido("La producción ya está en estado " + actual);
        }
        if (esEstadoFinal(actual)) {
            throw new CambioEstadoProduccionInvalido("No se puede modificar una producción " + actual);
        }
        if (actual == TipoEstadoProduccion.EN_PROCESO && !esEstadoFinal(destino)) {
            throw new CambioEstadoProduccionInvalido("Desde EN_PROCESO solo se puede FINALIZAR o CANCELAR");
        }
    }

    private boolean esEstadoFinal(TipoEstadoProduccion estado) {
        return estado == TipoEstadoProduccion.FINALIZADA || estado == TipoEstadoProduccion.CANCELADA;
    }

    private void actualizarFechaModificacion(ProduccionModel produccion, LocalDateTime fechaModificacion) {
        produccion.setFechaModificacion(fechaModificacion);
        produccionRepository.save(produccion);
    }

    // Record para usar en el cálculo de progreso de celdas de tabla
    private record CeldaKey(Long idTabla, Long idFila, Long idColumna) {
    }

}