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

    public RecetaDto(RecetaModel receta) {
        this.id = receta.getId();
        this.nombre = receta.getNombre();
        this.descripcion = receta.getDescripcion();
        this.fechaCreacion = receta.getFechaCreacion();
        this.creadoPor = new UsuarioDto(receta.getCreadoPor());
        System.out.println(creadoPor);
    }

}
