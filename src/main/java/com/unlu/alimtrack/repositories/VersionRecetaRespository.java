package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.VersionRecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRecetaRespository extends JpaRepository<VersionRecetaModel, Long> {
}
