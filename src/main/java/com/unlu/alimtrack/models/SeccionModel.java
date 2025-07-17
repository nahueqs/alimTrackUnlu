package com.unlu.alimtrack.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "seccion")
public class SeccionModel {
    @Id
    @Column(name = "id_seccion", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_version", nullable = false)
    private VersionRecetaModel idVersion;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Lob
    @Column(name = "tipo", nullable = false)
    private String tipo;

    @ColumnDefault("0")
    @Column(name = "orden")
    private Integer orden;

}