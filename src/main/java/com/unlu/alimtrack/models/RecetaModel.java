package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "recetaPadre", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("recetaPadre")
    private List<VersionRecetaModel> versiones = new ArrayList<>();

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por", nullable = false)
    private UsuarioModel creadoPor;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;


}