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
@Table(name = "produccion")
public class ProduccionModel {
    @Id
    @Column(name = "id_produccion", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_version", nullable = false)
    private VersionRecetaModel idVersion;

    @Column(name = "fecha_inicio")
    private Instant fechaInicio;

    @Column(name = "fecha_fin")
    private Instant fechaFin;

    @Column(name = "lote", length = 100)
    private String lote;

    @Column(name = "encargado", length = 100)
    private String encargado;

    @ColumnDefault("'en_proceso'")
    @Lob
    @Column(name = "estado")
    private String estado;

}