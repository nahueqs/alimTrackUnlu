package com.unlu.alimtrack.dtos.request;

import jakarta.validation.constraints.NotNull;

public record RecetaModifyDTO(@NotNull String codigoReceta, String nombre, String descripcion) {
}

