package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionMetadataPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.SeccionResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.PublicMapper;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.SeccionManagementService;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VersionRecetaEstructuraServiceImpl implements VersionRecetaEstructuraService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final VersionRecetaMetadataService versionRecetaMetadataService;
    private final SeccionManagementService seccionManagementService;
    private final VersionRecetaMapper versionRecetaMapper;
    private final PublicMapper publicMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "versionRecetaEstructura", key = "#codigoVersion")
    public VersionEstructuraPublicResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion) {
        log.info("Obteniendo estructura completa para la versión de receta: {}", codigoVersion);
        VersionMetadataPublicResponseDTO metadata = publicMapper.metadataVersionToPublicDTO(versionRecetaMetadataService.findByCodigoVersion(codigoVersion));

        // Delegamos la carga de la estructura al servicio de secciones
        List<SeccionResponseDTO> seccionesCompletas = seccionManagementService.obtenerSeccionesDTOCompletasPorVersion(codigoVersion);

        Integer totalCampos = seccionManagementService.getCantidadCampos(seccionesCompletas);
        Integer totalCeldas = seccionManagementService.getCantidadCeldasTablas(seccionesCompletas);


        log.info("Versión {} encontrada y estructura cargada. Mapeando a DTO...", codigoVersion);

        return new VersionEstructuraPublicResponseDTO(
                metadata,
                seccionesCompletas,
                totalCampos,
                totalCeldas
        );
    }
}
