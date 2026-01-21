package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.exceptions.ValidationException;
import com.unlu.alimtrack.mappers.RespuestaCampoMapper;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.CampoSimpleRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
import com.unlu.alimtrack.services.RespuestaCampoService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.base.BaseRespuestaService;
import com.unlu.alimtrack.services.validators.RespuestaValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class RespuestaCampoServiceImpl extends BaseRespuestaService<RespuestaCampoModel>
        implements RespuestaCampoService {

    private final RespuestaCampoRepository respuestaCampoRepository;
    private final ProduccionRepository produccionRepository;
    private final CampoSimpleRepository campoSimpleRepository;
    private final UsuarioService usuarioService;
    private final RespuestaCampoMapper respuestaCampoMapper;

    // Constructor que llama al constructor padre
    public RespuestaCampoServiceImpl(
            RespuestaValidationService validationService,
            RespuestaCampoRepository respuestaCampoRepository,
            ProduccionRepository produccionRepository,
            CampoSimpleRepository campoSimpleRepository,
            UsuarioService usuarioService,
            RespuestaCampoMapper respuestaCampoMapper) {

        // LLAMAR AL CONSTRUCTOR PADRE
        super(validationService);

        this.respuestaCampoRepository = respuestaCampoRepository;
        this.produccionRepository = produccionRepository;
        this.campoSimpleRepository = campoSimpleRepository;
        this.usuarioService = usuarioService;
        this.respuestaCampoMapper = respuestaCampoMapper;
    }

    @Override
    public RespuestaCampoResponseDTO guardarRespuestaCampo(
            String codigoProduccion,
            Long idCampo,
            RespuestaCampoRequestDTO request) {

        log.info("Guardando respuesta para campo. Producción: {}, Campo: {}",
                codigoProduccion, idCampo);

        // 1. Validar request básico
        validarRequestBasico(request, idCampo);

        // 2. Obtener campo y su tipo
        CampoSimpleModel campo = obtenerCampo(idCampo);
        TipoDatoCampo tipoCampo = campo.getTipoDato();

        // 3. Obtener usuario
        UsuarioModel usuario = obtenerUsuario(request.getEmailCreador());

        // 4. Buscar producción
        ProduccionModel produccion = buscarProduccion(codigoProduccion);

        // 5. Buscar o crear respuesta
        RespuestaCampoModel respuesta = obtenerOCrearRespuesta(produccion, campo, usuario);

        // 6. Procesar respuesta (validar y asignar valores) - USANDO MÉTODO DEL PADRE
        procesarRespuesta(respuesta, request, tipoCampo);

        // 7. Guardar y actualizar timestamps
        RespuestaCampoModel respuestaGuardada = respuestaCampoRepository.save(respuesta);
        actualizarFechaModificacionProduccion(produccion);

        log.debug("Respuesta de campo guardada exitosamente. ID: {}, Tipo: {}, Valor: {}",
                respuestaGuardada.getId(),
                tipoCampo,
                obtenerValorRespuesta(respuestaGuardada, tipoCampo));

        // 8. Retornar DTO
        return respuestaCampoMapper.toResponseDTO(respuestaGuardada);
    }

    @Override
    public RespuestaCampoResponseDTO vaciarRespuestaCampo(
            String codigoProduccion,
            Long idCampo,
            String emailUsuario) {

        log.info("Vaciando respuesta para campo. Producción: {}, Campo: {}",
                codigoProduccion, idCampo);

        try {
            // 1. Obtener usuario
            UsuarioModel usuario = obtenerUsuario(emailUsuario);

            // 2. Buscar producción
            ProduccionModel produccion = buscarProduccion(codigoProduccion);

            // 3. Obtener campo
            CampoSimpleModel campo = obtenerCampo(idCampo);
            TipoDatoCampo tipoCampo = campo.getTipoDato();

            // 4. Buscar respuesta existente
            Optional<RespuestaCampoModel> respuestaOpt = respuestaCampoRepository
                    .findTopByIdProduccionAndIdCampoOrderByTimestampDesc(produccion, campo);

            if (respuestaOpt.isPresent()) {
                // Si existe, vaciar sus valores
                RespuestaCampoModel respuesta = respuestaOpt.get();
                respuesta.limpiarValores();
                respuesta.setTimestamp(LocalDateTime.now());

                RespuestaCampoModel respuestaActualizada = respuestaCampoRepository.save(respuesta);
                actualizarFechaModificacionProduccion(produccion);

                log.debug("Respuesta de campo vaciada exitosamente. ID: {}", respuestaActualizada.getId());
                return respuestaCampoMapper.toResponseDTO(respuestaActualizada);
            } else {
                // Si no existe, no hay nada que vaciar
                log.debug("No existe respuesta de campo para vaciar");
                return null;
            }
        } catch (Exception e) {
            log.error("Error al vaciar respuesta de campo", e);
            throw new ValidationException("Error al vaciar respuesta: " + e.getMessage());
        }
    }

    @Override
    public Optional<RespuestaCampoModel> buscarRespuestaExistente(
            ProduccionModel produccion,
            CampoSimpleModel campo) {

        return respuestaCampoRepository.findTopByIdProduccionAndIdCampoOrderByTimestampDesc(
                produccion, campo);
    }

    @Override
    public RespuestaCampoModel crearNuevaRespuesta(
            ProduccionModel produccion,
            CampoSimpleModel campo,
            UsuarioModel usuario) {

        RespuestaCampoModel respuesta = new RespuestaCampoModel();
        respuesta.setIdProduccion(produccion);
        respuesta.setIdCampo(campo);
        respuesta.setCreadoPor(usuario);
        respuesta.setTimestamp(LocalDateTime.now());

        return respuesta;
    }

    @Override
    public void actualizarFechaModificacionProduccion(ProduccionModel produccion) {
        produccion.setFechaModificacion(LocalDateTime.now());
        produccionRepository.save(produccion);
    }

    @Override
    public List<RespuestaCampoModel> buscarTodasRespuestasPorProduccion(ProduccionModel produccion) {
        return respuestaCampoRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
    }

    @Override
    public boolean validarRespuestaSinGuardar(Long idCampo, RespuestaCampoRequestDTO request) {
        try {
            // 1. Obtener campo y su tipo
            CampoSimpleModel campo = obtenerCampo(idCampo);
            TipoDatoCampo tipoCampo = campo.getTipoDato();

            // 2. Validar usando el servicio de validación
            validationService.validarRespuesta(
                    tipoCampo,
                    request.getValorTexto(),
                    request.getValorNumerico(),
                    request.getValorFecha(),
                    request.getValorBooleano()
            );

            return true;
        } catch (Exception e) {
            log.debug("Validación fallida para campo {}: {}", idCampo, e.getMessage());
            return false;
        }
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void validarRequestBasico(RespuestaCampoRequestDTO request, Long idCampo) {
        if (request == null) {
            throw new ValidationException("Request no puede ser nulo");
        }

        if (request.getEmailCreador() == null || request.getEmailCreador().trim().isEmpty()) {
            throw new ValidationException("El email del creador es obligatorio");
        }

        if (request.getIdCampo() == null) {
            throw new ValidationException("El ID del campo es obligatorio");
        }

        if (!request.getIdCampo().equals(idCampo)) {
            throw new ValidationException("ID de campo inconsistente");
        }
    }

    private CampoSimpleModel obtenerCampo(Long idCampo) {
        return campoSimpleRepository.findById(idCampo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Campo no encontrado con ID: " + idCampo));
    }

    private UsuarioModel obtenerUsuario(String email) {
        return usuarioService.getUsuarioModelByEmail(email);
    }

    private ProduccionModel buscarProduccion(String codigoProduccion) {
        return produccionRepository.findByCodigoProduccion(codigoProduccion)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producción no encontrada: " + codigoProduccion));
    }

    private RespuestaCampoModel obtenerOCrearRespuesta(
            ProduccionModel produccion,
            CampoSimpleModel campo,
            UsuarioModel usuario) {

        Optional<RespuestaCampoModel> existente = respuestaCampoRepository
                .findTopByIdProduccionAndIdCampoOrderByTimestampDesc(produccion, campo);

        if (existente.isPresent()) {
            log.debug("Respuesta existente encontrada. ID: {}", existente.get().getId());
            RespuestaCampoModel respuesta = existente.get();
            respuesta.setCreadoPor(usuario);
            return respuesta;
        }

        log.debug("Creando nueva respuesta de campo");
        return crearNuevaRespuesta(produccion, campo, usuario);
    }

    // ========== MÉTODOS ADICIONALES UTILES ==========

    /**
     * Método para obtener una respuesta específica
     */
    public Optional<RespuestaCampoResponseDTO> obtenerRespuestaCampo(
            String codigoProduccion,
            Long idCampo) {

        try {
            ProduccionModel produccion = buscarProduccion(codigoProduccion);
            CampoSimpleModel campo = obtenerCampo(idCampo);

            Optional<RespuestaCampoModel> respuestaOpt = buscarRespuestaExistente(produccion, campo);

            return respuestaOpt.map(respuestaCampoMapper::toResponseDTO);
        } catch (Exception e) {
            log.error("Error al obtener respuesta de campo", e);
            return Optional.empty();
        }
    }

    /**
     * Método para obtener todas las respuestas de una producción con sus campos
     */
    public List<RespuestaCampoResponseDTO> obtenerTodasRespuestasConDetalles(
            String codigoProduccion) {

        ProduccionModel produccion = buscarProduccion(codigoProduccion);
        List<RespuestaCampoModel> respuestas = buscarTodasRespuestasPorProduccion(produccion);

        return respuestas.stream()
                .map(respuestaCampoMapper::toResponseDTO)
                .toList();
    }

    /**
     * Método para verificar si un campo tiene respuesta
     */
    public boolean tieneRespuesta(String codigoProduccion, Long idCampo) {
        try {
            ProduccionModel produccion = buscarProduccion(codigoProduccion);
            CampoSimpleModel campo = obtenerCampo(idCampo);

            Optional<RespuestaCampoModel> respuesta = buscarRespuestaExistente(produccion, campo);
            return respuesta.isPresent() && !respuesta.get().esRespuestaVacia();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Método para obtener el valor de una respuesta como String
     */
    public String obtenerValorComoString(String codigoProduccion, Long idCampo) {
        try {
            ProduccionModel produccion = buscarProduccion(codigoProduccion);
            CampoSimpleModel campo = obtenerCampo(idCampo);
            TipoDatoCampo tipoCampo = campo.getTipoDato();

            Optional<RespuestaCampoModel> respuestaOpt = buscarRespuestaExistente(produccion, campo);

            if (respuestaOpt.isPresent()) {
                RespuestaCampoModel respuesta = respuestaOpt.get();
                Object valor = obtenerValorRespuesta(respuesta, tipoCampo);
                return valor != null ? valor.toString() : null;
            }

            return null;
        } catch (Exception e) {
            log.error("Error al obtener valor como string", e);
            return null;
        }
    }
}