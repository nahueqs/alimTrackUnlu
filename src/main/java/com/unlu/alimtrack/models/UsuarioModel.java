package com.unlu.alimtrack.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "usuario")
public class UsuarioModel {

  @Id
  @Column(name = "id_usuario", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre_usuario", nullable = false, length = 50, unique = true)
  private String username;

  @Column(name = "nombre_completo", nullable = false, length = 100)
  private String nombre;

  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @ColumnDefault("0")
  @Column(name = "es_admin", nullable = false)
  private Boolean esAdmin = false;

  @ColumnDefault("1")
  @Column(name = "esta_activo")
  private Boolean estaActivo = true;

  @Column(name = "contraseña", nullable = false, length = 60)
  private String contraseña;

}