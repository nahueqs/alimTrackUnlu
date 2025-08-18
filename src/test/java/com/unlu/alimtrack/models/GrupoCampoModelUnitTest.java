package com.unlu.alimtrack.models;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static com.unlu.alimtrack.enums.TipoSeccion.agrupada;
import static org.junit.jupiter.api.Assertions.*;

class GrupoCampoModelUnitTest {

    @PersistenceContext
    private final EntityManager entityManager;

    public GrupoCampoModelUnitTest(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

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

    @Test
    void testUniqueConstraintViolations() {
        SeccionModel seccion = new SeccionModel();
        seccion.setTitulo("Sección Test");
        seccion.setTipo(agrupada);
        entityManager.persist(seccion);

        GrupoCampoModel grupo1 = new GrupoCampoModel();
        grupo1.setIdSeccion(seccion);
        grupo1.setSubtitulo("Repetido");

        GrupoCampoModel grupo2 = new GrupoCampoModel();
        grupo2.setIdSeccion(seccion);
        grupo2.setSubtitulo("Repetido"); // violará la unique constraint junto con id

        entityManager.persist(grupo1);
        entityManager.persist(grupo2);

        assertThrows(DataIntegrityViolationException.class, () -> {
            entityManager.flush();
        });
    }


}
