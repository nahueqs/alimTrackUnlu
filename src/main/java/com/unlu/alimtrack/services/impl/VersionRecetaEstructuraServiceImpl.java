package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionMetadataPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.SeccionResponseDTO;
import com.unlu.alimtrack.mappers.PublicMapper;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.SeccionManagementService;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para obtener la estructura completa de una versión de receta.
 * Coordina la obtención de metadatos y la estructura detallada (secciones, campos, tablas).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VersionRecetaEstructuraServiceImpl implements VersionRecetaEstructuraService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final VersionRecetaMetadataService versionRecetaMetadataService;
    private final SeccionManagementService seccionManagementService;
    private final VersionRecetaMapper versionRecetaMapper;
    private final PublicMapper publicMapper;

    /**
     * Obtiene la estructura completa de una versión de receta, incluyendo metadatos y secciones detalladas.
     *
     * @param codigoVersion Código de la versión de receta.
     * @return DTO con la estructura completa de la versión.
     */
    @Override
    @Transactional(readOnly = true)
    public VersionEstructuraPublicResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion) {
        log.info("Iniciando recuperación de estructura completa para la versión de receta: {}", codigoVersion);
        
        // 1. Obtener metadatos básicos
        VersionMetadataPublicResponseDTO metadata = publicMapper.metadataVersionToPublicDTO(
                versionRecetaMetadataService.findByCodigoVersion(codigoVersion)
        );
        log.debug("Metadatos recuperados para versión: {}", codigoVersion);

        // 2. Obtener estructura detallada (secciones)
        // Delegamos la carga de la estructura al servicio de secciones
        List<SeccionResponseDTO> seccionesCompletas = seccionManagementService.obtenerSeccionesDTOCompletasPorVersion(codigoVersion);
        log.debug("Secciones recuperadas: {}", seccionesCompletas.size());

        // 3. Calcular totales para estadísticas/progreso
        Integer totalCampos = seccionManagementService.getCantidadCampos(seccionesCompletas);
        Integer totalCeldas = seccionManagementService.getCantidadCeldasTablas(seccionesCompletas);
        log.debug("Totales calculados - Campos: {}, Celdas: {}", totalCampos, totalCeldas);

        log.info("Estructura completa de versión {} construida exitosamente.", codigoVersion);

        return new VersionEstructuraPublicResponseDTO(
                metadata,
                seccionesCompletas,
                totalCampos,
                totalCeldas
        );
    }
}
