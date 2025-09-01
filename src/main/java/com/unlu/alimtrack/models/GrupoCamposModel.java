package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Entity
@NoArgsConstructor
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

  @Column(name = "subtitulo")
  @Length(min = 1, max = 255)
  private String subtitulo;

  @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
  @JsonIgnoreProperties("grupo")
  private List<CampoSimpleModel> campos = new ArrayList<>();

  public void addCampo(CampoSimpleModel campo) {
    campos.add(campo);
    campo.setGrupo(this);
  }

  public void removeCampo(CampoSimpleModel campo) {
    campos.remove(campo);
    campo.setGrupo(null);
  }

}