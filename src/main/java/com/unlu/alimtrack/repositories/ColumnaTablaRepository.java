package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.ColumnaTablaModel;
import com.unlu.alimtrack.models.TablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnaTablaRepository extends JpaRepository<ColumnaTablaModel, Long> {


    @Query("SELECT c FROM ColumnaTablaModel c WHERE c.tabla = :tabla")
    List<ColumnaTablaModel> findAllByTabla(@Param("tabla") TablaModel tabla);
}

