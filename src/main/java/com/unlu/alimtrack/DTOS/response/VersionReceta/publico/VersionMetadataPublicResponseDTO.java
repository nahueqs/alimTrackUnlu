package com.unlu.alimtrack.DTOS.response.VersionReceta.publico;

import java.time.LocalDateTime;

public record VersionMetadataPublicResponseDTO(

        String codigoVersionReceta,

        String codigoRecetaPadre,

        String nombreRecetaPadre,

        String nombre,

        String descripcion,

        LocalDateTime fechaCreacion
) {
}
