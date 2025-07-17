package com.unlu.alimtrack.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "respuestacampo")
public class RespuestaCampoModel {
    @Id
    @Column(name = "id_respuesta", nullable = false)
    private Integer id;

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
    private Instant timestamp;

}