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
import com.unlu.alimtrack.services.VersionRecetaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para manejar solicitudes públicas.
 * Proporciona acceso a información de producciones y recetas con un nivel de detalle
 * adecuado para usuarios no autenticados o vistas públicas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublicRequestsServiceImpl implements PublicRequestsService {

    private final ProduccionQueryService produccionQueryService;
    private final ProduccionManagementService produccionManagementService;
    private final VersionRecetaService versionRecetaService;
    private final PublicMapper publicMapper;

    /**
     * Obtiene la metadata de todas las producciones disponibles para el público.
     *
     * @return Lista de DTOs con metadata pública de las producciones.
     */
    @Override
    public List<MetadataProduccionPublicaResponseDTO> getAllProduccionesMetadataPublico() {
        log.info("Obteniendo metadata pública de todas las producciones.");
        ProduccionFilterRequestDTO filtros = new ProduccionFilterRequestDTO(null, null, null, null, null, null);
        
        List<MetadataProduccionPublicaResponseDTO> result = produccionQueryService.getAllProduccionesMetadata(filtros).stream()
                .map(publicMapper::metadataProduccionToPublicDTO)
                .collect(Collectors.toList());
        
        log.debug("Retornando {} registros de metadata pública.", result.size());
        return result;
    }

    /**
     * Obtiene el estado actual (respuestas) de una producción para vista pública.
     *
     * @param codigoProduccion Código de la producción.
     * @return DTO con las respuestas públicas de la producción.
     */
    @Override
    public RespuestasProduccionPublicResponseDTO getEstadoActualProduccionPublico(String codigoProduccion) {
        log.info("Obteniendo estado actual público para la producción: {}", codigoProduccion);
        UltimasRespuestasProduccionResponseDTO respuestas = produccionManagementService.getUltimasRespuestas(codigoProduccion);
        
        log.debug("Mapeando respuestas a formato público para {}", codigoProduccion);
        return publicMapper.respuestasToPublicDTO(respuestas);
    }

    /**
     * Obtiene el estado general de una producción (información básica) para vista pública.
     *
     * @param codigoProduccion Código de la producción.
     * @return DTO con el estado público de la producción.
     */
    @Override
    public EstadoProduccionPublicoResponseDTO getProduccionPublic(String codigoProduccion) {
        log.info("Obteniendo información pública general para la producción: {}", codigoProduccion);
        return produccionQueryService.getEstadoProduccion(codigoProduccion);
    }

    /**
     * Obtiene la estructura de la versión de receta asociada a una producción para vista pública.
     *
     * @param codigoProduccion Código de la producción.
     * @return DTO con la estructura pública de la versión de receta.
     */
    @Override
    public VersionEstructuraPublicResponseDTO getEstructuraProduccion(String codigoProduccion) {
        log.info("Obteniendo estructura pública de la receta para la producción: {}", codigoProduccion);
        
        ProduccionMetadataResponseDTO produccion = produccionQueryService.findByCodigoProduccion(codigoProduccion);
        log.debug("Producción {} utiliza versión de receta: {}", codigoProduccion, produccion.codigoVersion());
        
        return versionRecetaService.getVersionRecetaCompletaResponseDTOByCodigo(produccion.codigoVersion());
    }
}
