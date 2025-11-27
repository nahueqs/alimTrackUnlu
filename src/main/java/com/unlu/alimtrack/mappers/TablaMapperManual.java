package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.TablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.FilaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.TablaResponseDTO;
import com.unlu.alimtrack.models.TablaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TablaMapperManual {

    @Autowired
    private ColumnaTablaMapperManual columnaMapperManual;

    @Autowired
    private FilaTablaMapperManual filaMapperManual;

    // ✅ MÉTODO PARA LECTURA (Model → DTO)
    public TablaResponseDTO toResponseDTO(TablaModel tabla) {
        if (tabla == null) {
            return null;
        }

        // ✅ Obtener id de la relación
        Long idSeccion = tabla.getSeccion() != null ? tabla.getSeccion().getId() : null;

        // ✅ Usar mappers manuales para columnas y filas
        List<ColumnaTablaResponseDTO> columnasDTO = columnaMapperManual.toResponseDTOList(tabla.getColumnas());
        List<FilaTablaResponseDTO> filasDTO = filaMapperManual.toResponseDTOList(tabla.getFilas());

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

    // ✅ MÉTODO PARA CREACIÓN (DTO → Model) - SIMPLIFICADO
    public TablaModel toModel(TablaCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        TablaModel tabla = new TablaModel();
        tabla.setNombre(dto.nombre());
        tabla.setDescripcion(dto.descripcion());
        tabla.setOrden(dto.orden());
        // ✅ La relación 'seccion' se asigna en el servicio
        // ✅ Las 'columnas' y 'filas' se procesan en el servicio

        return tabla;
    }

    public List<TablaResponseDTO> toResponseDTOList(List<TablaModel> tablas) {
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