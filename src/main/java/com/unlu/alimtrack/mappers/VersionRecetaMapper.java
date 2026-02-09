package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionMetadataPublicResponseDTO;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SeccionMapperManual.class})
public interface VersionRecetaMapper {


    // --- Mapeo para Metadatos Públicos ---

    @Mapping(target = "codigoRecetaPadre", source = "recetaPadre.codigoReceta")
    @Mapping(target = "nombreRecetaPadre", source = "recetaPadre.nombre")
    VersionMetadataPublicResponseDTO toVersionMetadataPublicResponseDTO(VersionRecetaModel version);

    List<VersionMetadataPublicResponseDTO> toVersionMetadataPublicResponseDTOList(List<VersionRecetaModel> versions);

    // --- Mapeo para Metadatos Protegidos ---

    @Mapping(target = "codigoRecetaPadre", source = "recetaPadre.codigoReceta")
    @Mapping(target = "nombreRecetaPadre", source = "recetaPadre.nombre")
    @Mapping(target = "creadaPor", source = "creadoPor.email")
    VersionMetadataResponseDTO toMetadataResponseDTO(VersionRecetaModel version);


    List<VersionMetadataResponseDTO> toMetadataResponseDTOList(List<VersionRecetaModel> versions);


    // --- Mapeo para Creación y Actualización ---

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recetaPadre", ignore = true) // Se asigna en el servicio
    @Mapping(target = "creadoPor", ignore = true) // Se asigna en el servicio
    @Mapping(target = "secciones", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    VersionRecetaModel toModel(VersionRecetaCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigoVersionReceta", ignore = true)
    @Mapping(target = "recetaPadre", ignore = true)
    @Mapping(target = "secciones", ignore = true)
    @Mapping(target = "creadoPor", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateModelFromModifyDTO(VersionRecetaModifyDTO dto, @MappingTarget VersionRecetaModel model);

}
