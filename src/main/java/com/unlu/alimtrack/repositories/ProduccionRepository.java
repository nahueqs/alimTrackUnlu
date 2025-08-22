package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ProduccionRepository extends JpaRepository<ProduccionModel, Long> {

    List<ProduccionModel> findAllByEstado(String estado);

    // List<ProduccionModel> findByRecetaId(Long recetaId);

    List<ProduccionModel> findByFechaInicioBetween(Date start, Date end);

    List<ProduccionModel> findByVersionRecetaId(Long versionRecetaId);

    ProduccionModel findByCodigoProduccion(String codigo);

    List<ProduccionModel> findAllByVersionReceta(VersionRecetaModel version);
}