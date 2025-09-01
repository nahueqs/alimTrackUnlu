package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.RecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaRepository extends JpaRepository<RecetaModel, Long> {

    RecetaModel findByCodigoReceta(String codigoReceta);

    void deleteByCodigoReceta(String codigo);

    boolean existsByCodigoReceta(String codigoReceta);

    @Query(value = "SELECT r FROM RecetaModel r WHERE r.creadoPor.username = :username")
    boolean existsByCreadoPorUsername(String username);

    boolean existsByCodigoRecetaAndCreadoPorUsername(String codigoReceta, String username);

    @Query(value = "SELECT r FROM RecetaModel r WHERE r.creadoPor.username = :username")
    Optional<List<RecetaModel>> findAllByCreadoPorUsername(String username);
}
