package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.UsuarioResponseDTO;
import com.unlu.alimtrack.enums.TipoRolUsuario;
import com.unlu.alimtrack.models.UsuarioModel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "estaActivo", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "email")
    UsuarioModel toModel(UsuarioCreateDTO usuarioCreateDTO);

    void updateModelFromModifyDTO(UsuarioModifyDTO dto, @MappingTarget UsuarioModel model);

    List<UsuarioResponseDTO> toResponseDTOList(List<UsuarioModel> usuarios);

    @Mapping(target = "rol", source = "rol", qualifiedByName = "tipoRolToString")
    UsuarioResponseDTO toResponseDTO(UsuarioModel model);

    @Named("tipoRolToString")
    default String convertirTipoDatoAString(TipoRolUsuario tipoRol) {
        return tipoRol != null ? tipoRol.name() : null;
    }
}
