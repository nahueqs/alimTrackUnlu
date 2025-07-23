package com.unlu.alimtrack.dtos;

import com.unlu.alimtrack.models.RecetaModel;
import lombok.Getter;

import java.time.Instant;

@Getter
public class RecetaDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private UsuarioDto creadoPor; // o ID o nombre
    private Instant fechaCreacion;
}
