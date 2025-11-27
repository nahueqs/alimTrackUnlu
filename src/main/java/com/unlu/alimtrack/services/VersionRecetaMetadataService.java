package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;
import com.unlu.alimtrack.models.VersionRecetaModel;

import java.util.List;

public interface VersionRecetaMetadataService {
    List<VersionRecetaMetadataResponseDTO> findAllVersiones();

    VersionRecetaMetadataResponseDTO findByCodigoVersion(String codigoVersion);

    List<VersionRecetaMetadataResponseDTO> findAllByCodigoReceta(String codigoRecetaPadre);

    VersionRecetaMetadataResponseDTO saveVersionReceta(String codigoRecetaPadre, VersionRecetaCreateDTO versionRecetaCreateDTO);

    VersionRecetaMetadataResponseDTO updateVersionReceta(String codigoVersion, VersionRecetaModifyDTO modificacion);

    void deleteVersionReceta(String codigoVersion);

    VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta);
}
