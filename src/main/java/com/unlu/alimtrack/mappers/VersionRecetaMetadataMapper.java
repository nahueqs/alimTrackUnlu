package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.services.impl.UsuarioServiceImpl;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class VersionRecetaMetadataMapper {

    @Lazy
    @Autowired
    protected UsuarioServiceImpl usuarioServiceImpl;

    @Mapping(target = "creadoPor", source = "emailCreador", qualifiedByName = "emailToModel")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "recetaPadre.codigoReceta", source = "codigoRecetaPadre")
    public abstract VersionRecetaModel toVersionRecetaModel(VersionRecetaCreateDTO versionRecetaCreateDto);

    @Mapping(target = "creadaPor", source = "creadoPor.nombre")
    @Mapping(target = "nombreRecetaPadre", source = "recetaPadre.nombre")
    @Mapping(target = "codigoRecetaPadre", source = "recetaPadre.codigoReceta")
    public abstract VersionRecetaMetadataResponseDTO toVersionRecetaResponseDTO(VersionRecetaModel versionRecetaModel);

    public abstract List<VersionRecetaMetadataResponseDTO> toVersionRecetaResponseDTOList(
            List<VersionRecetaModel> versionRecetaModels);

    public abstract void updateModelFromModifyDTO(VersionRecetaModifyDTO modificacion, @MappingTarget VersionRecetaModel model);


    @Named("emailToModel")
    protected UsuarioModel usernameEmailToModel(String email) {
        return usuarioServiceImpl.getUsuarioModelByEmail(email);
    }
}
