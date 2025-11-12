package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.ColumnaTablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.ColumnaTablaModel;
import com.unlu.alimtrack.models.TablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper para convertir entre ColumnaTablaModel y sus DTOs.
 * Maneja la conversión de String a TipoDatoCampo enum para el tipo de dato.
 */
@Mapper(componentModel = "spring")
public interface ColumnaTablaMapper {

    /**
     * Convierte un DTO de creación a modelo de entidad.
     * La relación 'tabla' debe ser asignada manualmente en el servicio.
     *
     * @param dto DTO con los datos para crear la columna
     * @return Modelo de columna (sin tabla asignada)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tabla", ignore = true) // Se asigna en el servicio
    @Mapping(target = "tipoDato", source = "tipoDato", qualifiedByName = "stringToTipoDato")
    ColumnaTablaModel toModel(ColumnaTablaCreateDTO dto);

    /**
     * Convierte el modelo de columna a DTO de respuesta.
     *
     * @param model Modelo de columna
     * @return DTO de respuesta
     */
    @Mapping(target = "idTabla", source = "tabla.id")
    @Mapping(target = "tipoDato", source = "tipoDato", qualifiedByName = "tipoDatoToString")
    ColumnaTablaResponseDTO toResponseDTO(ColumnaTablaModel model);

    /**
     * Convierte una lista de modelos de columnas a DTOs de respuesta.
     *
     * @param columnas Lista de modelos de columnas
     * @return Lista de DTOs de respuesta
     */
    List<ColumnaTablaResponseDTO> toResponseDTOList(List<ColumnaTablaModel> columnas);

    /**
     * Convierte un String a TipoDatoCampo enum.
     * Utiliza el método fromString del enum que maneja la conversión case-insensitive.
     *
     * @param tipoDatoStr String con el valor del tipo de dato (ej: "texto", "entero", "decimal")
     * @return Enum TipoDatoCampo correspondiente
     * @throws IllegalArgumentException si el valor no es válido
     */
    @Named("stringToTipoDato")
    default TipoDatoCampo convertirStringATipoDato(String tipoDatoStr) {
        if (tipoDatoStr == null || tipoDatoStr.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de dato no puede ser nulo o vacío");
        }
        return TipoDatoCampo.fromString(tipoDatoStr);
    }

    /**
     * Convierte un TipoDatoCampo enum a String.
     * Utiliza el método getValue() del enum.
     *
     * @param tipoDato Enum TipoDatoCampo
     * @return String con el valor (ej: "texto", "entero", "decimal")
     */
    @Named("tipoDatoToString")
    default String convertirTipoDatoAString(TipoDatoCampo tipoDato) {
        return tipoDato != null ? tipoDato.getValue() : null;
    }

    /**
     * Crea una referencia de TablaModel con solo el ID.
     * <p>
     * ⚠️ IMPORTANTE: Este método es SOLO para entidades EXISTENTES en la BD.
     * No usar para crear nuevas tablas.
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