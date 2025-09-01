package com.unlu.alimtrack.dtos.create;

import jakarta.validation.constraints.NotNull;

public record ProduccionCreateDTO(@NotNull Long idVersionReceta, @NotNull String codigoProduccion,

                                  String lote,
                                  String encargado) {
}
//mas tarde implementar o no con usuario creador para iniciar la produccion
/* @NotNull Long idUsuarioCreador, */