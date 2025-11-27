package com.unlu.alimtrack.services;

import com.unlu.alimtrack.models.ProduccionModel;

import java.time.LocalDateTime;
import java.util.Map;

public interface AutoSaveService {
    void ejecutarAutoSaveInmediato(Long idProduccion);

    void guardarAutoSave(Long idProduccion);

    Map<String, Object> recuperarUltimoAutoSave(ProduccionModel produccion);

    LocalDateTime obtenerUltimoAutoSaveTimestamp(ProduccionModel produccion);

    boolean existeAutoSave(ProduccionModel produccion);
}
