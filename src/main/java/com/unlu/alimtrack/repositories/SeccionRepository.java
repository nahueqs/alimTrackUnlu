package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.GrupoCamposModel;
import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.TablaModel;
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

    // Consultas para cargar colecciones de forma eficiente
    @Query("SELECT DISTINCT s FROM SeccionModel s LEFT JOIN FETCH s.camposSimples cs WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findSeccionesConCamposSimples(@Param("secciones") List<SeccionModel> secciones);

    @Query("SELECT DISTINCT s FROM SeccionModel s LEFT JOIN FETCH s.gruposCampos g WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findSeccionesConGrupos(@Param("secciones") List<SeccionModel> secciones);

    @Query("SELECT DISTINCT s FROM SeccionModel s LEFT JOIN FETCH s.tablas t WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findSeccionesConTablas(@Param("secciones") List<SeccionModel> secciones);

    @Query("SELECT DISTINCT gc FROM GrupoCamposModel gc LEFT JOIN FETCH gc.campos c WHERE gc.seccion.id IN :idsSecciones ORDER BY gc.orden")
    List<GrupoCamposModel> findGruposWithCamposBySeccionIds(@Param("idsSecciones") List<Long> idsSecciones);

    @Query("SELECT DISTINCT t FROM TablaModel t LEFT JOIN FETCH t.columnas c WHERE t.seccion.id IN :idsSecciones ORDER BY t.orden, c.orden")
    List<TablaModel> findTablasWithColumnasBySeccionIds(@Param("idsSecciones") List<Long> idsSecciones);

    @Query("SELECT DISTINCT t FROM TablaModel t LEFT JOIN FETCH t.filas f WHERE t.seccion.id IN :idsSecciones ORDER BY t.orden, f.orden")
    List<TablaModel> findTablasWithFilasBySeccionIds(@Param("idsSecciones") List<Long> idsSecciones);

    boolean existsByVersionRecetaPadre_CodigoVersionRecetaAndOrden(String codigoVersionReceta, Integer orden);

    boolean existsByVersionRecetaPadre_CodigoVersionRecetaAndTitulo(String codigoVersionReceta, String titulo);
}
