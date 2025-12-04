package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespuestaTablaRepository extends JpaRepository<RespuestaTablaModel, Long> {


    // Obtener última respuesta por tabla específica
    @Query(value = """
            SELECT rt1.* FROM respuesta_tabla rt1
            INNER JOIN (
                SELECT 
                    id_tabla, 
                    id_fila, 
                    id_columna, 
                    MAX(timestamp) as max_timestamp
                FROM respuesta_tabla
                WHERE id_produccion = :produccionId
                GROUP BY id_tabla, id_fila, id_columna
            ) rt2 ON rt1.id_tabla = rt2.id_tabla 
                AND rt1.id_fila = rt2.id_fila
                AND rt1.id_columna = rt2.id_columna
                AND rt1.timestamp = rt2.max_timestamp
            WHERE rt1.id_produccion = :produccionId
            ORDER BY rt1.id_tabla, rt1.id_fila, rt1.id_columna
            """, nativeQuery = true)
    List<RespuestaTablaModel> findAllUltimasRespuestasByProduccion(
            @Param("produccionId") Long produccionId);


    List<RespuestaTablaModel> findAllByProduccion(@Param("produccion") ProduccionModel produccion);

    Optional<RespuestaTablaModel> findByProduccionAndIdTablaIdAndFilaIdAndColumnaId(ProduccionModel Produccion, Long tablaId, Long filaId, Long columnaId);
}