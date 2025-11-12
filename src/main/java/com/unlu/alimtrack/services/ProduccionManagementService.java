package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.SeccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.respuestas.EstadoActualProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.respuestas.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.mappers.RespuestaCampoMapper;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
import com.unlu.alimtrack.services.validators.ProductionManagerServiceValidator;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import com.unlu.alimtrack.services.validators.inputs.InputValidator;
import com.unlu.alimtrack.services.validators.inputs.IntegerValidationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduccionManagementService {
    private final ProduccionRepository produccionRepository;
    private final ProduccionMapper produccionMapper;
    private final RespuestaCampoMapper respuestaCampoMapper;
    private final ProductionManagerServiceValidator productionManagerServiceValidator;
    private final VersionRecetaValidator versionRecetaValidator;
    private final VersionRecetaMetadataService versionRecetaMetadataService;
    private final InputValidator inputValidator;
    private final RespuestaCampoRepository respuestaCampoRepository;
    private final AutoSaveService autoSaveService;


    public ProduccionCambioEstadoRequestDTO updateEstado(Long productionId,
                                                         ProduccionCambioEstadoRequestDTO nuevoEstado) {
        // Validar transiciones de estado válidas
        // Ej: No se puede cancelar una producción completada
        return null;
    }

    public ProduccionResponseDTO saveProduccion(String codigoProduccion, ProduccionCreateDTO createDTO) {

        // verifico la que el cuerpo del dto coincida con la url de la peticion
        // verifico que no exista una produccion con el mismo codigo verificarIntegridadDatosCreacion(codigoProduccion, createDTO);
        // verifico que exista el usuario creador
        // verifico que la version padre exista
        productionManagerServiceValidator.verificarCreacionProduccion(codigoProduccion, createDTO);
        ProduccionModel modelFinal = produccionMapper.createDTOtoModel(createDTO);
        produccionRepository.save(modelFinal);
        return produccionMapper.modelToResponseDTO(modelFinal);
    }

    @Transactional
    public RespuestaCampoResponseDTO guardarRespuestaCampo(String codigoProduccion, Long idCampo,
                                                           RespuestaCampoRequestDTO request) {
        log.debug("Guardando respuesta para campo: {}, producción: {}", idCampo, codigoProduccion);

        ProduccionModel produccion = productionManagerServiceValidator.validarProduccionParaEdicion(codigoProduccion);
        CampoSimpleModel campo = productionManagerServiceValidator.validarCampoExiste(idCampo);
        versionRecetaValidator.validarCampoPerteneceAVersion(produccion, campo);
        // validarInputCampo(request.valor(), campo.getTipoDato());


        RespuestaCampoModel respuesta = guardarOActualizarRespuesta(produccion, campo, request);

        autoSaveService.ejecutarAutoSaveInmediato(produccion.getProduccion());

        log.info("Respuesta guardada exitosamente - Producción: {}, Campo: {}", codigoProduccion, idCampo);
        return respuestaCampoMapper.respuestaCampoToResponseDTO(respuesta);
    }

    private void validarInputCampo(String valor, TipoDatoCampo tipoDatoCampo) {
        switch (tipoDatoCampo) {
            case ENTERO:
                inputValidator.addValidationStrategy(new IntegerValidationStrategy());
                inputValidator.validateWithStrategy(valor, new IntegerValidationStrategy());
                break;
            default:
                throw new IllegalArgumentException("Tipo de dato no soportado: " + tipoDatoCampo);
        }
    }

    private RespuestaCampoModel guardarOActualizarRespuesta(ProduccionModel produccion,
                                                            CampoSimpleModel campo,
                                                            RespuestaCampoRequestDTO request) {

        RespuestaCampoModel respuestaExistente = respuestaCampoRepository
                .findByIdProduccionAndIdCampo(produccion, campo);

        if (respuestaExistente != null) {
            // UPDATE
            respuestaExistente.setValor(request.valor());
            respuestaExistente.setTimestamp(LocalDateTime.now());
            return respuestaExistente;
        } else {
            // INSERT
            RespuestaCampoModel nuevaRespuesta = new RespuestaCampoModel();
            nuevaRespuesta.setIdProduccion(produccion);
            nuevaRespuesta.setIdCampo(campo);
            nuevaRespuesta.setValor(request.valor());
            nuevaRespuesta.setTimestamp(LocalDateTime.now());
            return respuestaCampoRepository.save(nuevaRespuesta);
        }
    }

    @Transactional(readOnly = true)
    public EstadoActualProduccionResponseDTO obtenerEstadoActual(String codigoProduccion) {
        log.debug("Obteniendo estado actual para producción: {}", codigoProduccion);

        // 1. Obtener producción
        ProduccionModel produccion = produccionRepository.findByCodigoProduccion(codigoProduccion);
        if (produccion == null) {
            throw new RecursoNoEncontradoException("Producción no encontrada: " + codigoProduccion);
        }

        // 2. Obtener estructura COMPLETA desde la versión de receta
        String codigoVersion = produccion.getVersionReceta().getCodigoVersionReceta();
        List<SeccionResponseDTO> estructura = versionRecetaMetadataService
                .findAllSeccionesByVersionReceta(codigoVersion);

        // 3. Obtener respuestas de campos
        List<RespuestaCampoModel> respuestasCampos = respuestaCampoRepository.findByIdProduccion(produccion);
        Map<Long, String> respuestasCamposMap = respuestasCampos.stream()
                .collect(Collectors.toMap(
                        r -> r.getIdCampo().getId(),
                        RespuestaCampoModel::getValor
                ));

        // 4. Calcular progreso
        //ProgresoResponseDTO progreso = calcularProgreso(estructura, respuestasCamposMap);

        return new EstadoActualProduccionResponseDTO(
                produccionMapper.modelToResponseDTO(produccion),
                estructura,
                respuestasCamposMap,
                //  new HashMap<>(), // respuestasTablas placeholder
                //   progreso,
                LocalDateTime.now()
        );
    }
}