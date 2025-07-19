package com.unlu.alimtrack.dtos;

import com.unlu.alimtrack.models.UsuarioModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO{
    private Long id;
    private String nombre;
    private String email;
    private Boolean esAdmin = false;

    public UsuarioDTO() {
    }

    public UsuarioDTO(UsuarioModel usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.esAdmin = usuario.getEsAdmin();
    }
}