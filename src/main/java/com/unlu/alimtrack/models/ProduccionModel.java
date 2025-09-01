package com.unlu.alimtrack.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "produccion")
public class ProduccionModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_produccion", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "id_version", nullable = false)
  private VersionRecetaModel versionReceta;

  @Size(max = 100)
  @Column(name = "codigo_produccion")
  private String codigoProduccion;

  @Column(name = "fecha_inicio")
  private Instant fechaInicio;

  @Column(name = "fecha_fin")
  private Instant fechaFin;

  @Size(max = 100)
  @Column(name = "lote")
  private String lote;

  @Size(max = 100)
  @Column(name = "encargado")
  private String encargado;

  @ColumnDefault("'en_curso'")
  @Column(name = "estado")
  private String estado;

  @Size(max = 255)
  @Column(name = "observaciones")
  private String observaciones;


}