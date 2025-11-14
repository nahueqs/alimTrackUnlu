package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.secciones.FilaTablaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.FilaTablaResponseDTO;
import com.unlu.alimtrack.models.FilaTablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FilaTablaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tabla", ignore = true)
    FilaTablaModel toModel(FilaTablaCreateDTO dto);

    /**
     * ✅ CORREGIDO: Método manual para asegurar que idTabla se asigne correctamente
     */
    default FilaTablaResponseDTO toResponseDTO(FilaTablaModel model) {
        if (model == null) {
            return null;
        }

        Long idTabla = model.getTabla() != null ? model.getTabla().getId() : null;

        System.out.println("🎯 FilaTablaMapper.toResponseDTO - Fila " + model.getId() +
                ": idTabla = " + idTabla + ", tabla objeto = " + model.getTabla());

        return new FilaTablaResponseDTO(
                model.getId(),
                idTabla,
                model.getNombre(),
                model.getOrden()
        );
    }

    @Named("mapFilaManual")
    default FilaTablaResponseDTO mapFilaManual(FilaTablaModel model) {
        System.out.println("🎯🎯🎯 FilaTablaMapper.mapFilaManual EJECUTADO - Fila " + model.getId());

        Long idTabla = model.getTabla() != null ? model.getTabla().getId() : null;

        System.out.println("🎯🎯🎯 Fila " + model.getId() + ": idTabla = " + idTabla);

        return new FilaTablaResponseDTO(
                model.getId(),
                idTabla,
                model.getNombre(),
                model.getOrden()
        );
    }

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