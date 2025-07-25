package com.unlu.alimtrack.dtos.request;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class versionRecetaCreateDto {
    private final Long id;
    private final Instant fechaCreacion;
    private final Long idCreadoPor;

    public versionRecetaCreateDto(Long id, Instant fechaCreacion, Long idCreadoPor) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.idCreadoPor = idCreadoPor;
    }
}
