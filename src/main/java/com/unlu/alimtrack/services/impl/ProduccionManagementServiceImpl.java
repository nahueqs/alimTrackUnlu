package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaCompletaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.CambioEstadoProduccionInvalido;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.mappers.RespuestaCampoMapper;
import com.unlu.alimtrack.mappers.RespuestaTablaMapper;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
import com.unlu.alimtrack.repositories.RespuestaTablaRepository;
import com.unlu.alimtrack.repositories.TablaRepository;
import com.unlu.alimtrack.services.AutoSaveService;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import com.unlu.alimtrack.services.validators.ProductionManagerServiceValidator;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProduccionManagementServiceImpl implements ProduccionManagementService {

    private final ProduccionRepository produccionRepository;
    private final RespuestaCampoRepository respuestaCampoRepository;
    private final RespuestaTablaRepository respuestaTablaRepository;
    private final TablaRepository tablaRepository;

    private final ProductionManagerServiceValidator productionManagerServiceValidator;
    private final VersionRecetaValidator versionRecetaValidator;
    private final VersionRecetaEstructuraService versionRecetaEstructuraService;
    private final AutoSaveService autoSaveService;

    private final ProduccionMapper produccionMapper;
    private final RespuestaCampoMapper respuestaCampoMapper;
    private final UsuarioService usuarioService;
    private final RespuestaTablaMapper respuestaTablasMapper;


    @Override
    public ProduccionMetadataResponseDTO iniciarProduccion(ProduccionCreateDTO createDTO) {
        log.info("Iniciando nueva producción: {}", createDTO.codigoProduccion());

        productionManagerServiceValidator.verificarCreacionProduccion(createDTO);

        ProduccionModel nuevaProduccion = produccionMapper.createDTOtoModel(createDTO);
        nuevaProduccion.setEstado(TipoEstadoProduccion.EN_PROCESO);
        nuevaProduccion.setFechaInicio(LocalDateTime.now());

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
        // 1. Validar contexto
        ProduccionModel produccion = productionManagerServiceValidator.validarProduccionParaEdicion(codigoProduccion);
        CampoSimpleModel campo = productionManagerServiceValidator.validarCampoExiste(idCampo);
        versionRecetaValidator.validarCampoPerteneceAVersion(produccion, campo);

        // 2. Persistir
        RespuestaCampoModel respuesta = guardarOActualizarRespuesta(produccion, campo, request);

        // 3. Efectos secundarios
        autoSaveService.ejecutarAutoSaveInmediato(produccion.getProduccion());

        return respuestaCampoMapper.toResponseDTO(respuesta);
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
        VersionRecetaCompletaResponseDTO estructura = versionRecetaEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(codigoVersion);
        log.debug("estructura obtenida");
        List<RespuestaCampoModel> respuestasCampos = respuestaCampoRepository.findByIdProduccion(produccion);
        log.debug("respuestasCampos obtenida");
        List<RespuestaTablaModel> respuestasTablas = respuestaTablaRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
        log.debug("respuestasTablas obtenida");
        ProgresoProduccionResponseDTO progreso = calcularProgresoGeneral2(estructura, respuestasCampos, respuestasTablas);
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


        ProduccionModel produccion = buscarProduccionPorCodigo("TARTA-25-10-A");
        log.debug("ProduccionModel obtenida");

        ProduccionMetadataResponseDTO produccionMetadata = produccionMapper.modelToResponseDTO(produccion);
        log.debug("produccionMetadata obtenida");

        String codigoVersion = produccion.getVersionReceta().getCodigoVersionReceta();

        //1. Recuperar estructura (esqueleto de la receta)
        VersionRecetaCompletaResponseDTO estructura = versionRecetaEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(codigoVersion);
        log.debug("estructura obtenida");
        List<RespuestaCampoModel> respuestasCampos = respuestaCampoRepository.findByIdProduccion(produccion);
        log.debug("respuestasCampos obtenida");
        List<RespuestaTablaModel> respuestasTablas = respuestaTablaRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
        log.debug("respuestasTablas obtenida");
        ProgresoProduccionResponseDTO progreso = calcularProgresoGeneral2(estructura, respuestasCampos, respuestasTablas);
        log.debug("progreso obtenida");

        return new UltimasRespuestasProduccionResponseDTO(
                produccionMetadata,
                respuestaCampoMapper.toResponseDTOList(respuestasCampos),
                respuestaTablasMapper.toResponseDTOList(respuestasTablas),
                progreso,
                LocalDateTime.now()
        );

    }

    // ============================================================================================
    // 3. HELPERS Y LÓGICA DE SOPORTE
    // ============================================================================================
    private ProgresoProduccionResponseDTO calcularProgresoGeneral2(
            VersionRecetaCompletaResponseDTO estructura,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas) {

        int totalCampos = estructura.totalCampos();
        int totalCeldas = estructura.totalCeldasTablas();

        int camposRespondidos = respuestasCampos.size();
        int celdasRespondidas = respuestasTablas.size();

        int totalGlobal = totalCampos + totalCeldas;
        int respondidoGlobal = camposRespondidos + celdasRespondidas;

        double porcentaje = totalGlobal > 0 ? (respondidoGlobal * 100.0) / totalGlobal : 0.0;

        return new ProgresoProduccionResponseDTO(
                totalCampos, camposRespondidos,
                totalCeldas, celdasRespondidas,
                totalGlobal, respondidoGlobal,
                porcentaje
        );
    }

    private ProduccionModel buscarProduccionPorCodigo(String codigo) {
        return produccionRepository.findByCodigoProduccion(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producción no encontrada: " + codigo));
    }

    private RespuestaCampoModel guardarOActualizarRespuesta(ProduccionModel produccion, CampoSimpleModel campo, RespuestaCampoRequestDTO request) {
        RespuestaCampoModel respuesta = respuestaCampoRepository.findByIdProduccionAndIdCampo(produccion, campo);
        UsuarioModel usuarioCreador = usuarioService.getUsuarioModelByEmail(request.emailCreador());


        if (respuesta == null) {
            respuesta = new RespuestaCampoModel();
            respuesta.setIdProduccion(produccion);
            respuesta.setIdCampo(campo);
            respuesta.setCreadoPor(usuarioCreador);
        }

        respuesta.setValor(request.valor());
        respuesta.setTimestamp(LocalDateTime.now());

        return respuestaCampoRepository.save(respuesta);
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


}

