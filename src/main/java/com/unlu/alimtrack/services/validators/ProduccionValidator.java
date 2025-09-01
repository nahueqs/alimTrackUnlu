package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.VersionRecetaRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Validador para parámetros de búsqueda de producciones

@Component
@RequiredArgsConstructor
public class ProduccionValidator {

  private final VersionRecetaRespository versionRecetaRespository;
  private final ProduccionRepository produccionRepository;

  /**
   * Valida la existencia de referencias
   */
  public void validateReferencias(String codigoVersionReceta, String lote, String encargado) {
    if (codigoVersionReceta != null) {
      validateVersionReceta(codigoVersionReceta);
    }
    if (lote != null) {
      validateLote(lote);
    }
    if (encargado != null) {
      validateEncargado(encargado);
    }
  }

  private void validateVersionReceta(String codigoVersion) {
    if (!versionRecetaRespository.existsByCodigoVersionReceta(codigoVersion)) {
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
}