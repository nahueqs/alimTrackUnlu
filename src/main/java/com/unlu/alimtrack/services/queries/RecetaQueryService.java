package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.models.RecetaModel;

import java.util.List;

public interface RecetaQueryService {
    boolean existeRecetaPorUsuario(String username);
    long contarRecetasPorUsuario(String username);
    boolean recetaPerteneceAUsuario(Long recetaId, String username);
    List<RecetaModel> findAllByCreadoPorUsername(String username);
    List<RecetaResponseDTO> findRecetasResponseByUsuario(String username);
}
