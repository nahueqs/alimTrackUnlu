package com.unlu.alimtrack.models;

import com.unlu.alimtrack.enums.TipoRolUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(columnNames = {"username", "email"}))
public class UsuarioModel implements UserDetails {

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

    @Column(name = "esta_activo", columnDefinition = "boolean default true")
    private Boolean estaActivo = true;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}