package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.ColumnaTablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ColumnaTablaResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.ColumnaTablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ColumnaTablaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tabla", ignore = true)
    @Mapping(target = "tipoDato", source = "tipoDato", qualifiedByName = "stringToTipoDato")
    ColumnaTablaModel toModel(ColumnaTablaCreateDTO dto);

    /**
     * ✅ CORREGIDO: Método manual para asegurar que idTabla se asigne correctamente
     */
    @Named("mapColumnaManual")
    default ColumnaTablaResponseDTO toResponseDTO(ColumnaTablaModel model) {
        System.out.println("🎯🎯🎯 ColumnaTablaMapper.mapColumnaManual EJECUTADO - Columna " + model.getId());

        Long idTabla = model.getTabla() != null ? model.getTabla().getId() : null;
        String tipoDatoString = model.getTipoDato() != null ? model.getTipoDato().getValue() : null;

        System.out.println("🎯🎯🎯 Columna " + model.getId() + ": idTabla = " + idTabla);

        return new ColumnaTablaResponseDTO(
                model.getId(),
                idTabla,
                model.getNombre(),
                model.getOrden(),
                tipoDatoString
        );
    }

    /**
     * Convierte una lista de modelos de columnas a DTOs de respuesta ordenadas.
     */
    default List<ColumnaTablaResponseDTO> toResponseDTOList(List<ColumnaTablaModel> columnas) {
        if (columnas == null || columnas.isEmpty()) {
            return List.of();
        }

        return columnas.stream()
                .sorted(Comparator.comparingInt(col -> col.getOrden() != null ? col.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Named("stringToTipoDato")
    default TipoDatoCampo convertirStringATipoDato(String tipoDatoStr) {
        if (tipoDatoStr == null || tipoDatoStr.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de dato no puede ser nulo o vacío");
        }
        return TipoDatoCampo.fromString(tipoDatoStr);
    }

    @Named("tipoDatoToString")
    default String convertirTipoDatoAString(TipoDatoCampo tipoDato) {
        return tipoDato != null ? tipoDato.getValue() : null;
    }
}