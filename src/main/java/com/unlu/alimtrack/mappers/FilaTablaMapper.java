package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.FilaTablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.FilaTablaResponseDTO;
import com.unlu.alimtrack.models.FilaTablaModel;
import com.unlu.alimtrack.models.TablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

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
     * @param filas Lista de modelos de filas
     * @return Lista de DTOs de respuesta
     */
    List<FilaTablaResponseDTO> toResponseDTOList(List<FilaTablaModel> filas);

    /**
     * Crea una referencia de TablaModel con solo el ID.
     * <p>
     * ⚠️ IMPORTANTE: Este método es SOLO para entidades EXISTENTES en la BD.
     * No usar para crear nuevas tablas.
     * <p>
     * Uso típico:
     * - Agregar filas a tablas ya existentes
     * - APIs REST que reciben IDs de entidades existentes
     *
     * @param idTabla ID de la tabla EXISTENTE en la base de datos
     * @return TablaModel proxy con solo el ID (sin cargar datos de BD)
     */
    default TablaModel tablaModelFromId(Long idTabla) {
        if (idTabla == null) {
            return null;
        }
        TablaModel tabla = new TablaModel();
        tabla.setId(idTabla);
        return tabla;
    }
}