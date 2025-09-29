package com.unlu.alimtrack.models;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;
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
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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
  private Long id_produccion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "id_version", nullable = false)
  private VersionRecetaModel versionReceta;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "usuario_creador", nullable = false)
  private UsuarioModel usuarioCreador;

  @Size(max = 100)
  @Column(name = "codigo_produccion")
  private String codigoProduccion;

  @Column(name = "fecha_inicio")
  private LocalDateTime fechaInicio;

  @Column(name = "fecha_fin")
  private LocalDateTime fechaFin;

  @Size(max = 100)
  @Column(name = "lote")
  private String lote;

  @Size(max = 100)
  @Column(name = "encargado")
  private String encargado;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", length = 20, nullable = false)
  @ColumnDefault("'EN_CURSO'")
  private TipoEstadoProduccion estado = TipoEstadoProduccion.EN_CURSO;

  @Size(max = 255)
  @Column(name = "observaciones")
  private String observaciones;


}