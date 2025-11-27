package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.FilaTablaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilaTablaRepository extends JpaRepository<FilaTablaModel, Long> {

}