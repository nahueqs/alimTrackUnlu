package com.unlu.alimtrack.models;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "produccion")
public class ProduccionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produccion", nullable = false)
    private Long produccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_version", nullable = false)
    private VersionRecetaModel versionReceta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "creado_por", nullable = false)
    private UsuarioModel usuarioCreador;

    @Size(max = 100)
    @Column(name = "codigo_produccion")
    private String codigoProduccion;

    @CreationTimestamp
    @Column(name = "fecha_inicio", updatable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Size(max = 100)
    @Column(name = "lote")
    private String lote;

    @Size(max = 100)
    @Column(name = "encargado")
    private String encargado;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'EN_PROCESO'")
    @Column(name = "estado", length = 20, nullable = false)
    private TipoEstadoProduccion estado;

    @Size(max = 255)
    @Column(name = "observaciones")
    private String observaciones;


    // Y en @PrePersist
    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = TipoEstadoProduccion.EN_PROCESO;
        }
    }
}