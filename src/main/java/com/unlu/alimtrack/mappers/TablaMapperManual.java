package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.TablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.FilaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.TablaResponseDTO;
import com.unlu.alimtrack.models.TablaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TablaMapperManual {

    @Autowired
    private ColumnaTablaMapperManual columnaMapperManual;

    @Autowired
    private FilaTablaMapperManual filaMapperManual;

    public TablaResponseDTO toResponseDTO(TablaModel tabla) {
        if (tabla == null) {
            return null;
        }

        Long idSeccion = tabla.getSeccion() != null ? tabla.getSeccion().getId() : null;

        // Pasamos la instancia de la tabla actual a los mappers de las colecciones
        List<ColumnaTablaResponseDTO> columnasDTO = columnaMapperManual.toResponseDTOList(tabla.getColumnas(), tabla);
        List<FilaTablaResponseDTO> filasDTO = filaMapperManual.toResponseDTOList(tabla.getFilas(), tabla);

        return new TablaResponseDTO(
                tabla.getId(),
                idSeccion,
                tabla.getNombre(),
                tabla.getDescripcion(),
                tabla.getOrden(),
                columnasDTO,
                filasDTO
        );
    }

    public TablaModel toModel(TablaCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        TablaModel tabla = new TablaModel();
        tabla.setNombre(dto.nombre());
        tabla.setDescripcion(dto.descripcion());
        tabla.setOrden(dto.orden());

        return tabla;
    }

    public List<TablaResponseDTO> toResponseDTOList(Set<TablaModel> tablas) {
        if (tablas == null || tablas.isEmpty()) {
            return List.of();
        }

        return tablas.stream()
                .sorted(Comparator.comparingInt(tabla -> tabla.getOrden() != null ? tabla.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TablaModel> toModelList(List<TablaCreateDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }

        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}
