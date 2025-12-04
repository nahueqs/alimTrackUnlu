package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "grupo_campos", uniqueConstraints = @UniqueConstraint(columnNames = {"subtitulo",
        "id_seccion"}))
public class GrupoCamposModel {

    @Id
    @Column(name = "id_grupo", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_seccion", nullable = false)
    @JsonIgnoreProperties("gruposCampos")
    private SeccionModel seccion;

    @Column(name = "subtitulo", nullable = false)
    @Length(min = 1, max = 255)
    private String subtitulo;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("grupo")
    @BatchSize(size = 25)
    private Set<CampoSimpleModel> campos = new HashSet<>();

    public void addCampo(CampoSimpleModel campo) {
        campos.add(campo);
        campo.setGrupo(this);
    }

    public void removeCampo(CampoSimpleModel campo) {
        campos.remove(campo);
        campo.setGrupo(null);
    }

}
