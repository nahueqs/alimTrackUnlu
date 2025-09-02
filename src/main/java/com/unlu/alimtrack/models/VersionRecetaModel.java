package com.unlu.alimtrack.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "version_receta")
@NoArgsConstructor
public class VersionRecetaModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_version", nullable = false)
  private Long id;

  @Column(name = "nombre", nullable = false)
  private String nombre;

  @Column(name = "descripcion")
  private String descripcion;

  @Column(name = "codigo_version_receta", unique = true, nullable = false, updatable = false)
  private String codigoVersionReceta;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_receta_padre", nullable = false)
  @JsonIgnoreProperties("versiones")
  private RecetaModel recetaPadre;

  @OneToMany(mappedBy = "versionRecetaPadre", cascade = CascadeType.ALL)
  @JsonIgnoreProperties("versionRecetaPadre")
  private List<SeccionModel> secciones = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "creado_por", nullable = false)
  private UsuarioModel creadoPor;

  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion;

}