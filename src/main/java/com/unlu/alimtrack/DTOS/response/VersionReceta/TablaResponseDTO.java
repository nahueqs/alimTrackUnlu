package com.unlu.alimtrack.DTOS.response.VersionReceta;

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
