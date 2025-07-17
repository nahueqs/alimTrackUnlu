package com.unlu.alimtrack.dtos;

import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import lombok.Getter;

import java.time.Instant;

@Getter
public class RecetaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long creadoPorId; // o ID o nombre
    private Instant fechaCreacion;

    public RecetaDTO(RecetaModel receta) {
        this.id = receta.getId();
        this.nombre = receta.getNombre();
        this.descripcion = receta.getDescripcion();
        this.fechaCreacion = receta.getFechaCreacion();
        this.creadoPorId = receta.getCreadoPor().getId();
        System.out.println(creadoPorId);
    }

}
