package com.unlu.alimtrack.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class VersionRecetaDto {
    private Long id;
    private Long idRecetaPadre;
    private Integer numeroVersion;
    private Instant fechaCreacion;
    private Long idCreadoPor;

}
