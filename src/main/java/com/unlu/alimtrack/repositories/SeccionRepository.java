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
    // ✅ Consulta básica - solo estructura
    @Query("SELECT s FROM SeccionModel s WHERE s.versionRecetaPadre = :versionRecetaPadre ORDER BY s.orden")
    List<SeccionModel> findByVersionRecetaPadre(@Param("versionRecetaPadre") VersionRecetaModel versionRecetaPadre);

    // ✅ Cargar campos simples por separado
    @Query("SELECT s FROM SeccionModel s LEFT JOIN FETCH s.camposSimples WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findWithCamposSimples(@Param("estructura") List<SeccionModel> secciones);

    // ✅ Cargar grupos por separado
    @Query("SELECT s FROM SeccionModel s LEFT JOIN FETCH s.gruposCampos WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findWithGruposCampos(@Param("estructura") List<SeccionModel> secciones);

    // ✅ Cargar tablas por separado
    @Query("SELECT s FROM SeccionModel s LEFT JOIN FETCH s.tablas WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findWithTablas(@Param("estructura") List<SeccionModel> secciones);

    // ✅ Cargar campos dentro de grupos
    @Query("SELECT DISTINCT gc FROM GrupoCamposModel gc LEFT JOIN FETCH gc.campos WHERE gc.seccion IN :secciones")
    List<GrupoCamposModel> findGruposWithCampos(@Param("estructura") List<SeccionModel> secciones);

    @Query("SELECT DISTINCT gc FROM GrupoCamposModel gc " +
            "LEFT JOIN FETCH gc.campos c " +
            "WHERE gc.seccion.idSeccion IN :idsSecciones " +
            "ORDER BY gc.orden, c.orden")
    List<GrupoCamposModel> findGruposWithCamposBySeccionIds(@Param("idsSecciones") List<Long> idsSecciones);

    // ✅ Cargar columnas de tablas POR SEPARADO (evitar multiple bag)
    @Query("SELECT DISTINCT t FROM TablaModel t LEFT JOIN FETCH t.columnas WHERE t IN :tablas")
    List<TablaModel> findTablasWithColumnas(@Param("tablas") List<TablaModel> tablas);


    // ✅ Cargar filas de tablas POR SEPARADO (evitar multiple bag)
    @Query("SELECT DISTINCT t FROM TablaModel t LEFT JOIN FETCH t.filas WHERE t IN :tablas")
    List<TablaModel> findTablasWithFilas(@Param("tablas") List<TablaModel> tablas);

    /**
     * Verifica si existe una sección con un título específico en una versión de receta
     */
    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.recetaPadre.codigoReceta = :versionReceta")
    boolean existsByCodigoVersionRecetaPadreAndTitulo(String codigoVersion, String titulo);


    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.recetaPadre.codigoReceta = :codigoVersion")
    boolean existsByCodigoVersionRecetaPadreAndOrden(String codigoVersion, Integer orden);
}
