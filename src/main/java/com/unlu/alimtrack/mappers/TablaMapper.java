package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.TablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.FilaTablaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.TablaResponseDTO;
import com.unlu.alimtrack.models.TablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre TablaModel y sus DTOs.
 * Gestiona automáticamente el ordenamiento de columnas y filas por su propiedad 'orden'.
 */
@Mapper(componentModel = "spring", uses = {ColumnaTablaMapper.class, FilaTablaMapper.class})
public interface TablaMapper {

    /**
     * Convierte un DTO de creación a modelo de entidad.
     * La relación 'seccion' debe ser asignada manualmente en el servicio.
     * Las columnas y filas deben procesarse en el servicio.
     *
     * @param dto DTO con los datos para crear la tabla
     * @return Modelo de tabla (sin seccion, columnas ni filas asignadas)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seccion", ignore = true)   // Se asigna en el servicio
    @Mapping(target = "columnas", ignore = true)  // Se procesan en el servicio
    @Mapping(target = "filas", ignore = true)
    // Se procesan en el servicio
    TablaModel toModel(TablaCreateDTO dto);


    /**
     * ✅ CORREGIDO: Método manual que fuerza el uso de mappers manuales
     */
    default TablaResponseDTO toResponseDTO(TablaModel model) {
        System.out.println("🎯🎯🎯 TablaMapper.toResponseDTO EJECUTADO - Tabla " + model.getId());

        if (model == null) {
            return null;
        }

        Long idSeccion = model.getSeccion() != null ? model.getSeccion().getIdSeccion() : null;

        // ✅ FORZAR uso de mappers manuales
        ColumnaTablaMapper columnaMapper = new ColumnaTablaMapperImpl();
        FilaTablaMapper filaMapper = new FilaTablaMapperImpl();

        List<ColumnaTablaResponseDTO> columnasDTO;
        columnasDTO = model.getColumnas() != null ?
                model.getColumnas().stream()
                        .sorted(Comparator.comparingInt(col -> col.getOrden() != null ? col.getOrden() : 0))
                        .map(columnaMapper::toResponseDTO)  // ✅ Usar método manual
                        .collect(Collectors.toList()) : List.of();

        List<FilaTablaResponseDTO> filasDTO = model.getFilas() != null ?
                model.getFilas().stream()
                        .sorted(Comparator.comparingInt(fila -> fila.getOrden() != null ? fila.getOrden() : 0))
                        .map(filaMapper::mapFilaManual)  // ✅ Usar método manual
                        .collect(Collectors.toList()) : List.of();

        return new TablaResponseDTO(
                model.getId(),
                idSeccion,
                model.getNombre(),
                model.getDescripcion(),
                model.getOrden(),
                columnasDTO,
                filasDTO
        );
    }

    /**
     * ✅ CORREGIDO: Método manual para lista
     */
    default List<TablaResponseDTO> toResponseDTOList(List<TablaModel> models) {
        System.out.println("🎯🎯🎯 TablaMapper.toResponseDTOList EJECUTADO - " + (models != null ? models.size() : 0) + " tablas");

        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return models.stream()
                .sorted(Comparator.comparingInt(tabla -> tabla.getOrden() != null ? tabla.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
}