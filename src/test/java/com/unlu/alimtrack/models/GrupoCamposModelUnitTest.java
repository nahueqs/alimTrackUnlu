package com.unlu.alimtrack.models;

import com.unlu.alimtrack.enums.TipoRolUsuario;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GrupoCamposModelUnitTest {

    @Autowired
    private TestEntityManager entityManager;
    private UsuarioModel usuario;
    private RecetaModel receta;
    private VersionRecetaModel versionReceta;
    private SeccionModel seccion;

    @Test
    void testSettersAndGetters() {
        GrupoCamposModel grupoCampo = new GrupoCamposModel();
        SeccionModel seccion = new SeccionModel();

        grupoCampo.setId(1L);
        grupoCampo.setSeccion(seccion);
        grupoCampo.setSubtitulo("Subtitulo Test");

        assertEquals(1, grupoCampo.getId());
        assertEquals(seccion, grupoCampo.getSeccion());
        assertEquals("Subtitulo Test", grupoCampo.getSubtitulo());
    }

    @BeforeEach
    public void setUp() {
        // Clear any existing data
        entityManager.clear();
        
        // Create a unique username and email for each test run
        String uniqueId = String.valueOf(System.currentTimeMillis());
        
        // Configurar datos de prueba
        usuario = new UsuarioModel();
        usuario.setNombre("Test User " + uniqueId);
        usuario.setUsername("testuser_" + uniqueId);
        usuario.setEmail("test_" + uniqueId + "@example.com");
        usuario.setPassword("password123");
        usuario.setRol(TipoRolUsuario.USUARIO);
        usuario = entityManager.persist(usuario);
        entityManager.flush();

        receta = new RecetaModel();
        receta.setNombre("Receta Test");
        receta.setCodigoReceta("REC-TEST-001");
        receta.setCreadoPor(usuario);
        receta = entityManager.persist(receta);

        versionReceta = new VersionRecetaModel();
        versionReceta.setNombre("Versión Test");
        versionReceta.setCodigoVersionReceta("VER-TEST-001");
        versionReceta.setRecetaPadre(receta);
        versionReceta.setCreadoPor(usuario);
        versionReceta = entityManager.persist(versionReceta);

        seccion = new SeccionModel();
        seccion.setTitulo("Sección Test");
        seccion.setVersionRecetaPadre(versionReceta);
        seccion.setOrden(1);
        seccion = entityManager.persist(seccion);

        entityManager.flush();
    }

    @Test
    public void testUniqueConstraintViolations() {
        GrupoCamposModel grupo1 = new GrupoCamposModel();
        grupo1.setSubtitulo("Grupo Único");
        grupo1.setSeccion(seccion);

        entityManager.persist(grupo1);
        entityManager.flush();

        GrupoCamposModel grupo2 = new GrupoCamposModel();
        grupo2.setSubtitulo("Grupo Único");
        grupo2.setSeccion(seccion);

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(grupo2);
            entityManager.flush();
        });
    }

    @Test
    public void testGruposConSubtitulosDiferentes() {
         GrupoCamposModel grupo1 = new GrupoCamposModel();
        grupo1.setSubtitulo("Grupo A");
        grupo1.setSeccion(seccion);

        GrupoCamposModel grupo2 = new GrupoCamposModel();
        grupo2.setSubtitulo("Grupo B");
        grupo2.setSeccion(seccion);

        entityManager.persist(grupo1);
        entityManager.persist(grupo2);
        entityManager.flush();

        assertNotNull("grupo1 debería tener un ID generado después de persistir", grupo1.getId());
        assertNotNull("grupo2 debería tener un ID generado después de persistir", grupo2.getId());
    }




}
