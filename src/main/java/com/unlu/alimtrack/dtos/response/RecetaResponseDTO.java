package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record RecetaResponseDTO(@NotNull String codigoReceta,
                                String nombre, String descripcion,
                                LocalDateTime fechaCreacion, String creadaPor)
{

}
