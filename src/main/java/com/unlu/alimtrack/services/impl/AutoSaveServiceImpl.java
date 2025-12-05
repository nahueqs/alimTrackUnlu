package com.unlu.alimtrack.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.AutoSaveProduccionModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import com.unlu.alimtrack.repositories.AutoSaveRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
import com.unlu.alimtrack.repositories.RespuestaTablaRepository;
import com.unlu.alimtrack.services.AutoSaveService;
// Removed import com.unlu.alimtrack.services.NotificationService;
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
    private final RespuestaTablaRepository respuestaTablaRepository; // Inyectado
    private final ProduccionRepository produccionRepository;
    private final ObjectMapper objectMapper;
    // Removed private final NotificationService notificationService;

    @Override
    @Async
    public void ejecutarAutoSaveInmediato(ProduccionModel produccion) {
        try {
            log.debug("Buscando producción con ID: {} para guardar su estado", produccion);


            log.info("Iniciando auto-save asíncrono para producción codigo: {}", produccion.getCodigoProduccion());
            guardarAutoSave(produccion);
            // Removed notificationService.notifyAutoSave(produccion.getCodigoProduccion());
            log.info("Auto-save para producción codigo: {} completado exitosamente.", produccion.getCodigoProduccion());
        } catch (Exception e) {
            log.error("Error durante el auto-save asíncrono para producción codigo: {}", produccion, e);
        }
    }

    @Override
    @Transactional
    public void guardarAutoSave(ProduccionModel produccion) {

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

        // Respuestas de campos simples
        List<RespuestaCampoModel> respuestasCampos = respuestaCampoRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
        Map<String, String> respuestasCamposMap = respuestasCampos.stream()
                .collect(Collectors.toMap(
                        respuesta -> "campo_" + respuesta.getIdCampo().getId(),
                        RespuestaCampoModel::getValor
                ));
        estado.put("respuestas_campos", respuestasCamposMap);

        // Respuestas de tablas
        List<RespuestaTablaModel> respuestasTablas = respuestaTablaRepository.findAllUltimasRespuestasByProduccion(produccion.getProduccion());
        Map<String, String> respuestasTablasMap = respuestasTablas.stream()
                .collect(Collectors.toMap(
                        respuesta -> "celda_" + respuesta.getIdTabla().getId() + "_" + respuesta.getFila().getId() + "_" + respuesta.getColumna().getId(),
                        RespuestaTablaModel::getValor
                ));
        estado.put("respuestas_tablas", respuestasTablasMap);

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