package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaModelMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecetaQueryServiceImpl implements RecetaQueryService {

  private final RecetaRepository recetaRepository;
  private final RecetaModelMapper recetaModelMapper;

  @Override
  public boolean existsByCreadoPorUsername(String username) {
    return recetaRepository.existsByCreadoPorUsername(username);
  }

  @Override
  public boolean recetaPerteneceAUsuario(String codigoReceta, String username) {
    return recetaRepository.existsByCodigoRecetaAndCreadoPorUsername(codigoReceta, username);
  }

  @Override
  public List<RecetaResponseDTO> findAllByCreadoPorUsername(String username) {
    List<RecetaModel> recetas = recetaRepository.findAllByCreadoPorUsername(username).orElseThrow(
        () -> new RecursoNoEncontradoException(
            "No existen el receta creadas por el usuario " + username)
    );
    return recetaModelMapper.recetaModelsToRecetaResponseDTOs(recetas);
  }
}
