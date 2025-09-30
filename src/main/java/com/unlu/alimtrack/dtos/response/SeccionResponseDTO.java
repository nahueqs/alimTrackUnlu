package com.unlu.alimtrack.dtos.response;

import java.util.List;

public record SeccionResponseDTO(
    String titulo,
    Integer orden,
    List<CampoSimpleResponseDTO> camposSimples
//    List<GrupoCamposResponseDTO> grupos,
//    List<TablaResponseDTO> tablas
){

}