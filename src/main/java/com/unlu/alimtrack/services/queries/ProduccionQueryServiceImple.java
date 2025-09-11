package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.repositories.ProduccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProduccionQueryServiceImple implements ProduccionQueryService {

  private final ProduccionRepository produccionRepository;

  @Override
  public boolean existsByVersionRecetaPadre(String codigoReceta) {
    return produccionRepository.existsByVersionReceta_CodigoVersionReceta(codigoReceta);
  }
}
