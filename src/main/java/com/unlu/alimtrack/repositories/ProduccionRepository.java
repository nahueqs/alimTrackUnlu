package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.models.ProduccionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduccionRepository extends JpaRepository<ProduccionModel, Long> {

    Optional<ProduccionModel> findByCodigoProduccion(String codigoProduccion);

    @Query("SELECT new com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO(p.codigoProduccion, p.estado, p.fechaModificacion) " +
            "FROM ProduccionModel p WHERE p.codigoProduccion = :codigoProduccion")
    Optional<EstadoProduccionPublicoResponseDTO> findProduccionPublicByCodigoProduccion(@Param("codigoProduccion") String codigoProduccion);

    @Query("SELECT p FROM ProduccionModel p WHERE "
            + "(:codigoVersionReceta IS NULL OR p.versionReceta.codigoVersionReceta = :codigoVersionReceta) AND "
            + "(:lote IS NULL OR p.lote = :lote) AND "
            + "(:encargado IS NULL OR LOWER(p.encargado) = LOWER(:encargado)) AND "
            + "(:fechaInicio IS NULL OR p.fechaInicio >= :fechaInicio) AND "
            + "(:fechaFin IS NULL OR p.fechaFin <= :fechaFin) AND "
            + "(:estado IS NULL OR p.estado = :estado)    "
    )
    List<ProduccionModel> findByAdvancedFilters(@Param("codigoVersionReceta") String codigoVersionReceta,
                                                @Param("lote") String lote, @Param("encargado") String encargado, @Param("estado") TipoEstadoProduccion estado,
                                                @Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin); // Removed Sort parameter

    boolean existsByLote(String lote);

    boolean existsByEncargadoIgnoreCase(String encargado);

    boolean existsByCodigoProduccion(String codigoProduccion);

    boolean existsByVersionReceta_CodigoVersionReceta(String codigoReceta);
}
