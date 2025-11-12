package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespuestaCampoRepository extends JpaRepository<RespuestaCampoModel, Long> {

    RespuestaCampoModel findByIdProduccionAndIdCampo(ProduccionModel produccion, CampoSimpleModel campo);

    boolean existsByIdProduccionAndIdCampo(ProduccionModel produccion, CampoSimpleModel campo);


    List<RespuestaCampoModel> findByIdProduccion(ProduccionModel produccion);
}