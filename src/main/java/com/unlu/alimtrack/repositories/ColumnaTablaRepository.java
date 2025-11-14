package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.ColumnaTablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnaTablaRepository extends JpaRepository<ColumnaTablaModel, Long> {

    @Query("SELECT c FROM ColumnaTablaModel c WHERE c.tabla.id IN :tablaIds ORDER BY c.orden")
    List<ColumnaTablaModel> findByTablaIds(@Param("tablaIds") List<Long> tablaIds);
}
