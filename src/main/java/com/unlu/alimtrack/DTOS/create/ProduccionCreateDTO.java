package com.unlu.alimtrack.DTOS.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para la creacion de una nueva produccion")
public record ProduccionCreateDTO(

        @NotNull
        @Schema(description = "Código de versión de receta padre de la produccion", example = "REC-V1-2024")
        @Size(min = 1, max = 50, message = "El código de versión debe tener entre 1 y 50 caracteres")
        String codigoVersionReceta,

        @NotNull
        @Schema(description = "Código de la produccion a crear", example = "PROD-1928")
        @Size(min = 1, max = 255, message = "El código de producción debe tener entre 1 y 255 caracteres")
        String codigoProduccion,

        @NotNull @Size(min = 1, max = 50)
        @Schema(description = "email del creador de la producción", example = "JuanPerez1@mail.com")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Formato de email inválido")
        String emailCreador,

        @Schema(description = "Número de lote", example = "LOTE-2024-001")
        @Pattern(regexp = "^[A-Z0-9-]{0,20}$", message = "Formato de lote inválido. Solo mayúsculas, números y guiones")
        @Size(max = 100, message = "El lote no puede exceder 100 caracteres")
        String lote,

        @Size(max = 100)
        @Schema(description = "encargado de la produccion a crear", example = "Jorge")
        String encargado,

        @Schema(description = "observaciones de la produccion a crear", example = "Quesos primera tanda, prueba 1")
        @Size(max = 255, message = "Las observaciones no pueden exceder 255 caracteres")
        String observaciones
) {

}
