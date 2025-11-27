package com.unlu.alimtrack.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.AutoSaveProduccionModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.repositories.AutoSaveRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoSaveServiceImpl implements AutoSaveService {

    private final AutoSaveRepository autoSaveRepository;
    private final RespuestaCampoRepository respuestaCampoRepository;
    private final ProduccionRepository produccionRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Async
    public void ejecutarAutoSaveInmediato(Long idProduccion) {
        try {
            log.info("Iniciando auto-save asíncrono para producción ID: {}", idProduccion);
            guardarAutoSave(idProduccion);
            log.info("Auto-save para producción ID: {} completado exitosamente", idProduccion);
        } catch (Exception e) {
            log.error("Error durante el auto-save asíncrono para producción ID: {}", idProduccion, e);
            // No se propaga la excepción para no afectar la operación principal del usuario.
        }
    }

    @Override
    @Transactional
    public void guardarAutoSave(Long idProduccion) {
        log.debug("Buscando producción con ID: {} para guardar su estado", idProduccion);
        ProduccionModel produccion = produccionRepository.findById(idProduccion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producción no encontrada para auto-save: " + idProduccion));

        log.debug("Obteniendo estado actual de la producción {}", produccion.getCodigoProduccion());
        Map<String, Object> estadoActual = obtenerEstadoActualProduccion(produccion);

        AutoSaveProduccionModel autoSave = autoSaveRepository
                .findByProduccion(produccion)
                .orElse(new AutoSaveProduccionModel());

        if (autoSave.getId() == null) {
            log.debug("Creando nuevo registro de auto-save para la producción {}", produccion.getCodigoProduccion());
            autoSave.setProduccion(produccion);
        } else {
            log.debug("Actualizando registro de auto-save existente para la producción {}", produccion.getCodigoProduccion());
        }

        autoSave.setDatos(estadoActual);
        autoSave.setTimestamp(LocalDateTime.now());

        autoSaveRepository.save(autoSave);
        log.debug("Auto-save para producción {} guardado en la base de datos", produccion.getCodigoProduccion());
    }

    private Map<String, Object> obtenerEstadoActualProduccion(ProduccionModel produccion) {
        Map<String, Object> estado = new HashMap<>();
        estado.put("codigo_produccion", produccion.getCodigoProduccion());
        estado.put("estado", produccion.getEstado().name());
        estado.put("timestamp_autosave", LocalDateTime.now().toString());
        estado.put("lote", produccion.getLote());
        estado.put("encargado", produccion.getEncargado());
        estado.put("observaciones", produccion.getObservaciones());

        List<RespuestaCampoModel> respuestas = respuestaCampoRepository.findByIdProduccion(produccion);
        Map<String, String> respuestasMap = respuestas.stream()
                .collect(Collectors.toMap(
                        respuesta -> "respuesta_id_" + respuesta.getIdCampo().getId(),
                        RespuestaCampoModel::getValor
                ));
        estado.put("respuestas_campos", respuestasMap);

        return estado;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> recuperarUltimoAutoSave(ProduccionModel produccion) {
        log.info("Recuperando último auto-save para la producción: {}", produccion.getCodigoProduccion());
        return autoSaveRepository.findByProduccion(produccion)
                .map(AutoSaveProduccionModel::getDatos)
                .orElse(new HashMap<>());
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime obtenerUltimoAutoSaveTimestamp(ProduccionModel produccion) {
        log.info("Obteniendo timestamp del último auto-save para la producción: {}", produccion.getCodigoProduccion());
        return autoSaveRepository.findByProduccion(produccion)
                .map(AutoSaveProduccionModel::getTimestamp)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeAutoSave(ProduccionModel produccion) {
        log.debug("Verificando si existe auto-save para la producción: {}", produccion.getCodigoProduccion());
        return autoSaveRepository.existsByProduccion(produccion);
    }
}
