package com.unlu.alimtrack.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "receta")
public class RecetaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receta", nullable = false)
    private Long id;

    @Column(name = "codigo_receta", unique = true, nullable = false, updatable = false)
    private String codigoReceta;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por", nullable = false)
    private UsuarioModel creadoPor;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;


}