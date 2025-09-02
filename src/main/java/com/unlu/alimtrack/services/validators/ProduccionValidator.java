package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.dtos.request.ProduccionFilterRequestDTO;
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