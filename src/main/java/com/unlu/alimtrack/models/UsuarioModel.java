package com.unlu.alimtrack.models;

import com.unlu.alimtrack.enums.TipoRolUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import com.unlu.alimtrack.enums.TipoRolUsuario;

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

  @Column(name = "username", nullable = false, length = 50, unique = true)
  private String username;

  @Column(name = "nombre", nullable = false, length = 100)
  private String nombre;

  @Column(name = "email", nullable = false, length = 100)
  private String email;

  @Enumerated(EnumType.STRING)
  @ColumnDefault("'USUARIO'")
  @Column(name = "rol", nullable = false)
  private TipoRolUsuario rol;

  @ColumnDefault("1")
  @Column(name = "esta_activo")
  private Boolean estaActivo = true;

  @Column(name = "password", nullable = false, length = 60)
  private String password;

}