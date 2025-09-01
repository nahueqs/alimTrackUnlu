package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unlu.alimtrack.enums.TipoSeccion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "seccion",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"id_version_receta_padre", "orden"}),
                @UniqueConstraint(columnNames = {"id_version_receta_padre", "titulo"}),
        }
)
public class SeccionModel {
    @Id
    @Column(name = "id_seccion", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_version_receta_padre", nullable = false)
    @JsonIgnoreProperties("secciones")
    private VersionRecetaModel versionRecetaPadre;

    @Column(name = "titulo", nullable = false)
    @Length(min = 1, max = 255)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoSeccion tipo;

    @OneToMany(mappedBy = "seccion", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("seccion")
    private List<GrupoCamposModel> gruposCampos = new ArrayList<>();

    @OneToMany(mappedBy = "seccion", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("seccion")
    private List<TablaModel> tablas = new ArrayList<>();

    @ColumnDefault("0")
    @Column(name = "orden")
    private Integer orden;

}