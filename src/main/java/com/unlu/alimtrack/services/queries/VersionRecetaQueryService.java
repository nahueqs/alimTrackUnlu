package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.DTOS.response.VersionRecetaResponseDTO;

import java.util.List;

public interface VersionRecetaQueryService {

    boolean existsByCreadaPorUsername(String username);

    List<VersionRecetaResponseDTO> findAllByCreadoPorUsername(String username);

    boolean existsByRecetaPadre(String codigoRecetaPadre);

    boolean existsByCodigoVersion(String codigoVersion);

    boolean versionTieneProducciones(String codigoVersion);
}



