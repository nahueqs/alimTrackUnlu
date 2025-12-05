package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespuestaCampoRepository extends JpaRepository<RespuestaCampoModel, Long> {

    RespuestaCampoModel findByIdProduccionAndIdCampo(ProduccionModel produccion, CampoSimpleModel campo);

    @Query(value = """
            SELECT rc1.* FROM respuesta_campo rc1
            INNER JOIN (
                SELECT
                    id_campo,
                    MAX(timestamp) as max_timestamp
                FROM respuesta_campo
                WHERE id_produccion = :produccionId
                GROUP BY id_campo
            ) rc2 ON rc1.id_campo = rc2.id_campo
                AND rc1.timestamp = rc2.max_timestamp
            WHERE rc1.id_produccion = :produccionId
            ORDER BY rc1.id_campo
            """, nativeQuery = true)
    List<RespuestaCampoModel> findAllUltimasRespuestasByProduccion(@Param("produccionId") Long produccionId);


    Optional<RespuestaCampoModel> findTopByIdProduccionAndIdCampoOrderByTimestampDesc(ProduccionModel idProduccion, CampoSimpleModel idCampo);
}