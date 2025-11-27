package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionEstadoPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionMetadataPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.RespuestasProduccionPublicResponseDTO;
import com.unlu.alimtrack.mappers.PublicMapper;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.ProduccionQueryService;
import com.unlu.alimtrack.services.PublicRequestsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicRequestsServiceImpl implements PublicRequestsService {

    private final ProduccionQueryService produccionQueryService;
    private final ProduccionManagementService produccionManagementService;
    private final PublicMapper publicMapper;

    @Override
    public List<ProduccionMetadataPublicaResponseDTO> getAllProduccionesMetadataPublico() {
        ProduccionFilterRequestDTO filtros = new ProduccionFilterRequestDTO(null, null, null, null, null, null);
        return produccionQueryService.getAllProduccionesMetadata(filtros).stream()
                .map(publicMapper::metadataProduccionToPublicDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RespuestasProduccionPublicResponseDTO getEstadoActualProduccionPublico(String codigoProduccion) {

        UltimasRespuestasProduccionResponseDTO respuestas = produccionManagementService.getUltimasRespuestas(codigoProduccion);


        return publicMapper.respuestasToPublicDTO(respuestas);
    }

    @Override
    public ProduccionEstadoPublicaResponseDTO getProduccionPublic(String codigoProduccion) {
        return produccionQueryService.getProduccionPublic(codigoProduccion);
    }
}
