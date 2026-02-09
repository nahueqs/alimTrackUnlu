package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaTablaRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.exceptions.ValidationException;
import com.unlu.alimtrack.mappers.RespuestaTablaMapper;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.*;
import com.unlu.alimtrack.services.RespuestaTablaService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.UsuarioValidationService;
import com.unlu.alimtrack.services.base.BaseRespuestaService;
import com.unlu.alimtrack.services.validators.RespuestaValidationService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para gestionar respuestas en celdas de tablas.
 * Extiende de BaseRespuestaService para reutilizar lógica común de validación y procesamiento.
 */
@Slf4j
@Service
@Transactional
public class RespuestaTablaServiceImpl extends BaseRespuestaService<RespuestaTablaModel>
        implements RespuestaTablaService {

    private final RespuestaTablaRepository respuestaTablaRepository;
    private final ProduccionRepository produccionRepository;
    private final TablaRepository tablaRepository;
    private final FilaTablaRepository filaTablaRepository;
    private final ColumnaTablaRepository columnaTablaRepository;
    private final UsuarioService usuarioService;
    private final UsuarioValidationService usuarioValidationService;
    private final RespuestaTablaMapper respuestaTablaMapper;

    // Constructor que llama al constructor padre
    public RespuestaTablaServiceImpl(
            RespuestaValidationService validationService,
            RespuestaTablaRepository respuestaTablaRepository,
            ProduccionRepository produccionRepository,
            TablaRepository tablaRepository,
            FilaTablaRepository filaTablaRepository,
            ColumnaTablaRepository columnaTablaRepository,
            UsuarioService usuarioService,
            UsuarioValidationService usuarioValidationService,
            RespuestaTablaMapper respuestaTablaMapper) {

        // LLAMAR AL CONSTRUCTOR PADRE PRIMERO
        super(validationService);

        this.respuestaTablaRepository = respuestaTablaRepository;
        this.produccionRepository = produccionRepository;
        this.tablaRepository = tablaRepository;
        this.filaTablaRepository = filaTablaRepository;
        this.columnaTablaRepository = columnaTablaRepository;
        this.usuarioService = usuarioService;
        this.usuarioValidationService = usuarioValidationService;
        this.respuestaTablaMapper = respuestaTablaMapper;
    }

    /**
     * Guarda o actualiza una respuesta para una celda específica de una tabla en una producción.
     *
     * @param codigoProduccion Código de la producción.
     * @param idTabla ID de la tabla.
     * @param idFila ID de la fila.
     * @param idColumna ID de la columna.
     * @param request DTO con los datos de la respuesta.
     * @return DTO con la respuesta guardada.
     */
    @Override
    @Transactional
    @Retryable(
            retryFor = {
                    LockAcquisitionException.class,
                    ObjectOptimisticLockingFailureException.class,
                    CannotAcquireLockException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public RespuestaCeldaTablaResponseDTO guardarRespuestaTabla(
            String codigoProduccion,
            Long idTabla,
            Long idFila,
            Long idColumna,
            RespuestaTablaRequestDTO request) {

        log.info("Iniciando guardado de respuesta para tabla ID: {}, Fila: {}, Columna: {} en producción: {}",
                idTabla, idFila, idColumna, codigoProduccion);

        // 1. Validar request básico
        validarRequestBasico(request);

        // 2. Obtener usuario validado
        UsuarioModel usuario = obtenerUsuario(request.getEmailCreador());

        // 3. Buscar producción
        ProduccionModel produccion = buscarProduccion(codigoProduccion);

        // 4. Obtener entidades relacionadas
        TablaModel tabla = obtenerTabla(idTabla);
        FilaTablaModel fila = obtenerFila(idFila);
        ColumnaTablaModel columna = obtenerColumna(idColumna);
        TipoDatoCampo tipoColumna = columna.getTipoDato();

        // 5. Validar relaciones
        validarRelaciones(tabla, fila, columna);

        // 6. Buscar o crear respuesta
        RespuestaTablaModel respuesta = obtenerOCrearRespuesta(
                produccion, tabla, fila, columna, usuario);

        // 7. Procesar respuesta (validar y asignar valores) - USANDO MÉTODO DEL PADRE
        log.debug("Procesando y validando valor de respuesta para tipo: {}", tipoColumna);
        procesarRespuesta(respuesta, request, tipoColumna);

        // 8. Guardar y actualizar timestamps
        RespuestaTablaModel respuestaGuardada = respuestaTablaRepository.save(respuesta);
        actualizarFechaModificacionProduccion(produccion);

        log.info("Respuesta de tabla guardada exitosamente. ID: {}", respuestaGuardada.getId());
        log.debug("Detalle respuesta: Tipo={}, Valor={}", tipoColumna, obtenerValorRespuesta(respuestaGuardada, tipoColumna));

        // 9. Retornar DTO con todos los datos
        return construirResponseDTO(respuestaGuardada, tabla, fila, columna);
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void validarRequestBasico(RespuestaTablaRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Request no puede ser nulo");
        }

        if (request.getEmailCreador() == null || request.getEmailCreador().trim().isEmpty()) {
            throw new ValidationException("El email del creador es obligatorio");
        }
    }

    private void validarRelaciones(TablaModel tabla, FilaTablaModel fila, ColumnaTablaModel columna) {
        // Validar que columna pertenece a tabla
        if (!columna.getTabla().getId().equals(tabla.getId())) {
            log.error("Inconsistencia: Columna {} no pertenece a Tabla {}", columna.getId(), tabla.getId());
            throw new ValidationException(
                    "La columna " + columna.getId() + " no pertenece a la tabla " + tabla.getId()
            );
        }
        // Validar que fila pertenece a tabla (si aplica en tu modelo, asumiendo que sí por lógica)
        if (!fila.getTabla().getId().equals(tabla.getId())) {
             log.error("Inconsistencia: Fila {} no pertenece a Tabla {}", fila.getId(), tabla.getId());
             throw new ValidationException(
                    "La fila " + fila.getId() + " no pertenece a la tabla " + tabla.getId()
            );
        }
    }

    private UsuarioModel obtenerUsuario(String email) {
        return usuarioValidationService.validarUsuarioAutorizado(email);
    }

    private ProduccionModel buscarProduccion(String codigoProduccion) {
        return produccionRepository.findByCodigoProduccion(codigoProduccion)
                .orElseThrow(() -> {
                    log.error("Producción no encontrada: {}", codigoProduccion);
                    return new RecursoNoEncontradoException("Producción no encontrada: " + codigoProduccion);
                });
    }

    private TablaModel obtenerTabla(Long idTabla) {
        return tablaRepository.findById(idTabla)
                .orElseThrow(() -> {
                    log.error("Tabla no encontrada: {}", idTabla);
                    return new RecursoNoEncontradoException("Tabla no encontrada: " + idTabla);
                });
    }

    private FilaTablaModel obtenerFila(Long idFila) {
        return filaTablaRepository.findById(idFila)
                .orElseThrow(() -> {
                    log.error("Fila no encontrada: {}", idFila);
                    return new RecursoNoEncontradoException("Fila no encontrada: " + idFila);
                });
    }

    private ColumnaTablaModel obtenerColumna(Long idColumna) {
        return columnaTablaRepository.findById(idColumna)
                .orElseThrow(() -> {
                    log.error("Columna no encontrada: {}", idColumna);
                    return new RecursoNoEncontradoException("Columna no encontrada: " + idColumna);
                });
    }

    private RespuestaTablaModel obtenerOCrearRespuesta(
            ProduccionModel produccion,
            TablaModel tabla,
            FilaTablaModel fila,
            ColumnaTablaModel columna,
            UsuarioModel usuario) {

        Optional<RespuestaTablaModel> existente = respuestaTablaRepository
                .findByProduccionAndTablaIdAndFilaIdAndColumnaId(
                        produccion, tabla.getId(), fila.getId(), columna.getId());

        if (existente.isPresent()) {
            log.debug("Respuesta existente encontrada (ID: {}). Actualizando creador.", existente.get().getId());
            RespuestaTablaModel respuesta = existente.get();
            respuesta.setCreadoPor(usuario);
            return respuesta;
        }

        log.debug("No existe respuesta previa. Creando nueva.");
        RespuestaTablaModel nuevaRespuesta = new RespuestaTablaModel();
        nuevaRespuesta.setProduccion(produccion);
        nuevaRespuesta.setTabla(tabla); // Asignación de la tabla corregida
        nuevaRespuesta.setFila(fila);
        nuevaRespuesta.setColumna(columna);
        nuevaRespuesta.setCreadoPor(usuario);
        nuevaRespuesta.setTimestamp(LocalDateTime.now());

        return nuevaRespuesta;
    }

    private void actualizarFechaModificacionProduccion(ProduccionModel produccion) {
        log.debug("Actualizando fecha de modificación de la producción: {}", produccion.getCodigoProduccion());
        produccion.setFechaModificacion(LocalDateTime.now());
        produccionRepository.save(produccion);
    }

    private RespuestaCeldaTablaResponseDTO construirResponseDTO(
            RespuestaTablaModel respuesta,
            TablaModel tabla,
            FilaTablaModel fila,
            ColumnaTablaModel columna) {

        TipoDatoCampo tipoColumna = columna.getTipoDato();
        Object valor = obtenerValorRespuesta(respuesta, tipoColumna);

        return new RespuestaCeldaTablaResponseDTO(
                tabla.getId(),
                fila.getId(),
                columna.getId(),
                tipoColumna.toString(),
                fila.getNombre(),
                columna.getNombre(),
                valor != null ? valor.toString() : null,
                respuesta.getTimestamp()
        );
    }

    // ========== MÉTODOS DE LA INTERFACE ==========

    @Override
    public List<RespuestaTablaModel> buscarTodasRespuestasPorProduccion(ProduccionModel produccion) {
        log.debug("Buscando todas las respuestas de tabla para producción: {}", produccion.getCodigoProduccion());
        return respuestaTablaRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
    }

    @Override
    public List<RespuestaTablaModel> buscarRespuestasPorTabla(
            ProduccionModel produccion, Long idTabla) {
        log.debug("Buscando respuestas para tabla ID: {} en producción: {}", idTabla, produccion.getCodigoProduccion());
        return respuestaTablaRepository.findByProduccionAndTablaId(produccion, idTabla);
    }

    @Override
    public boolean validarValorParaColumna(Long idColumna, String valor) {
        log.debug("Validando valor '{}' para columna ID: {}", valor, idColumna);
        try {
            ColumnaTablaModel columna = obtenerColumna(idColumna);
            TipoDatoCampo tipoColumna = columna.getTipoDato();

            // Validaciones básicas según tipo
            switch (tipoColumna) {
                case TEXTO:
                    return valor == null || valor.length() <= 1000;
                case DECIMAL:
                    try {
                        new java.math.BigDecimal(valor);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                case ENTERO:
                    try {
                        java.math.BigDecimal num = new java.math.BigDecimal(valor);
                        return num.stripTrailingZeros().scale() <= 0;
                    } catch (Exception e) {
                        return false;
                    }
                case FECHA:
                    try {
                        java.time.LocalDateTime.parse(valor);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                case BOOLEANO:
                    String lower = valor.toLowerCase();
                    return "true".equals(lower) || "false".equals(lower) ||
                            "1".equals(lower) || "0".equals(lower);
                default:
                    return true;
            }
        } catch (Exception e) {
            log.warn("Error validando valor para columna {}: {}", idColumna, e.getMessage());
            return false;
        }
    }

    /**
     * Vacía (elimina lógicamente) la respuesta de una celda de tabla.
     *
     * @param codigoProduccion Código de la producción.
     * @param idTabla ID de la tabla.
     * @param idFila ID de la fila.
     * @param idColumna ID de la columna.
     * @param emailUsuario Email del usuario que realiza la acción.
     * @return DTO con la respuesta vaciada, o null si no existía.
     */
    @Override
    public RespuestaCeldaTablaResponseDTO vaciarRespuestaTabla(
            String codigoProduccion,
            Long idTabla,
            Long idFila,
            Long idColumna,
            String emailUsuario) {

        log.info("Iniciando vaciado de respuesta para tabla ID: {}, Fila: {}, Columna: {} en producción: {}",
                idTabla, idFila, idColumna, codigoProduccion);

        try {
            // Obtener datos necesarios
            UsuarioModel usuario = obtenerUsuario(emailUsuario);
            ProduccionModel produccion = buscarProduccion(codigoProduccion);
            TablaModel tabla = obtenerTabla(idTabla);
            FilaTablaModel fila = obtenerFila(idFila);
            ColumnaTablaModel columna = obtenerColumna(idColumna);

            // Buscar respuesta existente
            Optional<RespuestaTablaModel> respuestaOpt = respuestaTablaRepository
                    .findByProduccionAndTablaIdAndFilaIdAndColumnaId(
                            produccion, idTabla, idFila, idColumna);

            if (respuestaOpt.isPresent()) {
                // Si existe, vaciar sus valores
                RespuestaTablaModel respuesta = respuestaOpt.get();
                respuesta.limpiarValores();
                respuesta.setTimestamp(LocalDateTime.now());

                RespuestaTablaModel respuestaActualizada = respuestaTablaRepository.save(respuesta);
                actualizarFechaModificacionProduccion(produccion);

                log.info("Respuesta de tabla vaciada exitosamente. ID: {}", respuestaActualizada.getId());
                return construirResponseDTO(respuestaActualizada, tabla, fila, columna);
            } else {
                // Si no existe, no hay nada que vaciar
                log.warn("Intento de vaciar respuesta inexistente para tabla ID: {}, Fila: {}, Columna: {}", idTabla, idFila, idColumna);
                return null;
            }
        } catch (Exception e) {
            log.error("Error al vaciar respuesta de tabla: {}", e.getMessage());
            throw new ValidationException("Error al vaciar respuesta: " + e.getMessage());
        }
    }
}
