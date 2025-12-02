package com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura;

public record ColumnaTablaResponseDTO(

        Long id,

        Long idTabla,

        String nombre,

        Integer orden,

        String tipoDato
) {
}
