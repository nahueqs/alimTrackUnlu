package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.services.impl.UsuarioServiceImpl;
import com.unlu.alimtrack.services.impl.VersionRecetaMetadataServiceImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProduccionMapper {

    @Lazy
    protected UsuarioServiceImpl usuarioServiceImpl;

    @Lazy
    private VersionRecetaMetadataServiceImpl versionRecetaMetadataServiceImpl;

    @Mapping(target = "emailCreador", source = "usuarioCreador.email")
    @Mapping(target = "codigoVersion", source = "versionReceta.codigoVersionReceta")
    public abstract ProduccionMetadataResponseDTO modelToResponseDTO(ProduccionModel model);

    public abstract List<ProduccionMetadataResponseDTO> modelListToResponseDTOList(List<ProduccionModel> modelList);

    @Mapping(target = "usuarioCreador", source = "emailCreador", qualifiedByName = "emailToModel")
    @Mapping(target = "produccion", ignore = true)
    @Mapping(target = "versionReceta", source = "codigoVersionReceta", qualifiedByName = "codigoVersionToVersionModel")
    @Mapping(target = "fechaInicio", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "estado", expression = "java(com.unlu.alimtrack.enums.TipoEstadoProduccion.EN_PROCESO)")
    @Mapping(target = "fechaFin", expression = "java(null)")
    public abstract ProduccionModel createDTOtoModel(ProduccionCreateDTO createDTO);


    public abstract MetadataProduccionPublicaResponseDTO modelToPublicDTO(ProduccionModel model);


    @Named("emailToModel")
    protected UsuarioModel usernameEmailToModel(String email) {
        return usuarioServiceImpl.getUsuarioModelByEmail(email);
    }

    @Named("codigoVersionToVersionModel")
    protected VersionRecetaModel codigoVersionToVersionModel(String codigoVersion) {
        return versionRecetaMetadataServiceImpl.findVersionModelByCodigo(codigoVersion);
    }


}

