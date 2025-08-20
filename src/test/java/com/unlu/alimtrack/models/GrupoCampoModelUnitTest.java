package com.unlu.alimtrack.models;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.unlu.alimtrack.enums.TipoSeccion;
import static com.unlu.alimtrack.enums.TipoSeccion.agrupada;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@DataJpaTest
@ActiveProfiles("test") // indica que use application-test.properties
class GrupoCampoModelUnitTest {

    @Autowired
    private TestEntityManager entityManager;
    private UsuarioModel usuario;
    private RecetaModel receta;
    private VersionRecetaModel versionReceta;
    private SeccionModel seccion;

    @Test
    void testSettersAndGetters() {
        GrupoCampoModel grupoCampo = new GrupoCampoModel();
        SeccionModel seccion = new SeccionModel();

        grupoCampo.setId(1);
        grupoCampo.setIdSeccion(seccion);
        grupoCampo.setSubtitulo("Subtitulo Test");

        assertEquals(1, grupoCampo.getId());
        assertEquals(seccion, grupoCampo.getIdSeccion());
        assertEquals("Subtitulo Test", grupoCampo.getSubtitulo());
    }

    @BeforeEach
    public void setUp() {
        // Configurar datos de prueba
        usuario = new UsuarioModel();
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setContraseña("password");
        usuario.setEsAdmin(false);
        usuario = entityManager.persist(usuario);

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
        seccion.setTipo(TipoSeccion.simple); // Ajusta según tu enum
        seccion.setIdVersionRecetaPadre(versionReceta);
        seccion.setOrden(1);
        seccion = entityManager.persist(seccion);

        entityManager.flush();
    }

    @Test
    public void testUniqueConstraintViolations() {
        GrupoCampoModel grupo1 = new GrupoCampoModel();
        grupo1.setSubtitulo("Grupo Único");
        grupo1.setIdSeccion(seccion);

        entityManager.persist(grupo1);
        entityManager.flush();

        GrupoCampoModel grupo2 = new GrupoCampoModel();
        grupo2.setSubtitulo("Grupo Único");
        grupo2.setIdSeccion(seccion);

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(grupo2);
            entityManager.flush();
        });
    }

    @Test
    public void testGruposConSubtitulosDiferentes() {
         GrupoCampoModel grupo1 = new GrupoCampoModel();
        grupo1.setSubtitulo("Grupo A");
        grupo1.setIdSeccion(seccion);

        GrupoCampoModel grupo2 = new GrupoCampoModel();
        grupo2.setSubtitulo("Grupo B");
        grupo2.setIdSeccion(seccion);

        entityManager.persist(grupo1);
        entityManager.persist(grupo2);
        entityManager.flush();

        assertNotNull("grupo1 debería tener un ID generado después de persistir", grupo1.getId());
        assertNotNull("grupo2 debería tener un ID generado después de persistir", grupo2.getId());
    }




}
