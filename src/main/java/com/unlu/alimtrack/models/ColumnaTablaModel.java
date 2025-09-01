package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "columna_tabla",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id_tabla", "orden"}))

public class ColumnaTablaModel {

  @Id
  @Column(name = "id_columna", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "id_tabla", nullable = false)
  @JsonIgnoreProperties("columnas")
  private TablaModel tabla;

  @Column(name = "nombre", nullable = false, length = 100)
  private String nombre;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_dato", nullable = false, length = 20)
  private TipoDatoCampo tipoDato;

  @ColumnDefault("0")
  @Column(name = "orden")
  private Integer orden;

}