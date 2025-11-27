package com.unlu.alimtrack.repositories;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.enums.TipoRolUsuario;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles; // Import added

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Added this line
public class ProduccionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProduccionRepository produccionRepository;

    private VersionRecetaModel versionReceta;
    private UsuarioModel usuarioCreador;
    private RecetaModel recetaPadre;

    @BeforeEach
    void setUp() {
        // Creamos las dependencias necesarias que no son el foco del test
        usuarioCreador = new UsuarioModel();
        usuarioCreador.setEmail("test@example.com");
        usuarioCreador.setPassword("password");
        usuarioCreador.setNombre("Test User");
        usuarioCreador.setEstaActivo(true);
        usuarioCreador.setRol(TipoRolUsuario.ADMIN); // Corregido: Asignar un rol
        entityManager.persist(usuarioCreador);

        recetaPadre = new RecetaModel();
        recetaPadre.setCodigoReceta("REC001");
        recetaPadre.setNombre("Receta Padre");
        recetaPadre.setDescripcion("Descripcion Receta Padre");
        recetaPadre.setCreadoPor(usuarioCreador);
        recetaPadre.setFechaCreacion(LocalDateTime.now()); // Explicitly set fechaCreacion
        entityManager.persist(recetaPadre);

        versionReceta = new VersionRecetaModel();
        versionReceta.setCodigoVersionReceta("VER001");
        versionReceta.setNombre("1.0");
        versionReceta.setDescripcion("Descripcion Version 1.0");
        versionReceta.setCreadoPor(usuarioCreador);
        versionReceta.setRecetaPadre(recetaPadre);
        versionReceta.setFechaCreacion(LocalDateTime.now()); // Explicitly set fechaCreacion
        entityManager.persist(versionReceta);
    }

    private ProduccionModel createAndPersistProduccion(String codigo, TipoEstadoProduccion estado, LocalDateTime fechaInicio, String lote, String encargado) {
        ProduccionModel produccion = new ProduccionModel();
        produccion.setCodigoProduccion(codigo);
        produccion.setEstado(estado);
        produccion.setFechaInicio(fechaInicio);
        produccion.setVersionReceta(versionReceta);
        produccion.setUsuarioCreador(usuarioCreador);
        produccion.setLote(lote);
        produccion.setEncargado(encargado);
        return entityManager.persist(produccion);
    }

    @Test
    void findByAdvancedFilters_whenFilterByEstado_shouldReturnMatchingProducciones() {
        // Arrange
        createAndPersistProduccion("P1", TipoEstadoProduccion.EN_PROCESO, LocalDateTime.now(), "L1", "E1");
        createAndPersistProduccion("P2", TipoEstadoProduccion.FINALIZADA, LocalDateTime.now(), "L2", "E2");
        createAndPersistProduccion("P3", TipoEstadoProduccion.EN_PROCESO, LocalDateTime.now(), "L3", "E3");
        entityManager.flush(); // Forzamos la sincronización con la BD en memoria

        // Act
        List<ProduccionModel> result = produccionRepository.findByAdvancedFilters(
                null, null, null, TipoEstadoProduccion.EN_PROCESO, null, null
        );

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProduccionModel::getCodigoProduccion).containsExactlyInAnyOrder("P1", "P3");
    }
}
