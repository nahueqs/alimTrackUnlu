package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.ColumnaTablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnaTablaRepository extends JpaRepository<ColumnaTablaModel, Long> {

}
