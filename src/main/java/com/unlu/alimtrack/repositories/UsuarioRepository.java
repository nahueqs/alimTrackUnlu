package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.UsuarioModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

  boolean existsByEmail(String email);

  Optional<UsuarioModel> findByUsername(String username);

  boolean existsByUsername(String username);

  void deleteByUsername(String username);
}
