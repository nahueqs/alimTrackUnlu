package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.FilaTablaResponseDTO;
import com.unlu.alimtrack.models.FilaTablaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilaTablaMapperManual {

    private static final Logger log = LoggerFactory.getLogger(FilaTablaMapperManual.class);

    public FilaTablaResponseDTO toResponseDTO(FilaTablaModel model) {
        log.debug("🔧 FilaTablaMapperManual - Procesando fila ID: {}", model != null ? model.getId() : "null");

        if (model == null) {
            return null;
        }

        // ✅ Obtener idTabla de la relación padre
        Long idTabla = model.getTabla() != null ? model.getTabla().getId() : null;

        log.debug("🔧 FilaTablaMapperManual - Fila {}: idTabla = {}", model.getId(), idTabla);

        return new FilaTablaResponseDTO(
                model.getId(),
                idTabla,  // ✅ Esto ya NO será null
                model.getNombre(),
                model.getOrden()
        );
    }

    public List<FilaTablaResponseDTO> toResponseDTOList(List<FilaTablaModel> models) {
        if (models == null || models.isEmpty()) {
            log.debug("🔧 FilaTablaMapperManual - No hay filas para mapear");
            return List.of();
        }

        log.debug("🔧 FilaTablaMapperManual - Mapeando {} filas", models.size());

        return models.stream()
                .sorted(Comparator.comparingInt(fila -> fila.getOrden() != null ? fila.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}