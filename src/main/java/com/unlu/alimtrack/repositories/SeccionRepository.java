package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.SeccionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeccionRepository extends JpaRepository<SeccionModel, Long> {
}
