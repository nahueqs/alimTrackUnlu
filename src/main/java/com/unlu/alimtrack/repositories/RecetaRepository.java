package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.RecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<RecetaModel, Long> {

    RecetaModel findByCodigoReceta(String codigoReceta);

    boolean existsByCodigoReceta(String codigoReceta);

    boolean existsByCreadoPor_Email(String email);

    List<RecetaModel> findAllByCreadoPor_Email(String email);

    boolean existsByCodigoRecetaAndCreadoPorEmail(String codigoReceta, String email);

}
