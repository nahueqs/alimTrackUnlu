package com.unlu.alimtrack.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;

// DTO usado para recibir la peticion de crear una nueva version

public record VersionRecetaCreateDTO(@NotNull Long id, Instant fechaCreacion, Long idCreadoPor) {
    public VersionRecetaCreateDTO(Long id, Instant fechaCreacion, Long idCreadoPor) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.idCreadoPor = idCreadoPor;
    }
}
