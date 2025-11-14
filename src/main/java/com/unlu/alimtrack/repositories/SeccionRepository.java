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

    /**
     * ✅ SOLUCIÓN PROFESIONAL - Consultas completamente separadas por nivel
     * Evita todos los MultipleBagFetchException
     */

    // ========== NIVEL 1: SECCIONES ==========
    @Query("SELECT s FROM SeccionModel s " +
            "WHERE s.versionRecetaPadre = :version " +
            "ORDER BY s.orden")
    List<SeccionModel> findSeccionesBasicas(@Param("version") VersionRecetaModel version);

    // ========== NIVEL 2: COLECCIONES DIRECTAS DE SECCIÓN ==========

    @Query("SELECT DISTINCT s FROM SeccionModel s " +
            "LEFT JOIN FETCH s.camposSimples cs " +
            "WHERE s IN :secciones " +
            "ORDER BY s.orden")
    List<SeccionModel> findSeccionesConCamposSimples(@Param("secciones") List<SeccionModel> secciones);

    @Query("SELECT DISTINCT s FROM SeccionModel s " +
            "LEFT JOIN FETCH s.gruposCampos g " +
            "WHERE s IN :secciones " +
            "ORDER BY s.orden")
    List<SeccionModel> findSeccionesConGrupos(@Param("secciones") List<SeccionModel> secciones);

    @Query("SELECT DISTINCT s FROM SeccionModel s " +
            "LEFT JOIN FETCH s.tablas t " +
            "WHERE s IN :secciones " +
            "ORDER BY s.orden")
    List<SeccionModel> findSeccionesConTablas(@Param("secciones") List<SeccionModel> secciones);

    // ========== NIVEL 3: COLECCIONES ANIDADAS (DESDE SUS PROPIAS ENTIDADES) ==========

    // Cargar campos dentro de grupos (desde GrupoCamposModel)
    @Query("SELECT DISTINCT gc FROM GrupoCamposModel gc " +
            "LEFT JOIN FETCH gc.campos c " +
            "WHERE gc.seccion.idSeccion IN :idsSecciones " +
            "ORDER BY gc.orden")
    List<GrupoCamposModel> findGruposWithCamposBySeccionIds(@Param("idsSecciones") List<Long> idsSecciones);

    // Cargar columnas de tablas (desde TablaModel)
    @Query("SELECT DISTINCT t FROM TablaModel t " +
            "LEFT JOIN FETCH t.columnas col " +
            "WHERE t.seccion.idSeccion IN :idsSecciones " +
            "ORDER BY t.orden")
    List<TablaModel> findTablasWithColumnasBySeccionIds(@Param("idsSecciones") List<Long> idsSecciones);

    // Cargar filas de tablas (desde TablaModel)
    @Query("SELECT DISTINCT t FROM TablaModel t " +
            "LEFT JOIN FETCH t.filas f " +
            "WHERE t.seccion.idSeccion IN :idsSecciones " +
            "ORDER BY t.orden")
    List<TablaModel> findTablasWithFilasBySeccionIds(@Param("idsSecciones") List<Long> idsSecciones);

    // ========== VALIDACIONES ==========

    @Query("SELECT COUNT(s) > 0 FROM SeccionModel s " +
            "WHERE s.versionRecetaPadre.codigoVersionReceta = :codigoVersion " +
            "AND s.titulo = :titulo")
    boolean existsByCodigoVersionRecetaPadreAndTitulo(
            @Param("codigoVersion") String codigoVersion,
            @Param("titulo") String titulo
    );

    @Query("SELECT COUNT(s) > 0 FROM SeccionModel s " +
            "WHERE s.versionRecetaPadre.codigoVersionReceta = :codigoVersion " +
            "AND s.orden = :orden")
    boolean existsByCodigoVersionRecetaPadreAndOrden(
            @Param("codigoVersion") String codigoVersion,
            @Param("orden") Integer orden
    );
}