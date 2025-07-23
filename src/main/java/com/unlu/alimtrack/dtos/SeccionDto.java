package com.unlu.alimtrack.dtos;


import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import lombok.Getter;

@Getter
public class SeccionDto {
    private Integer id;
    private VersionRecetaModel idVersion;
    private String titulo;
    private String tipo;
    private Integer orden;

}
