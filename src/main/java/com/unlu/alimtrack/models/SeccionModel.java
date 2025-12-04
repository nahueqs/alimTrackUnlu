package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
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
    @JsonIgnoreProperties("estructura")
    private VersionRecetaModel versionRecetaPadre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT) // Assuming RESTRICT is desired for FK
    @JoinColumn(name = "creado_por", referencedColumnName = "id_usuario", nullable = false)
    private UsuarioModel creadoPor;

    @Column(name = "titulo", nullable = false)
    @Length(min = 1, max = 255)
    private String titulo;

    @OneToMany(mappedBy = "seccion", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("seccion")
    private Set<GrupoCamposModel> gruposCampos = new HashSet<>();

    @OneToMany(mappedBy = "seccion", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("seccion")
    private Set<TablaModel> tablas = new HashSet<>();

    @OneToMany(mappedBy = "seccion", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("seccion")
    private Set<CampoSimpleModel> camposSimples = new HashSet<>();

    @ColumnDefault("0")
    @Column(name = "orden")
    private Integer orden;

}
