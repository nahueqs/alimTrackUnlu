package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;

public interface VersionRecetaEstructuraService {
    VersionEstructuraPublicResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion);
}
