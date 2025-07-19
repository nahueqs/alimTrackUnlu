package com.unlu.alimtrack.dtos;

import com.unlu.alimtrack.models.RecetaModel;
import lombok.Getter;

import java.time.Instant;

@Getter
public class RecetaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private UsuarioDTO creadoPor; // o ID o nombre
    private Instant fechaCreacion;



    public RecetaDTO(RecetaModel receta) {
        this.id = receta.getId();
        this.nombre = receta.getNombre();
        this.descripcion = receta.getDescripcion();
        this.fechaCreacion = receta.getFechaCreacion();

        UsuarioDTO usuarioDtoLocal = new UsuarioDTO(receta.getCreadoPor());

        this.creadoPor = usuarioDtoLocal;
        System.out.println(creadoPor);
    }


}
