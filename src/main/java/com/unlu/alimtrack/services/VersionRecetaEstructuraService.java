package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.VersionReceta.SeccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaCompletaResponseDTO;
import com.unlu.alimtrack.mappers.VersionRecetaMetadataMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.SeccionRepository;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VersionRecetaEstructuraService {

    VersionRecetaRepository versionRecetaRepository;
    SeccionRepository seccionRepository;
    SeccionService seccionService;
    VersionRecetaMetadataMapper versionRecetaMetadataMapper;


    public VersionRecetaCompletaResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion) {
        VersionRecetaModel versionReceta = versionRecetaRepository.findByCodigoVersionReceta(codigoVersion);
        List<SeccionResponseDTO> secciones = seccionService.obtenerSeccionesDTOCompletasPorVersion(codigoVersion);

        return new VersionRecetaCompletaResponseDTO(
                true,
                versionReceta.getCodigoVersionReceta(),
                versionRecetaMetadataMapper.toVersionRecetaResponseDTO(versionReceta),
                secciones
        );
    }

}
