package com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura;

import java.util.List;

public record TablaResponseDTO(

        Long id,

        Long idSeccion,

        String nombre,

        String descripcion,

        Integer orden,

        List<ColumnaTablaResponseDTO> columnas,

        List<FilaTablaResponseDTO> filas
) {
}
