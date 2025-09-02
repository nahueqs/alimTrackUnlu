package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ProduccionResponseDTO(@NotNull String codigoProduccion, String codigoVersion,
                                    String encargado,
                                    String lote,
                                    String estado, LocalDateTime fechaInicio) {

}
