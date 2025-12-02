package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.models.ColumnaTablaModel;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ColumnaTablaMapperManual {

    public ColumnaTablaResponseDTO toResponseDTO(ColumnaTablaModel model) {
        if (model == null) {
            return null;
        }

        // ✅ Obtener idTabla de la relación padre
        Long idTabla = model.getTabla() != null ? model.getTabla().getId() : null;
        String tipoDato = model.getTipoDato() != null ? model.getTipoDato().getValue() : null;

        return new ColumnaTablaResponseDTO(
                model.getId(),
                idTabla,
                model.getNombre(),
                model.getOrden(),
                tipoDato
        );
    }

    public List<ColumnaTablaResponseDTO> toResponseDTOList(List<ColumnaTablaModel> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return models.stream()
                .sorted(Comparator.comparingInt(col -> col.getOrden() != null ? col.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}