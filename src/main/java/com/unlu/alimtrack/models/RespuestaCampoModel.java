package com.unlu.alimtrack.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "respuesta_campo")
public class RespuestaCampoModel {

  @Id
  @Column(name = "id_respuesta", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "id_produccion", nullable = false)
  private ProduccionModel idProduccion;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "id_campo", nullable = false)
  private CampoSimpleModel idCampo;

  @Lob
  @Column(name = "valor")
  private String valor;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "timestamp")
  private LocalDateTime timestamp;

}