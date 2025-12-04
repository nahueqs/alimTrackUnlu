package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.TablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TablaRepository extends JpaRepository<TablaModel, Long> {

    @Query("SELECT DISTINCT t FROM TablaModel t " +
            "LEFT JOIN FETCH t.columnas col " +
            "WHERE t.id IN :tablaIds " +
            "ORDER BY t.orden, col.orden")
    List<TablaModel> findWithColumnasByTablaIds(@Param("tablaIds") List<Long> tablaIds);

    @Query("SELECT DISTINCT t FROM TablaModel t " +
            "LEFT JOIN FETCH t.filas f " +
            "WHERE t.id IN :tablaIds " +
            "ORDER BY t.orden, f.orden")
    List<TablaModel> findWithFilasByTablaIds(@Param("tablaIds") List<Long> tablaIds);

    // Método para encontrar tablas por IDs básico (sin relaciones)
    List<TablaModel> findByIdIn(List<Long> tablaIds);

    List<TablaModel> findAllBySeccion(SeccionModel seccion);

}
