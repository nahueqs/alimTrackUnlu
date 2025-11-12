package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ProduccionResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProduccionMapper {

    @Autowired
    @Lazy
    protected UsuarioService usuarioService;

    @Lazy
    @Autowired
    private VersionRecetaMetadataService versionRecetaMetadataService;

    @Mapping(target = "usernameCreador", source = "usuarioCreador.username")
    @Mapping(target = "codigoVersion", source = "versionReceta.codigoVersionReceta")
    public abstract ProduccionResponseDTO modelToResponseDTO(ProduccionModel model);

    public abstract List<ProduccionResponseDTO> modelListToResponseDTOList(List<ProduccionModel> modelList);

    @Mapping(target = "usuarioCreador", source = "usernameCreador", qualifiedByName = "usernameToUsuarioModel")
    @Mapping(target = "produccion", ignore = true)
    @Mapping(target = "versionReceta", source = "codigoVersionReceta", qualifiedByName = "codigoVersionToVersionModel")
    @Mapping(target = "fechaInicio", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "estado", expression = "java(com.unlu.alimtrack.enums.TipoEstadoProduccion.EN_PROCESO)")
    @Mapping(target = "fechaFin", expression = "java(null)")
    public abstract ProduccionModel createDTOtoModel(ProduccionCreateDTO createDTO);

    @Named("usernameToUsuarioModel")
    protected UsuarioModel usernameToUsuarioModel(String username) {
        return usuarioService.getUsuarioModelByUsername(username);
    }

    @Named("codigoVersionToVersionModel")
    protected VersionRecetaModel codigoVersionToVersionModel(String codigoVersion) {
        return versionRecetaMetadataService.findVersionModelByCodigo(codigoVersion);
    }


}

