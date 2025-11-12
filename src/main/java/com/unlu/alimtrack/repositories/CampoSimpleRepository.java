package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.CampoSimpleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampoSimpleRepository extends JpaRepository<CampoSimpleModel, Long> {


}
