package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaTablaRepository extends JpaRepository<RespuestaTablaModel, Long> {
    List<RespuestaTablaModel> findByIdProduccion(ProduccionModel produccion);
    
}
