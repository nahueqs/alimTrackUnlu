package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.impl.VersionRecetaMetadataServiceImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired
import org.springframework.context.annotation.Lazy; // Keep Lazy for VersionRecetaMetadataServiceImpl if needed elsewhere

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProduccionMapper {

    @Autowired
    protected UsuarioService usuarioService; // Not final, injected by Spring

    @Autowired
    @Lazy // Keep Lazy if there's a potential circular dependency with VersionRecetaMetadataServiceImpl
    protected VersionRecetaMetadataServiceImpl versionRecetaMetadataServiceImpl; // Not final, injected by Spring

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
    public UsuarioModel usernameEmailToModel(String email) {
        return usuarioService.getUsuarioModelByEmail(email);
    }

    @Named("codigoVersionToVersionModel")
    public VersionRecetaModel codigoVersionToVersionModel(String codigoVersion) {
        return versionRecetaMetadataServiceImpl.findVersionModelByCodigo(codigoVersion);
    }
}
