package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.FilaTablaModel;
import com.unlu.alimtrack.models.TablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilaTablaRepository extends JpaRepository<FilaTablaModel, Long> {

    @Query("SELECT f FROM FilaTablaModel f WHERE f.tabla = :tabla")
    List<FilaTablaModel> findAllByTabla(@Param("tabla") TablaModel tabla);
}