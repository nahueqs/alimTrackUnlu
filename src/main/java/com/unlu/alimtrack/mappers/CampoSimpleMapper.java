package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.CampoSimpleCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.CampoSimpleResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.CampoSimpleModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CampoSimpleMapper {

    /**
     * Convierte un modelo de campo simple a DTO de respuesta.
     *
     * @param model Modelo de campo simple
     * @return DTO de respuesta con los datos del campo
     */
    @Mapping(target = "idSeccion", source = "seccion.id")
    @Mapping(target = "idGrupo", source = "grupo.id")
    @Mapping(target = "tipoDato", source = "tipoDato", qualifiedByName = "tipoDatoToString")
    CampoSimpleResponseDTO toResponseDTO(CampoSimpleModel model);

    /**
     * Convierte un DTO de creación a modelo de entidad.
     * El tipoDato se convierte de String a enum usando el método convertirTipoDato.
     * Las relaciones seccion y grupo deben ser asignadas manualmente en el servicio.
     *
     * @param dto DTO con los datos para crear el campo
     * @return Modelo de campo simple (sin relaciones seccion/grupo asignadas)
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "seccion", ignore = true), // Se asigna en el servicio
            @Mapping(target = "grupo", ignore = true),
            @Mapping(target = "tipoDato", source = "tipoDato", qualifiedByName = "stringToTipoDato")
    })
    CampoSimpleModel toModel(CampoSimpleCreateDTO dto);


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


}
