package com.unlu.alimtrack.DTOS.modify;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record RecetaModifyDTO(

    @Size(min = 2, max = 255)
    String nombre,

    @Schema(description = "Use null para no modificar, empty string para borrar descripción")
    @Size(min = 2, max = 255)
    String descripcion) {

}

