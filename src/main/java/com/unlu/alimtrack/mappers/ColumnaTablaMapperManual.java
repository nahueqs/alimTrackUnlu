package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.models.ColumnaTablaModel;
import com.unlu.alimtrack.models.TablaModel;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ColumnaTablaMapperManual {

    public ColumnaTablaResponseDTO toResponseDTO(ColumnaTablaModel model, TablaModel parentTabla) {
        if (model == null) {
            return null;
        }

        String tipoDato = model.getTipoDato() != null ? model.getTipoDato().getValue() : null;

        return new ColumnaTablaResponseDTO(
                model.getId(),
                parentTabla.getId(), // Usamos el ID del padre explícito
                model.getNombre(),
                model.getOrden(),
                tipoDato
        );
    }

    public List<ColumnaTablaResponseDTO> toResponseDTOList(Set<ColumnaTablaModel> models, TablaModel parentTabla) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return models.stream()
                .sorted(Comparator.comparingInt(col -> col.getOrden() != null ? col.getOrden() : 0))
                .map(col -> toResponseDTO(col, parentTabla))
                .collect(Collectors.toList());
    }
}
