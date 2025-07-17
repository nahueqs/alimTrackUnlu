package com.unlu.alimtrack.repositories;


import com.unlu.alimtrack.models.RecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaRepository extends JpaRepository<RecetaModel, Long> {
}
