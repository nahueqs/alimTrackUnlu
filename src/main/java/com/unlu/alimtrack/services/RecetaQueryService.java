package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.Receta.RecetaMetadataResponseDTO;

import java.util.List;

public interface RecetaQueryService {

    boolean recetaTieneVersiones(String codigoReceta);

    boolean existsByCreadoPorEmail(String email);
}
