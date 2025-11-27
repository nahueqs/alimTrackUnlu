package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.AutoSaveProduccionModel;
import com.unlu.alimtrack.models.ProduccionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutoSaveRepository extends JpaRepository<AutoSaveProduccionModel, Long> {

    Optional<AutoSaveProduccionModel> findByProduccion(ProduccionModel produccion);

    boolean existsByProduccion(ProduccionModel produccion);

    void deleteByProduccion(ProduccionModel produccion);
}
