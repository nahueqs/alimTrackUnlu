package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.RecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<RecetaModel, Long> {

    RecetaModel findByCodigoReceta(String codigoReceta);

    @Query("SELECT COUNT(r) > 0 FROM RecetaModel r WHERE r.codigoReceta = :codigoReceta AND r.id != :excludeId")
    boolean existsByCodigoRecetaAndIdNot(@Param("codigoReceta") String codigoReceta,
                                         @Param("excludeId") Long excludeId);

    void deleteByCodigoReceta(String codigo);

    boolean existsByCodigoReceta(String codigoReceta);

    @Query(value = "SELECT r FROM RecetaModel r WHERE r.creadoPor.username = :username")
    boolean existsByCreadoPorUsername(String username);

}
