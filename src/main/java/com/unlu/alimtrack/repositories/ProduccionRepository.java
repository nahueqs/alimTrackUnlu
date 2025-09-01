package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ProduccionRepository extends JpaRepository<ProduccionModel, Long> {

    Boolean existsByCodigoProduccion(String codigoProduccion);

    List<ProduccionModel> findAllByEstado(String estado);

    // List<ProduccionModel> findByRecetaId(Long recetaId);

    ProduccionModel findByCodigoProduccion(String codigoProduccion);

    List<ProduccionModel> findAllByVersionReceta(VersionRecetaModel version);

    @Query("SELECT p FROM ProduccionModel p WHERE " +
            "(:codigoVersionReceta IS NULL OR p.versionReceta.codigoVersionReceta = :codigoVersionReceta) AND " +
            "(:lote IS NULL OR p.lote = :lote) AND " +
            "(:encargado IS NULL OR LOWER(p.encargado) = LOWER(:encargado)) AND " +
            "(:fechaInicio IS NULL OR p.fechaInicio >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR p.fechaInicio <= :fechaFin)")
    List<ProduccionModel> findByAdvancedFilters(
            @Param("codigoVersionReceta") String codigoVersionReceta,
            @Param("lote") String lote,
            @Param("encargado") String encargado,
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin);

    @Query("SELECT p FROM ProduccionModel p WHERE " +
            "p.versionReceta.codigoVersionReceta = :codigoVersionReceta AND " +
            "p.fechaInicio BETWEEN :fechaInicio AND :fechaFin")
    List<ProduccionModel> findByVersionRecetaAndFechaRange(
            @Param("codigoVersionReceta") String codigoVersionReceta,
            @Param("fechaInicio") Instant fechaInicio,
            @Param("fechaFin") Instant fechaFin);

    boolean existsByLote(String lote);

    boolean existsByEncargadoIgnoreCase(String encargado);

    //List<ProduccionModel> findAllByCodigoVersionReceta(String codigoVersionReceta);
}