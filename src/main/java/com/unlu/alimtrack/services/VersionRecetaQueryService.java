package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;

import java.util.List;

public interface VersionRecetaQueryService {

    List<VersionRecetaMetadataResponseDTO> findAllByCreadoPorEmail(String username);

    boolean existsByRecetaPadre(String codigoRecetaPadre);

    boolean existsByCodigoVersion(String codigoVersion);

    boolean existsByCreadaPorEmail(String email);
}



