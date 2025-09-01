package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;

public record SeccionCreateDTO(

    @NotNull
    String codigoVersionRecetaPadre,

    @NotNull
    String titulo,

    @NotNull
    String tipo,

    @NotNull
    int orden) {

}
