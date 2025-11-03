package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeccionRepository extends JpaRepository<SeccionModel, Long> {

    /**
     * Busca una sección por su ID cargando sus relaciones de manera eager
     */
    @Query("SELECT s FROM SeccionModel s " +
            "LEFT JOIN FETCH s.gruposCampos g " +
            "LEFT JOIN FETCH s.camposSimples cs " +
            "LEFT JOIN FETCH s.tablas t " +
            "WHERE s.idSeccion = :id")
    Optional<SeccionModel> findByIdWithRelations(@Param("id") Long id);

    /**
     * Busca todas las secciones de una versión de receta específica
     */
    List<SeccionModel> findByVersionRecetaPadre(VersionRecetaModel versionReceta);

    /**
     * Busca todas las secciones de una versión de receta ordenadas por el campo 'orden'
     */
    List<SeccionModel> findByVersionRecetaPadreOrderByOrdenAsc(VersionRecetaModel versionReceta);

    /**
     * Verifica si existe una sección con un título específico en una versión de receta
     */
    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.recetaPadre.codigoReceta = :versionReceta")
    boolean existsByCodigoVersionRecetaPadreAndTitulo(String codigoVersion, String titulo);

    /**
     * Cuenta la cantidad de secciones de una versión de receta
     */
    long countByVersionRecetaPadre(VersionRecetaModel versionReceta);


    /**
     * Elimina todas las secciones de una versión de receta
     */
    void deleteByVersionRecetaPadre(VersionRecetaModel versionReceta);

    @Query(value = "SELECT v FROM VersionRecetaModel v WHERE v.recetaPadre.codigoReceta = :codigoVersion")
    boolean existsByCodigoVersionRecetaPadreAndOrden(String codigoVersion, Integer orden);
}
