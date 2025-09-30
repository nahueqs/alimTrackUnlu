package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.create.SeccionCreateDTO;
import com.unlu.alimtrack.dtos.response.CampoSimpleResponseDTO;
import com.unlu.alimtrack.dtos.response.SeccionResponseDTO;
import com.unlu.alimtrack.models.SeccionModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeccionMapper {

  @Mapping(target = "versionRecetaPadre.id", source = "codigoVersionRecetaPadre")
  SeccionModel createDTOToModel(SeccionCreateDTO seccionCreateDTO);

  @Mapping(target = "camposSimples", source = "camposSimples")
  SeccionResponseDTO toResponseDTO(SeccionModel model, List<CampoSimpleResponseDTO> camposSimples );
//      List<GrupoCamposResponseDTO> grupos,
//      List<TablaResponseDTO> tablas);
}
