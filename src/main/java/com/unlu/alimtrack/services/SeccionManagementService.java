package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.secciones.SeccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.SeccionResponseDTO;
import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;

import java.util.List;

public interface SeccionManagementService {
    SeccionModel crearSeccion(String codigoReceta, SeccionCreateDTO seccionDTO);

    List<SeccionResponseDTO> obtenerSeccionesDTOCompletasPorVersion(String codigoVersion);

    List<SeccionModel> obtenerSeccionesCompletasPorVersion(VersionRecetaModel versionReceta);

    Integer getCantidadCampos(List<SeccionResponseDTO> secciones);

    Integer getCantidadTablas(List<SeccionResponseDTO> secciones);

    Integer getCantidadCeldasTablas(List<SeccionResponseDTO> secciones);
}
