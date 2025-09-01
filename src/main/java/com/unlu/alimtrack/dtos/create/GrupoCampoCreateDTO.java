package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GrupoCampoCreateDTO(

    @NotNull
    Long idSeccion,

    @NotNull
    @Size(min = 2, max = 255)
    String subtitulo

) {

}
