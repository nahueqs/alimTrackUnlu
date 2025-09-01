package com.unlu.alimtrack.dtos.modify;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecetaModifyDTO(String nombre,
                              @Schema(description = "Use null para no modificar, empty string para borrar descripción")
                              String descripcion) {
}

