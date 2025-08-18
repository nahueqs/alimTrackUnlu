package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;

public record GrupoCampoCreateDTO(@NotNull Long idSeccion, @NotNull String subtitulo) {
}
