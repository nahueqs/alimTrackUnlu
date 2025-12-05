package com.unlu.alimtrack.DTOS.request;

import jakarta.validation.constraints.NotBlank;

public record RespuestaCampoRequestDTO(


        String valor,
        String emailCreador


) {
}
