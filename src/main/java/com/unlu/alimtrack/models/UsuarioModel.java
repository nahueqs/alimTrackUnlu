package com.unlu.alimtrack.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class UsuarioModel {
    @Id
    @Column(name = "id_usuario", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @ColumnDefault("0")
    @Column(name = "es_admin", nullable = false)
    private Boolean esAdmin = false;

    @Column(name = "contraseña", nullable = false, length = 60)
    private String contraseña;

}