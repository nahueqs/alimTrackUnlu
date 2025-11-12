package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.AutoSaveProduccionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutoSaveRepository extends JpaRepository<AutoSaveProduccionModel, Long> {

    Optional<AutoSaveProduccionModel> findByProduccionProduccion(Long idProduccion);

    boolean existsByProduccionProduccion(Long idProduccion);

    @Modifying
    @Query("DELETE FROM AutoSaveProduccionModel a WHERE a.produccion.produccion = :idProduccion")
    void deleteByProduccionProduccion(@Param("idProduccion") Long idProduccion);
}
