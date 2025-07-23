package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.VersionRecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRecetaRespository extends JpaRepository<VersionRecetaModel, Long> {

    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.id = :idVersion AND v.creadoPor.id = :idRecetaPadre" )
    VersionRecetaModel findByIdRecetaPadreAndIdVersion(@Param("idRecetaPadre") Long idRecetaPadre,@Param("idVersion") Long idVersion);

}
