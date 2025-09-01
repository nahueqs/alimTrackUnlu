package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record VersionRecetaResponseDTO(@NotNull String codigoVersionReceta,
                                       String nombreRecetaPadre, String nombre,
                                       String descripcion,
                                       String creadaPor, Instant fechaCreacion) {

}
