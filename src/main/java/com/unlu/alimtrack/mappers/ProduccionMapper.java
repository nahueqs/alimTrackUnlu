package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.create.ProduccionCreateDTO;
import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = ProduccionModel.class, componentModel = "spring")
public interface ProduccionMapper {

  @Mapping(target = "codigoVersion", source = "versionReceta.codigoVersionReceta")
  ProduccionResponseDTO modelToResponseDTO(ProduccionModel model);

  List<ProduccionResponseDTO> modelListToResponseDTOList(List<ProduccionModel> modelList);

  ProduccionModel createDTOtoModel(ProduccionCreateDTO createDTO);
}

