package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import java.util.List;

public interface VersionRecetaQueryService {

  boolean existsByCreadaPorUsername(String username);

  List<VersionRecetaResponseDTO> findAllByCreadoPorUsername(String username);

  boolean existsByRecetaPadre(String codigoRecetaPadre);

}
