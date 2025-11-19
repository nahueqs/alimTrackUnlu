package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.models.ColumnaTablaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ColumnaTablaMapperManual {

    private static final Logger log = LoggerFactory.getLogger(ColumnaTablaMapperManual.class);

    public ColumnaTablaResponseDTO toResponseDTO(ColumnaTablaModel model) {
        log.debug("🔧 ColumnaTablaMapperManual - Procesando columna ID: {}", model != null ? model.getId() : "null");

        if (model == null) {
            return null;
        }

        // ✅ Obtener idTabla de la relación padre
        Long idTabla = model.getTabla() != null ? model.getTabla().getId() : null;
        String tipoDato = model.getTipoDato() != null ? model.getTipoDato().getValue() : null;

        log.debug("🔧 ColumnaTablaMapperManual - Columna {}: idTabla = {}", model.getId(), idTabla);

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
            log.debug("🔧 ColumnaTablaMapperManual - No hay columnas para mapear");
            return List.of();
        }

        log.debug("🔧 ColumnaTablaMapperManual - Mapeando {} columnas", models.size());

        return models.stream()
                .sorted(Comparator.comparingInt(col -> col.getOrden() != null ? col.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}