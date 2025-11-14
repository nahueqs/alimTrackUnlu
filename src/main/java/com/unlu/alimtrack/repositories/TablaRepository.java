package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.TablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TablaRepository extends JpaRepository<TablaModel, Long> {

    @Query("SELECT t FROM TablaModel t WHERE t.seccion.idSeccion IN :seccionIds ORDER BY t.orden")
    List<TablaModel> findBySeccionIds(@Param("seccionIds") List<Long> seccionIds);

    @Query("SELECT DISTINCT t FROM TablaModel t " +
            "LEFT JOIN FETCH t.columnas col " +
            "WHERE t.seccion.idSeccion IN :seccionIds " +
            "ORDER BY t.orden")
    List<TablaModel> findWithColumnasBySeccionIds(@Param("seccionIds") List<Long> seccionIds);

    @Query("SELECT DISTINCT t FROM TablaModel t " +
            "LEFT JOIN FETCH t.filas f " +
            "WHERE t.seccion.idSeccion IN :seccionIds " +
            "ORDER BY t.orden")
    List<TablaModel> findWithFilasBySeccionIds(@Param("seccionIds") List<Long> seccionIds);
}