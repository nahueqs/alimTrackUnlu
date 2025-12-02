package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.models.VersionRecetaModel;

import java.util.List;

public interface VersionRecetaMetadataService {
    List<VersionMetadataResponseDTO> findAllVersiones();

    VersionMetadataResponseDTO findByCodigoVersion(String codigoVersion);

    List<VersionMetadataResponseDTO> findAllByCodigoReceta(String codigoRecetaPadre);

    VersionMetadataResponseDTO saveVersionReceta(String codigoRecetaPadre, VersionRecetaCreateDTO versionRecetaCreateDTO);

    VersionMetadataResponseDTO updateVersionReceta(String codigoVersion, VersionRecetaModifyDTO modificacion);

    void deleteVersionReceta(String codigoVersion);

    VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta);
}
