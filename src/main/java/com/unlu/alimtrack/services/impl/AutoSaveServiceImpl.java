package com.unlu.alimtrack.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.models.AutoSaveProduccionModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import com.unlu.alimtrack.repositories.AutoSaveRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
import com.unlu.alimtrack.repositories.RespuestaTablaRepository;
import com.unlu.alimtrack.services.AutoSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de guardado automático (AutoSave) para producciones.
 * Permite guardar y recuperar el estado de una producción de forma asíncrona o síncrona.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AutoSaveServiceImpl implements AutoSaveService {

    private final AutoSaveRepository autoSaveRepository;
    private final RespuestaCampoRepository respuestaCampoRepository;
    private final RespuestaTablaRepository respuestaTablaRepository;
    private final ProduccionRepository produccionRepository;
    private final ObjectMapper objectMapper;

    /**
     * Ejecuta el guardado automático de una producción de forma asíncrona.
     *
     * @param produccion La producción a guardar.
     */
    @Override
    @Async
    public void ejecutarAutoSaveInmediato(ProduccionModel produccion) {
        try {
            log.debug("Iniciando proceso de auto-save asíncrono para producción ID: {}", produccion.getProduccion());
            log.info("Ejecutando auto-save para producción código: {}", produccion.getCodigoProduccion());
            
            guardarAutoSave(produccion);
            
            log.info("Auto-save para producción código: {} completado exitosamente.", produccion.getCodigoProduccion());
        } catch (Exception e) {
            log.error("Error crítico durante el auto-save asíncrono para producción código: {}", produccion.getCodigoProduccion(), e);
        }
    }

    /**
     * Guarda el estado actual de una producción en la base de datos.
     * Si ya existe un registro de auto-save, lo actualiza.
     *
     * @param produccion La producción a guardar.
     */
    @Override
    @Transactional
    public void guardarAutoSave(ProduccionModel produccion) {
        log.debug("Obteniendo estado actual completo de la producción {}", produccion.getCodigoProduccion());
        
        try {
            Map<String, Object> estadoActual = obtenerEstadoActualProduccion(produccion);

            AutoSaveProduccionModel autoSave = autoSaveRepository
                    .findByProduccion(produccion)
                    .orElse(new AutoSaveProduccionModel());

            if (autoSave.getId() == null) {
                log.debug("Creando nuevo registro de auto-save para la producción {}", produccion.getCodigoProduccion());
                autoSave.setProduccion(produccion);
            } else {
                log.debug("Actualizando registro de auto-save existente (ID: {}) para la producción {}", autoSave.getId(), produccion.getCodigoProduccion());
            }

            autoSave.setDatos(estadoActual);
            autoSave.setTimestamp(LocalDateTime.now());

            autoSaveRepository.save(autoSave);
            log.debug("Auto-save persistido correctamente para producción {}", produccion.getCodigoProduccion());
        } catch (Exception e) {
            log.error("Error al persistir el auto-save para la producción {}: {}", produccion.getCodigoProduccion(), e.getMessage());
            throw e;
        }
    }

    private Map<String, Object> obtenerEstadoActualProduccion(ProduccionModel produccion) {
        Map<String, Object> estado = new HashMap<>();
        estado.put("codigo_produccion", produccion.getCodigoProduccion());
        estado.put("estado", produccion.getEstado().name());
        estado.put("timestamp_autosave", LocalDateTime.now().toString());
        estado.put("lote", produccion.getLote());
        estado.put("encargado", produccion.getEncargado());
        estado.put("observaciones", produccion.getObservaciones());

        // Respuestas de campos simples
        List<RespuestaCampoModel> respuestasCampos = respuestaCampoRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
        Map<String, String> respuestasCamposMap = respuestasCampos.stream()
                .collect(Collectors.toMap(
                        respuesta -> "campo_" + respuesta.getIdCampo().getId(),
                        RespuestaCampoModel::getValorTexto
                ));
        estado.put("respuestas_campos", respuestasCamposMap);

        // Respuestas de tablas
        List<RespuestaTablaModel> respuestasTablas = respuestaTablaRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
        Map<String, String> respuestasTablasMap = respuestasTablas.stream()
                .collect(Collectors.toMap(
                        respuesta -> respuesta.getId().toString(),
                        respuesta -> {
                            if (respuesta.getColumna() != null && respuesta.getColumna().getTipoDato() != null) {
                                Object valor = respuesta.getValor(respuesta.getColumna().getTipoDato());
                                return valor != null ? valor.toString() : "";
                            }
                            return "";
                        }
                ));
        estado.put("respuestas_tablas", respuestasTablasMap);

        return estado;
    }

    /**
     * Recupera los datos del último auto-save guardado para una producción.
     *
     * @param produccion La producción de la cual recuperar el auto-save.
     * @return Un mapa con los datos guardados, o un mapa vacío si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> recuperarUltimoAutoSave(ProduccionModel produccion) {
        log.info("Recuperando último auto-save para la producción: {}", produccion.getCodigoProduccion());
        return autoSaveRepository.findByProduccion(produccion)
                .map(autoSave -> {
                    log.debug("Auto-save encontrado con timestamp: {}", autoSave.getTimestamp());
                    return autoSave.getDatos();
                })
                .orElseGet(() -> {
                    log.info("No se encontró ningún auto-save previo para la producción: {}", produccion.getCodigoProduccion());
                    return new HashMap<>();
                });
    }

    /**
     * Obtiene la fecha y hora del último auto-save realizado para una producción.
     *
     * @param produccion La producción consultada.
     * @return LocalDateTime del último guardado, o null si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public LocalDateTime obtenerUltimoAutoSaveTimestamp(ProduccionModel produccion) {
        log.debug("Consultando timestamp del último auto-save para la producción: {}", produccion.getCodigoProduccion());
        return autoSaveRepository.findByProduccion(produccion)
                .map(AutoSaveProduccionModel::getTimestamp)
                .orElse(null);
    }
}
