package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.UsuarioModel;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    boolean existsByEmail(String email);

    Optional<UsuarioModel> findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);

    boolean estaActivo(Boolean estaActivo);

    Optional<UsuarioModel> findByEmail(@NotNull String email);
}
