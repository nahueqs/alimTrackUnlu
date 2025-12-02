package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.CampoSimpleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampoSimpleRepository extends JpaRepository<CampoSimpleModel, Long> {

    Optional<CampoSimpleModel> findById(Long id);

}
