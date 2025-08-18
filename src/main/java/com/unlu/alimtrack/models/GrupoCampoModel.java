package com.unlu.alimtrack.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Entity
@Table(name = "grupo_campos", uniqueConstraints = @UniqueConstraint(columnNames = {"subtitulo", "id_grupo"}))
public class GrupoCampoModel {
    @Id
    @Column(name = "id_grupo", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_seccion", nullable = false)
    private SeccionModel idSeccion;

    @Column(name = "subtitulo")
    @Length(min = 1, max = 255)
    private String subtitulo;
}