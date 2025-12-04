package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.FilaTablaResponseDTO;
import com.unlu.alimtrack.models.FilaTablaModel;
import com.unlu.alimtrack.models.TablaModel;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilaTablaMapperManual {

    public FilaTablaResponseDTO toResponseDTO(FilaTablaModel model, TablaModel parentTabla) {
        if (model == null) {
            return null;
        }

        return new FilaTablaResponseDTO(
                model.getId(),
                parentTabla.getId(), // Usamos el ID del padre explícito
                model.getNombre(),
                model.getOrden()
        );
    }

    public List<FilaTablaResponseDTO> toResponseDTOList(Set<FilaTablaModel> models, TablaModel parentTabla) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return models.stream()
                .sorted(Comparator.comparingInt(fila -> fila.getOrden() != null ? fila.getOrden() : 0))
                .map(fila -> toResponseDTO(fila, parentTabla))
                .collect(Collectors.toList());
    }
}
