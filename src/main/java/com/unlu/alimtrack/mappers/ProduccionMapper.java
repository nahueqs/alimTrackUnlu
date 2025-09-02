package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = ProduccionModel.class, componentModel = "spring")
public interface ProduccionMapper {

  @Mapping(target = "codigoVersion", source = "versionReceta.codigoVersionReceta")
  ProduccionResponseDTO produccionToProduccionResponseDTO(ProduccionModel produccion);

  List<ProduccionResponseDTO> toProduccionResponseDTOList(List<ProduccionModel> produccion);

}

