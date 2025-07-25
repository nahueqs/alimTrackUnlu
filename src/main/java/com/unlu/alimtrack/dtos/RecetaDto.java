package com.unlu.alimtrack.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class RecetaDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private UsuarioDto creadoPor; // o ID o nombre
    private Instant fechaCreacion;
}
