package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.ProduccionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProduccionRepository extends JpaRepository<ProduccionModel, Long> {
    List<ProduccionModel> findByEstado(String estado);

   // List<ProduccionModel> findByRecetaId(Long recetaId);
    List<ProduccionModel> findByFechaInicioBetween(Date start, Date end);
    List<ProduccionModel> findByVersionRecetaId(Long versionRecetaId);

}