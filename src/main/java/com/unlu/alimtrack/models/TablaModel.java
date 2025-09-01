package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "tabla")
public class TablaModel {

  @Id
  @Column(name = "id_tabla", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "id_seccion", nullable = false)
  @JsonIgnoreProperties("tablas")
  private SeccionModel seccion;

  @Column(name = "nombre")
  private String nombre;

  @OneToMany(mappedBy = "tabla", cascade = CascadeType.ALL)
  @JsonIgnoreProperties("tabla")
  private List<ColumnaTablaModel> columnas = new ArrayList<>();

  @OneToMany(mappedBy = "tabla", cascade = CascadeType.ALL)
  @JsonIgnoreProperties("tabla")
  private List<FilaTablaModel> filas = new ArrayList<>();


}