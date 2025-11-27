package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaCompletaResponseDTO;

public interface VersionRecetaEstructuraService {
    VersionRecetaCompletaResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion);
}
