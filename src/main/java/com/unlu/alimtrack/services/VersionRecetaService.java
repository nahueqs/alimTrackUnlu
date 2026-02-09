package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.create.VersionRecetaLlenaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.models.VersionRecetaModel;

import java.util.List;

public interface VersionRecetaService {
    // Métodos de Metadata
    List<VersionMetadataResponseDTO> findAllVersiones();

    VersionMetadataResponseDTO findByCodigoVersion(String codigoVersion);

    List<VersionMetadataResponseDTO> findAllByCodigoReceta(String codigoRecetaPadre);

    VersionMetadataResponseDTO saveVersionReceta(String codigoRecetaPadre, VersionRecetaCreateDTO versionRecetaCreateDTO);

    VersionMetadataResponseDTO saveVersionRecetaCompleta(String codigoRecetaPadre, VersionRecetaLlenaCreateDTO versionRecetaLlenaCreateDTO);

    VersionMetadataResponseDTO updateVersionReceta(String codigoVersion, VersionRecetaModifyDTO modificacion);

    void deleteVersionReceta(String codigoVersion);

    VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta);

    // Métodos de Estructura (Traídos de VersionRecetaEstructuraService)
    VersionEstructuraPublicResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion);
}
