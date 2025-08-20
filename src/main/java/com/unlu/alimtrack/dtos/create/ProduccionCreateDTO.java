package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;

public record ProduccionCreateDTO(@NotNull Long idVersionReceta, @NotNull String codigoProduccion, @NotNull Long idUsuarioCreador, String lote,
                                  String encargado) {
}
