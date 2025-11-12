package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;

import java.util.List;

public interface VersionRecetaQueryService {

    boolean existsByCreadaPorUsername(String username);

    List<VersionRecetaMetadataResponseDTO> findAllByCreadoPorUsername(String username);

    boolean existsByRecetaPadre(String codigoRecetaPadre);

    boolean existsByCodigoVersion(String codigoVersion);

    boolean versionTieneProducciones(String codigoVersion);
}



