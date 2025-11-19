package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.VersionReceta.SeccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaCompletaResponseDTO;
import com.unlu.alimtrack.mappers.VersionRecetaMetadataMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VersionRecetaEstructuraService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final SeccionService seccionService;
    private final VersionRecetaMetadataMapper versionRecetaMetadataMapper;


    public VersionRecetaCompletaResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion) {
        log.debug("Obteniendo todas las estructura para la versión de receta con código: {}", codigoVersion);
        VersionRecetaModel versionReceta = versionRecetaRepository.findByCodigoVersionReceta(codigoVersion);
        log.debug("Version encontrada");
        List<SeccionResponseDTO> secciones = seccionService.obtenerSeccionesDTOCompletasPorVersion(codigoVersion);
        Integer cantCampos = seccionService.getCantidadCampos(secciones);
        Integer cantTablas = seccionService.getCantidadTablas(secciones);
        Integer cantCeldasTablas = seccionService.getCantidadCeldasTablas(secciones);

        return new VersionRecetaCompletaResponseDTO(
                true,
                versionReceta.getCodigoVersionReceta(),
                versionRecetaMetadataMapper.toVersionRecetaResponseDTO(versionReceta),
                secciones,
                secciones.size(),
                cantCampos,
                cantTablas,
                cantCeldasTablas

        );
    }


}
