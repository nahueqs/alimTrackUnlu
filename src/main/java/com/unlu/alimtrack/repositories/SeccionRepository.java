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


    @Query("SELECT DISTINCT s FROM SeccionModel s " +
            "LEFT JOIN FETCH s.gruposCampos g " +
            "LEFT JOIN FETCH g.campos c " +
            "LEFT JOIN FETCH s.camposSimples cs " +
            "LEFT JOIN FETCH s.tablas t " +
            "LEFT JOIN FETCH t.columnas " +
            "LEFT JOIN FETCH t.filas " +
            "WHERE s.versionRecetaPadre = :versionRecetaPadre " +
            "ORDER BY s.orden, g.orden, c.orden")
    List<SeccionModel> findByVersionRecetaPadreCompleto(@Param("versionRecetaPadre") VersionRecetaModel versionRecetaPadre);

    // ✅ CONSULTA OPTIMIZADA para grupos con campos
    @Query("SELECT DISTINCT s FROM SeccionModel s " +
            "LEFT JOIN FETCH s.gruposCampos g " +
            "LEFT JOIN FETCH g.campos " +
            "WHERE s.versionRecetaPadre = :versionRecetaPadre " +
            "ORDER BY s.orden, g.orden")
    List<SeccionModel> findByVersionRecetaPadreWithGruposAndCampos(@Param("versionRecetaPadre") VersionRecetaModel versionRecetaPadre);


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


    //--------------------------
    // ✅ Consulta básica - solo secciones
    @Query("SELECT s FROM SeccionModel s WHERE s.versionRecetaPadre = :versionRecetaPadre ORDER BY s.orden")
    List<SeccionModel> findByVersionRecetaPadre(@Param("versionRecetaPadre") VersionRecetaModel versionRecetaPadre);

    // ✅ Cargar campos simples por separado
    @Query("SELECT s FROM SeccionModel s LEFT JOIN FETCH s.camposSimples WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findWithCamposSimples(@Param("secciones") List<SeccionModel> secciones);

    // ✅ Cargar grupos por separado (SIN CAMPOS)
    @Query("SELECT s FROM SeccionModel s LEFT JOIN FETCH s.gruposCampos WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findWithGruposCampos(@Param("secciones") List<SeccionModel> secciones);

    // ✅ Cargar tablas por separado
    @Query("SELECT s FROM SeccionModel s LEFT JOIN FETCH s.tablas WHERE s IN :secciones ORDER BY s.orden")
    List<SeccionModel> findWithTablas(@Param("secciones") List<SeccionModel> secciones);

    // ✅ Cargar campos dentro de grupos (CONSULTA CLAVE)
    @Query("SELECT DISTINCT gc FROM GrupoCamposModel gc LEFT JOIN FETCH gc.campos WHERE gc.seccion.idSeccion IN :idsSecciones ORDER BY gc.orden")
    List<GrupoCamposModel> findGruposWithCamposBySeccionIds(@Param("idsSecciones") List<Long> idsSecciones);
}
