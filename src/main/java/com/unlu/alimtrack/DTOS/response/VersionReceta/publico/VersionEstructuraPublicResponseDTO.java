package com.unlu.alimtrack.DTOS.response.VersionReceta.publico;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.SeccionResponseDTO;

import java.util.List;

public record VersionEstructuraPublicResponseDTO(

        VersionMetadataPublicResponseDTO metadata,

        List<SeccionResponseDTO> estructura,

        Integer totalCampos,

        Integer totalCeldas

) {
}
