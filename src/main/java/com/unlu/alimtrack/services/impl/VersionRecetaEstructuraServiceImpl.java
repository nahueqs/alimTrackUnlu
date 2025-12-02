package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.SeccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.TablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.SeccionManagementService;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VersionRecetaEstructuraServiceImpl implements VersionRecetaEstructuraService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final SeccionManagementService seccionManagementService;
    private final VersionRecetaMapper versionRecetaMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "versionRecetaEstructura", key = "#codigoVersion")
    public VersionEstructuraPublicResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion) {
        log.info("Obteniendo estructura completa para la versión de receta: {}", codigoVersion);

        VersionRecetaModel versionReceta = versionRecetaRepository.findByCodigoVersionReceta(codigoVersion)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la versión de receta con código: " + codigoVersion));

        log.debug("Versión {} encontrada. Obteniendo estructura de secciones.", codigoVersion);
        List<SeccionResponseDTO> secciones = seccionManagementService.obtenerSeccionesDTOCompletasPorVersion(codigoVersion);
        // DEBUG TEMPORAL
        log.debug("=== DEBUG FINAL DTOs ===");
        for (SeccionResponseDTO seccion : secciones) {
            log.debug("Sección: id={}, titulo={}", seccion.id(), seccion.titulo());
            if (seccion.tablas() != null) {
                for (TablaResponseDTO tabla : seccion.tablas()) {
                    log.debug("  Tabla: id={}, nombre={}", tabla.id(), tabla.nombre());
                }
            }
        }
        log.debug("Calculando contadores de estructura para la versión {}", codigoVersion);
        Integer totalCampos = seccionManagementService.getCantidadCampos(secciones);
        Integer totalCeldas = seccionManagementService.getCantidadTablas(secciones);

        log.info("Estructura completa para la versión {} obtenida exitosamente.", codigoVersion);
        return new VersionEstructuraPublicResponseDTO(
                versionRecetaMapper.toVersionMetadataPublicResponseDTO(versionReceta),
                secciones,
                totalCampos,
                totalCeldas
        );


    }
}
