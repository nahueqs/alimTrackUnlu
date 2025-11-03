package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.DTOS.response.RecetaResponseDTO;

import java.util.List;

public interface RecetaQueryService {

    boolean recetaTieneVersiones(String codigoReceta);

    boolean existsByCreadoPorUsername(String username);

    boolean recetaPerteneceAUsuario(String codigoReceta, String username);

    List<RecetaResponseDTO> findAllByCreadoPorUsername(String username);
}
