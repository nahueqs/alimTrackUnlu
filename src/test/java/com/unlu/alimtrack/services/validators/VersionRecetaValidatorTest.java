package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.exceptions.ModificacionInvalidaException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class VersionRecetaValidatorTest {

    @InjectMocks
    private VersionRecetaValidator versionRecetaValidator;

    private ProduccionModel produccion;
    private CampoSimpleModel campo;
    private VersionRecetaModel versionRecetaProduccion;
    private VersionRecetaModel versionRecetaCampo;
    private SeccionModel seccion;

    @BeforeEach
    void setUp() {
        versionRecetaProduccion = new VersionRecetaModel();
        versionRecetaProduccion.setId(1L);

        versionRecetaCampo = new VersionRecetaModel();
        versionRecetaCampo.setId(2L);

        seccion = new SeccionModel();

        produccion = new ProduccionModel();
        produccion.setVersionReceta(versionRecetaProduccion);

        campo = new CampoSimpleModel();
        campo.setSeccion(seccion);
    }

    @Test
    void validarCampoPerteneceAVersion_whenCampoBelongsToVersion_shouldNotThrowException() {
        // Arrange
        seccion.setVersionRecetaPadre(versionRecetaProduccion); // El campo pertenece a la misma versión

        // Act & Assert
        assertThatCode(() -> versionRecetaValidator.validarCampoPerteneceAVersion(produccion, campo))
                .doesNotThrowAnyException();
    }

    @Test
    void validarCampoPerteneceAVersion_whenCampoDoesNotBelongToVersion_shouldThrowModificacionInvalidaException() {
        // Arrange
        seccion.setVersionRecetaPadre(versionRecetaCampo); // El campo pertenece a una versión DIFERENTE

        // Act & Assert
        assertThatThrownBy(() -> versionRecetaValidator.validarCampoPerteneceAVersion(produccion, campo))
                .isInstanceOf(ModificacionInvalidaException.class)
                .hasMessage("El campo no pertenece a la version de la produccion");
    }

    @Test
    void validarCampoPerteneceAVersion_whenProduccionIsNull_shouldThrowRecursoNoEncontradoException() {
        // Act & Assert
        assertThatThrownBy(() -> versionRecetaValidator.validarCampoPerteneceAVersion(null, campo))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Produccion o campo no encontrado");
    }

    @Test
    void validarCampoPerteneceAVersion_whenCampoIsNull_shouldThrowRecursoNoEncontradoException() {
        // Act & Assert
        assertThatThrownBy(() -> versionRecetaValidator.validarCampoPerteneceAVersion(produccion, null))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Produccion o campo no encontrado");
    }
}
