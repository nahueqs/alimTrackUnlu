package com.unlu.alimtrack.dtos;

import com.unlu.alimtrack.models.UsuarioModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String email;
    private Boolean esAdmin = false;
}