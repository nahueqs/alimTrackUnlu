package com.unlu.alimtrack.DTOS.request;

import jakarta.validation.constraints.NotBlank;

public record RespuestaCampoRequestDTO(
        @NotBlank
        String valor,
        String emailCreador


) {
}
