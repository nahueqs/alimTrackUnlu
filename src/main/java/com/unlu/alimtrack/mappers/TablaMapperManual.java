package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.TablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.FilaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.TablaResponseDTO;
import com.unlu.alimtrack.models.TablaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TablaMapperManual {

    private static final Logger log = LoggerFactory.getLogger(TablaMapperManual.class);

    @Autowired
    private ColumnaTablaMapperManual columnaMapperManual;

    @Autowired
    private FilaTablaMapperManual filaMapperManual;

    // ✅ MÉTODO PARA LECTURA (Model → DTO)
    public TablaResponseDTO toResponseDTO(TablaModel tabla) {
        log.debug("🔧 TablaMapperManual - Procesando tabla ID: {}", tabla != null ? tabla.getId() : "null");

        if (tabla == null) {
            return null;
        }

        // ✅ Obtener idSeccion de la relación
        Long idSeccion = tabla.getSeccion() != null ? tabla.getSeccion().getIdSeccion() : null;

        // ✅ Usar mappers manuales para columnas y filas
        List<ColumnaTablaResponseDTO> columnasDTO = columnaMapperManual.toResponseDTOList(tabla.getColumnas());
        List<FilaTablaResponseDTO> filasDTO = filaMapperManual.toResponseDTOList(tabla.getFilas());

        log.debug("🔧 TablaMapperManual - Tabla {} mapeada: {} columnas, {} filas, idSeccion: {}",
                tabla.getId(), columnasDTO.size(), filasDTO.size(), idSeccion);

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
        log.debug("🔧 TablaMapperManual - Creando tabla desde DTO: {}", dto != null ? dto.nombre() : "null");

        if (dto == null) {
            return null;
        }

        TablaModel tabla = new TablaModel();
        tabla.setNombre(dto.nombre());
        tabla.setDescripcion(dto.descripcion());
        tabla.setOrden(dto.orden());
        // ✅ La relación 'seccion' se asigna en el servicio
        // ✅ Las 'columnas' y 'filas' se procesan en el servicio

        log.debug("🔧 TablaMapperManual - Tabla creada: '{}'", tabla.getNombre());

        return tabla;
    }

    public List<TablaResponseDTO> toResponseDTOList(List<TablaModel> tablas) {
        if (tablas == null || tablas.isEmpty()) {
            log.debug("🔧 TablaMapperManual - No hay tablas para mapear");
            return List.of();
        }

        log.debug("🔧 TablaMapperManual - Mapeando {} tablas", tablas.size());

        return tablas.stream()
                .sorted(Comparator.comparingInt(tabla -> tabla.getOrden() != null ? tabla.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TablaModel> toModelList(List<TablaCreateDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            log.debug("🔧 TablaMapperManual - No hay DTOs para crear tablas");
            return List.of();
        }

        log.debug("🔧 TablaMapperManual - Creando {} tablas desde DTOs", dtos.size());

        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}