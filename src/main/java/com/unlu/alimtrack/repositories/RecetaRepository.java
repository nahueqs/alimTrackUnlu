package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.RecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<RecetaModel, Long> {

    List<RecetaModel> findAllByCreadoPorId(Long id);

    RecetaModel findByCodigoReceta(String codigoReceta);

    @Query("SELECT COUNT(r) > 0 FROM RecetaModel r WHERE r.codigoReceta = :codigoReceta AND r.id != :excludeId")
    boolean existsByCodigoRecetaAndIdNot(@Param("codigoReceta") String codigoReceta,
                                         @Param("excludeId") Long excludeId);

    List<RecetaModel> findAllByCodigoReceta(String codigoReceta);
}
