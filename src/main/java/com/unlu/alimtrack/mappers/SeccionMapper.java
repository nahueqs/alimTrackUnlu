package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.CampoSimpleResponseDTO;
import com.unlu.alimtrack.DTOS.response.GrupoCamposResponseDTO;
import com.unlu.alimtrack.DTOS.response.SeccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.TablaResponseDTO;
import com.unlu.alimtrack.models.SeccionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CampoSimpleMapper.class, GrupoCamposMapper.class, TablaMapper.class})
public interface SeccionMapper {

    SeccionMapper INSTANCE = Mappers.getMapper(SeccionMapper.class);


    /**
     * Converts SeccionModel to SeccionResponseDTO with all related data
     *
     * @param model         The model to convert
     * @param camposSimples List of CampoSimpleResponseDTO
     * @param grupos        List of GrupoCamposResponseDTO
     * @param tablas        List of TablaResponseDTO
     * @return The complete response DTO
     */
    @Mapping(target = "idSeccion", source = "model.idSeccion")
    @Mapping(target = "codigoVersion", source = "model.versionRecetaPadre.codigoVersionReceta")
    @Mapping(target = "usernameCreador", source = "model.usernameCreador")
    @Mapping(target = "titulo", source = "model.titulo")
    @Mapping(target = "orden", source = "model.orden")
    @Mapping(target = "camposSimples", source = "camposSimples")
    @Mapping(target = "gruposCampos", source = "grupos")
    @Mapping(target = "tablas", source = "tablas")
    SeccionResponseDTO toResponseDTO(
            SeccionModel model,
            List<CampoSimpleResponseDTO> camposSimples,
            List<GrupoCamposResponseDTO> grupos,
            List<TablaResponseDTO> tablas
    );

    /**
     * Converts a list of SeccionModel to a list of SeccionResponseDTO
     *
     * @param models List of SeccionModel
     * @return List of SeccionResponseDTO
     */
    List<SeccionResponseDTO> toResponseDTOList(List<SeccionModel> models);

//    /**
//     * Updates a SeccionModel from a SeccionCreateDTO
//     * @param dto The DTO with updated values
//     * @param model The model to update
//     */
//    @Mapping(target = "idSeccion", ignore = true)
//    @Mapping(target = "versionRecetaPadre", ignore = true)
//    @Mapping(target = "usernameCreador", source = "dto.usernameCreador")
//    @Mapping(target = "titulo", source = "dto.titulo")
//    @Mapping(target = "orden", source = "dto.orden")
//    @Mapping(target = "camposSimples", ignore = true)
//    @Mapping(target = "gruposCampos", ignore = true)
//    @Mapping(target = "tablas", ignore = true)
//    void updateModelFromDTO(SeccionCreateDTO dto, @org.mapstruct.MappingTarget SeccionModel model);
}
