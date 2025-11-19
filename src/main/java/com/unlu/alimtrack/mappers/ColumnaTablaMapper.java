package com.unlu.alimtrack.mappers;


import com.unlu.alimtrack.DTOS.create.secciones.ColumnaTablaCreateDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.ColumnaTablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ColumnaTablaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tabla", ignore = true)
    @Mapping(target = "tipoDato", source = "tipoDato", qualifiedByName = "stringToTipoDato")
    ColumnaTablaModel toModel(ColumnaTablaCreateDTO dto);


    @Named("stringToTipoDato")
    default TipoDatoCampo stringToTipoDato(String tipoDato) {
        return TipoDatoCampo.fromString(tipoDato);
    }
}
