package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;

import java.util.List;

public interface ProduccionQueryService {

    ProduccionMetadataResponseDTO findByCodigoProduccion(String codigo);

    List<ProduccionMetadataResponseDTO> getAllProduccionesMetadata(ProduccionFilterRequestDTO filtros);

    EstadoProduccionPublicoResponseDTO getEstadoProduccion(String codigoProduccion);

    boolean existsByVersionRecetaPadre(String codigoReceta);

    boolean existsByCodigoProduccion(String codigoProduccion);

}
