package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.VersionRecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRecetaRepository extends JpaRepository<VersionRecetaModel, Long> {

    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.id = :idVersion AND v.creadoPor.id = :idRecetaPadre")
    VersionRecetaModel findByIdRecetaPadreAndIdVersion(@Param("idRecetaPadre") Long idRecetaPadre,
                                                       @Param("idVersion") Long idVersion);

    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.recetaPadre.id = :idReceta")
    List<VersionRecetaModel> getVersionesByIdRecetaPadre(Long idReceta);

    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.creadoPor.id = :idCreadaPor")
    List<VersionRecetaModel> findAllByCreadaPorId(Long idCreadaPor);

    Optional<VersionRecetaModel> findByCodigoVersionReceta(String codigoVersionReceta);

    boolean existsByCodigoVersionReceta(String codigoVersion);

    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.recetaPadre.codigoReceta = :codigoRecetaPadre")
    List<VersionRecetaModel> findAllVersionesByCodigoRecetaPadre(String codigoRecetaPadre);

    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.creadoPor.email = :email")
    boolean existsByCreadaPorEmail(String email);

    List<VersionRecetaModel> findAllByCreadoPorEmail(String email);

    boolean existsByRecetaPadre_CodigoReceta(String recetaPadreCodigoReceta);
}
