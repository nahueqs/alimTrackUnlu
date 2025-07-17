package com.unlu.alimtrack.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "grupocampos")
public class GrupoCampoModel {
    @Id
    @Column(name = "id_grupo", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_seccion", nullable = false)
    private SeccionModel idSeccion;

    @Column(name = "subtitulo")
    private String subtitulo;

}