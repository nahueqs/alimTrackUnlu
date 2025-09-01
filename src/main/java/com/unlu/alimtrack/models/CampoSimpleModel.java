package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "campo_simple")
public class CampoSimpleModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_campo", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "id_seccion", nullable = false)
  @JsonIgnoreProperties("camposSimples")
  private SeccionModel seccion;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.SET_NULL)
  @JoinColumn(name = "id_grupo")
  @JsonIgnoreProperties("campos")
  private GrupoCamposModel grupo;

  @Column(name = "nombre", nullable = false, length = 100)
  private String nombre;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_dato")
  private TipoDatoCampo tipoDato;

  @ColumnDefault("0")
  @Column(name = "orden")
  private Integer orden;

}