package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProduccionCreateDTO(

    @NotNull
    @Size(min = 1, max = 255)
    String codigoVersionRecetaPadre,

    @NotNull
    @Size(min = 1, max = 255)
    String codigoProduccion,

    @NotNull
    @Size(min = 1, max = 50)
    String usernameCreador,

    @Size(min = 1, max = 100)
    String lote,

    @Size(min = 1, max = 100)
    String encargado) {

}
