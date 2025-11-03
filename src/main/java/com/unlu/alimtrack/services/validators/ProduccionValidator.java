package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Validador para parámetros de búsqueda de producciones

@Component
@RequiredArgsConstructor
public class ProduccionValidator {

    private final VersionRecetaRepository versionRecetaRepository;
    private final ProduccionRepository produccionRepository;

    /**
     * Valida la existencia de referencias
     */
    public void validarReferencias(ProduccionFilterRequestDTO filtros) {
        if (filtros.codigoVersionReceta() != null) {
            validateVersionReceta(filtros.codigoVersionReceta());
        }
        if (filtros.lote() != null) {
            validateLote(filtros.lote());
        }
        if (filtros.encargado() != null) {
            validateEncargado(filtros.encargado());
        }
    }


    private void validateVersionReceta(String codigoVersion) {
        if (!versionRecetaRepository.existsByCodigoVersionReceta(codigoVersion)) {
            throw new RecursoNoEncontradoException(codigoVersion);
        }
    }

    private void validateLote(String lote) {
        if (!produccionRepository.existsByLote(lote)) {
            throw new RecursoNoEncontradoException(lote);
        }
    }

    private void validateEncargado(String encargado) {
        if (!produccionRepository.existsByEncargadoIgnoreCase(encargado)) {
            throw new RecursoNoEncontradoException(encargado);
        }
    }

    public LocalDateTime convertToStartOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    public LocalDateTime convertToEndOfDay(LocalDate date) {
        return date != null ? date.atTime(23, 59, 59) : null;
    }
}