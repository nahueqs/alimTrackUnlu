package com.unlu.alimtrack.DTOS.request;

import jakarta.validation.constraints.NotBlank;

public record RespuestaCeldaTablaResquestDTO(

        String valor,

        String emailCreador

) {
}
