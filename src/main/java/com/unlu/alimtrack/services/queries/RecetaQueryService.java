package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;

import java.util.List;

public interface RecetaQueryService {
    boolean existenRecetaPorUsuario(String username);

    boolean recetaPerteneceAUsuario(String codigoReceta, String username);

    List<RecetaResponseDTO> findAllByCreadoPorUsername(String username);
}
