package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;

import java.util.List;

public interface VersionRecetaQueryService {

    List<VersionMetadataResponseDTO> findAllByCreadoPorEmail(String username);

    boolean existsByRecetaPadre(String codigoRecetaPadre);

    boolean existsByCodigoVersion(String codigoVersion);

    boolean existsByCreadaPorEmail(String email);

}



