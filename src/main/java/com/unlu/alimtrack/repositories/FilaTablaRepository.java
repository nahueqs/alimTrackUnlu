package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.FilaTablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilaTablaRepository extends JpaRepository<FilaTablaModel, Long> {

    @Query("SELECT f FROM FilaTablaModel f WHERE f.tabla.id IN :tablaIds ORDER BY f.orden")
    List<FilaTablaModel> findByTablaIds(@Param("tablaIds") List<Long> tablaIds);
}