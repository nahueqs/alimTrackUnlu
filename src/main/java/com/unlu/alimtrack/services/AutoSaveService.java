package com.unlu.alimtrack.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.AutoSaveProduccionModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.repositories.AutoSaveRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoSaveService {

    private final AutoSaveRepository autoSaveRepository;
    private final RespuestaCampoRepository respuestaCampoRepository;
    private final ProduccionRepository produccionRepository;
    private final ObjectMapper objectMapper;

    /**
     * Ejecuta auto-save inmediato de forma ASÍNCRONA
     */
    @Async
    public void ejecutarAutoSaveInmediato(Long idProduccion) {
        try {
            log.debug("Ejecutando auto-save inmediato para producción: {}", idProduccion);
            guardarAutoSave(idProduccion);
            log.debug("Auto-save inmediato completado para ID: {}", idProduccion);
        } catch (Exception e) {
            log.error("Error en auto-save inmediato para producción: {}", idProduccion, e);
            // No propagamos la exception - el auto-save no debe afectar la operación principal
        }
    }

    /**
     * Guarda el estado actual de la producción en autosave_produccion
     */
    @Transactional
    public void guardarAutoSave(Long idProduccion) {
        ProduccionModel produccion = null;
        try {

            produccion = produccionRepository.findById(idProduccion)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producción no encontrada: " + idProduccion));

            // 1. Obtener todas las respuestas actuales de la producción
            Map<String, Object> estadoActual = obtenerEstadoActualProduccion(produccion);

            // 2. Buscar auto-save existente o crear nuevo
            AutoSaveProduccionModel autoSave = autoSaveRepository
                    .findByProduccionProduccion(produccion.getProduccion())
                    .orElse(new AutoSaveProduccionModel());

            // 3. Configurar el modelo según tu esquema
            if (autoSave.getId() == null) {
                // Nuevo registro
                autoSave.setProduccion(produccion);
            }
            autoSave.setDatos(estadoActual);
            autoSave.setTimestamp(LocalDateTime.now());

            autoSaveRepository.save(autoSave);

            log.debug("Auto-save guardado para producción id: {}", idProduccion);

        } catch (Exception e) {
            log.error("Error guardando auto-save para producción id : {}", idProduccion, e);
            throw new RuntimeException("Error en auto-save", e);
        }
    }

    /**
     * Obtiene el estado actual de la producción para serializar en JSON
     */
    private Map<String, Object> obtenerEstadoActualProduccion(ProduccionModel produccion) {
        Map<String, Object> estado = new HashMap<>();

        // 1. Información básica de la producción
        estado.put("codigo_produccion", produccion.getCodigoProduccion());
        estado.put("estado", produccion.getEstado().name());
        estado.put("timestamp_autosave", LocalDateTime.now().toString());

        // 2. Respuestas de campos
        List<RespuestaCampoModel> respuestas = respuestaCampoRepository
                .findByIdProduccion(produccion);

        Map<String, String> respuestasMap = respuestas.stream()
                .collect(Collectors.toMap(
                        respuesta -> "respuesta_id_" + respuesta.getIdCampo().getId(),
                        RespuestaCampoModel::getValor
                ));

        estado.put("respuestas_campos", respuestasMap);

        // 3. Metadatos de la producción
        estado.put("lote", produccion.getLote());
        estado.put("encargado", produccion.getEncargado());
        estado.put("observaciones", produccion.getObservaciones());

        return estado;
    }

    /**
     * Recupera el último auto-save de una producción
     */
    public Map<String, Object> recuperarUltimoAutoSave(ProduccionModel produccion) {
        return autoSaveRepository.findByProduccionProduccion(produccion.getProduccion())
                .map(AutoSaveProduccionModel::getDatos)
                .orElse(new HashMap<>());
    }

    /**
     * Obtiene el timestamp del último auto-save
     */
    public LocalDateTime obtenerUltimoAutoSaveTimestamp(ProduccionModel produccion) {
        return autoSaveRepository.findByProduccionProduccion(produccion.getProduccion())
                .map(AutoSaveProduccionModel::getTimestamp)
                .orElse(null);
    }

    /**
     * Verifica si existe auto-save para una producción
     */
    public boolean existeAutoSave(ProduccionModel produccion) {
        return autoSaveRepository.existsByProduccionProduccion(produccion.getProduccion());
    }
}