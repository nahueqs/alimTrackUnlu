package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.RecetaMetadataResponseDTO;

import java.util.List;

public interface RecetaService {
    List<RecetaMetadataResponseDTO> findAllRecetas();

    RecetaMetadataResponseDTO findReceta(String codigoReceta);

    RecetaMetadataResponseDTO addReceta(String codigoReceta, RecetaCreateDTO receta);

    RecetaMetadataResponseDTO updateReceta(String codigoReceta, RecetaModifyDTO recetaDTO);

    void deleteReceta(String codigoReceta);

    boolean existsByCodigoReceta(String codigoReceta);

    List<RecetaMetadataResponseDTO> findAllByCreadoPorEmail(String email);
}
