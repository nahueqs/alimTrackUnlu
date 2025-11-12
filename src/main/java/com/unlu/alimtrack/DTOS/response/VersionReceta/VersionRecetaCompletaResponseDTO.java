package com.unlu.alimtrack.DTOS.response.VersionReceta;

import java.util.List;

public record VersionRecetaCompletaResponseDTO(

        Boolean success,
        String message,
        VersionRecetaMetadataResponseDTO versionRecetaMetadata,
        List<SeccionResponseDTO> estructura

) {


}
