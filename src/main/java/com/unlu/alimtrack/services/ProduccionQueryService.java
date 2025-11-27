package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionEstadoPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.ProduccionMetadataResponseDTO;

import java.util.List;

public interface ProduccionQueryService {

    ProduccionMetadataResponseDTO findByCodigoProduccion(String codigo);

    List<ProduccionMetadataResponseDTO> getAllProduccionesMetadata(ProduccionFilterRequestDTO filtros);

    ProduccionEstadoPublicaResponseDTO getProduccionPublic(String codigoProduccion);

    boolean existsByVersionRecetaPadre(String codigoReceta);

    boolean existsByCodigoProduccion(String codigoProduccion);
}
