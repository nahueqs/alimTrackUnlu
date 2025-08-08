package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.RecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<RecetaModel, Long> {

    List<RecetaModel> findAllByCreadoPorId(Long id);
}
