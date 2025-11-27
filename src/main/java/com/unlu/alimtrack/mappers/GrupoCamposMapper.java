package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.GrupoCamposCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.GrupoCamposResponseDTO;
import com.unlu.alimtrack.models.GrupoCamposModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper para convertir entre GrupoCamposModel y sus DTOs.
 * Gestiona automáticamente el ordenamiento de campos por su propiedad 'orden'.
 */
@Mapper(componentModel = "spring", uses = {CampoSimpleMapper.class})
public interface GrupoCamposMapper {

    /**
     * Convierte un DTO de creación a modelo de entidad.
     * La relación 'seccion' debe ser asignada manualmente en el servicio.
     * Los campos del grupo (si los tiene el DTO) deben procesarse en el servicio.
     *
     * @param dto DTO con los datos para crear el grupo
     * @return Modelo de grupo de campos (sin seccion ni campos asignados)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seccion", ignore = true) // Se asigna en el servicio
    @Mapping(target = "campos", ignore = true)
    // Se procesan en el servicio
    GrupoCamposModel toModel(GrupoCamposCreateDTO dto);

    /**
     * Convierte el modelo de grupo de campos a DTO de respuesta.
     * Los campos se ordenan automáticamente por su propiedad 'orden'.
     *
     * @param entity Modelo de grupo de campos
     * @return DTO de respuesta con campos ordenados
     */
    @Mapping(target = "idSeccion", source = "seccion.id")
    @Mapping(target = "campos", source = "campos")
    GrupoCamposResponseDTO toResponseDTO(GrupoCamposModel entity);


    /**
     * Convierte una lista de modelos de grupos a DTOs de respuesta.
     *
     * @param gruposCampos Lista de modelos de grupos
     * @return Lista de DTOs de respuesta
     */
    List<GrupoCamposResponseDTO> toResponseDTOList(List<GrupoCamposModel> gruposCampos);
}