package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestasProduccionPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.mappers.PublicMapper;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.ProduccionQueryService;
import com.unlu.alimtrack.services.PublicRequestsService;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicRequestsServiceImpl implements PublicRequestsService {

    private final ProduccionQueryService produccionQueryService;
    private final ProduccionManagementService produccionManagementService;
    private final VersionRecetaEstructuraService versionRecetaEstructuraService;
    private final PublicMapper publicMapper;


    @Override
    public List<MetadataProduccionPublicaResponseDTO> getAllProduccionesMetadataPublico() {
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
    public EstadoProduccionPublicoResponseDTO getProduccionPublic(String codigoProduccion) {
        return produccionQueryService.getEstadoProduccion(codigoProduccion);
    }

    @Override
    public VersionEstructuraPublicResponseDTO getEstructuraProduccion(String codigoProduccion) {

        ProduccionMetadataResponseDTO produccion = produccionQueryService.findByCodigoProduccion(codigoProduccion);

        return versionRecetaEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(produccion.codigoVersion());
    }
}
