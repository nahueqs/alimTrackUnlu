package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.FilaTablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.FilaTablaResponseDTO;
import com.unlu.alimtrack.models.FilaTablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre FilaTablaModel y sus DTOs.
 */
@Mapper(componentModel = "spring")
public interface FilaTablaMapper {

    /**
     * Convierte un DTO de creación a modelo de entidad.
     * La relación 'tabla' debe ser asignada manualmente en el servicio.
     *
     * @param dto DTO con los datos para crear la fila
     * @return Modelo de fila (sin tabla asignada)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tabla", ignore = true)
    // Se asigna en el servicio
    FilaTablaModel toModel(FilaTablaCreateDTO dto);

    /**
     * Convierte el modelo de fila a DTO de respuesta.
     *
     * @param model Modelo de fila
     * @return DTO de respuesta
     */
    @Mapping(target = "idTabla", source = "tabla.id")
    FilaTablaResponseDTO toResponseDTO(FilaTablaModel model);

    /**
     * Convierte una lista de modelos de filas a DTOs de respuesta.
     *
     * @param models Lista de modelos de filas
     * @return Lista de DTOs de respuesta
     */
    default List<FilaTablaResponseDTO> toResponseDTOList(List<FilaTablaModel> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return models.stream()
                .sorted(Comparator.comparingInt(fila -> fila.getOrden() != null ? fila.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}