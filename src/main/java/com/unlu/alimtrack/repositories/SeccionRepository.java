package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeccionRepository extends JpaRepository<SeccionModel, Long> {

    @Query("SELECT s FROM SeccionModel s WHERE s.versionRecetaPadre = :version ORDER BY s.orden")
    List<SeccionModel> findSeccionesBasicas(@Param("version") VersionRecetaModel version);

    @Query("SELECT DISTINCT s FROM SeccionModel s LEFT JOIN FETCH s.camposSimples WHERE s IN :secciones")
    List<SeccionModel> fetchCamposSimples(@Param("secciones") List<SeccionModel> secciones);

    @Query("SELECT DISTINCT s FROM SeccionModel s LEFT JOIN FETCH s.gruposCampos WHERE s IN :secciones")
    List<SeccionModel> fetchGruposCampos(@Param("secciones") List<SeccionModel> secciones);

    @Query("SELECT DISTINCT s FROM SeccionModel s LEFT JOIN FETCH s.tablas WHERE s IN :secciones")
    List<SeccionModel> fetchTablas(@Param("secciones") List<SeccionModel> secciones);


    @Query("SELECT DISTINCT s FROM SeccionModel s LEFT JOIN FETCH s.gruposCampos gc LEFT JOIN FETCH gc.campos WHERE s IN :secciones")
    List<SeccionModel> fetchCamposInGruposCampos(@Param("secciones") List<SeccionModel> secciones);


    boolean existsByVersionRecetaPadre_CodigoVersionRecetaAndOrden(String codigoVersionReceta, Integer orden);

    boolean existsByVersionRecetaPadre_CodigoVersionRecetaAndTitulo(String codigoVersionReceta, String titulo);
}
