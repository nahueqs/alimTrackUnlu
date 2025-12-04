package com.unlu.alimtrack.DTOS.request;

import jakarta.validation.constraints.NotBlank;

public record RespuestaCeldaTablaResquestDTO(

        @NotBlank
        String valor,

        String emailCreador

) {
}
