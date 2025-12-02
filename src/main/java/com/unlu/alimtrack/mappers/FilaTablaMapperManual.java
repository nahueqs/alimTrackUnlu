package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.FilaTablaResponseDTO;
import com.unlu.alimtrack.models.FilaTablaModel;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilaTablaMapperManual {

    public FilaTablaResponseDTO toResponseDTO(FilaTablaModel model) {
        if (model == null) {
            return null;
        }

        // ✅ Obtener idTabla de la relación padre
        Long idTabla = model.getTabla() != null ? model.getTabla().getId() : null;

        return new FilaTablaResponseDTO(
                model.getId(),
                idTabla,  // ✅ Esto ya NO será null
                model.getNombre(),
                model.getOrden()
        );
    }

    public List<FilaTablaResponseDTO> toResponseDTOList(List<FilaTablaModel> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return models.stream()
                .sorted(Comparator.comparingInt(fila -> fila.getOrden() != null ? fila.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}