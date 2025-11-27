package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.CampoSimpleRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.impl.UsuarioServiceImpl;
import com.unlu.alimtrack.services.ProduccionQueryService;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.ProductionManagerServiceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductionManagerServiceValidatorTest {

    @Mock
    private ProduccionQueryService produccionQueryService;
    @Mock
    private VersionRecetaQueryService versionRecetaQueryService;
    @Mock
    private CampoSimpleRepository campoSimpleRepository;
    @Mock
    private ProduccionRepository produccionRepository;
    @Mock
    private UsuarioServiceImpl usuarioServiceImpl;

    @InjectMocks
    private ProductionManagerServiceValidator validator;

    @Test
    void verificarCreacionProduccion_whenCodigoExists_shouldThrowRecursoDuplicado() {
        // Arrange
        ProduccionCreateDTO dto = new ProduccionCreateDTO("V1", "PROD-001", "user@test.com", "L1", "E1", "O1");
        when(produccionQueryService.existsByCodigoProduccion("PROD-001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> validator.verificarCreacionProduccion(dto))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void verificarCreacionProduccion_whenVersionNotExists_shouldThrowRecursoNoEncontrado() {
        // Arrange
        ProduccionCreateDTO dto = new ProduccionCreateDTO("V-NON-EXIST", "PROD-001", "user@test.com", "L1", "E1", "O1");
        when(produccionQueryService.existsByCodigoProduccion("PROD-001")).thenReturn(false);
        when(versionRecetaQueryService.existsByCodigoVersion("V-NON-EXIST")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.verificarCreacionProduccion(dto))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("La versión de receta especificada no existe");
    }

    @Test
    void verificarCreacionProduccion_whenUserNotExists_shouldThrowRecursoNoEncontrado() {
        // Arrange
        ProduccionCreateDTO dto = new ProduccionCreateDTO("V1", "PROD-001", "user@test.com", "L1", "E1", "O1");
        when(produccionQueryService.existsByCodigoProduccion("PROD-001")).thenReturn(false);
        when(versionRecetaQueryService.existsByCodigoVersion("V1")).thenReturn(true);
        when(usuarioServiceImpl.existsByEmail("user@test.com")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.verificarCreacionProduccion(dto))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("El usuario creador especificado no existe");
    }

    @Test
    void verificarCreacionProduccion_whenUserIsInactive_shouldThrowOperacionNoPermitida() {
        // Arrange
        ProduccionCreateDTO dto = new ProduccionCreateDTO("V1", "PROD-001", "user@test.com", "L1", "E1", "O1");
        when(produccionQueryService.existsByCodigoProduccion("PROD-001")).thenReturn(false);
        when(versionRecetaQueryService.existsByCodigoVersion("V1")).thenReturn(true);
        when(usuarioServiceImpl.existsByEmail("user@test.com")).thenReturn(true);
        when(usuarioServiceImpl.estaActivoByEmail("user@test.com")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.verificarCreacionProduccion(dto))
                .isInstanceOf(OperacionNoPermitida.class)
                .hasMessageContaining("El usuario creador especificado se encuentra inactivo");
    }

    @Test
    void verificarCreacionProduccion_whenAllIsValid_shouldNotThrowException() {
        // Arrange
        ProduccionCreateDTO dto = new ProduccionCreateDTO("V1", "PROD-001", "user@test.com", "L1", "E1", "O1");
        when(produccionQueryService.existsByCodigoProduccion("PROD-001")).thenReturn(false);
        when(versionRecetaQueryService.existsByCodigoVersion("V1")).thenReturn(true);
        when(usuarioServiceImpl.existsByEmail("user@test.com")).thenReturn(true);
        when(usuarioServiceImpl.estaActivoByEmail("user@test.com")).thenReturn(true);

        // Act & Assert
        validator.verificarCreacionProduccion(dto); // Should complete without exception
    }

    @Test
    void validarProduccionParaEdicion_whenNotInProceso_shouldThrowOperacionNoPermitida() {
        // Arrange
        ProduccionModel produccion = new ProduccionModel();
        produccion.setEstado(TipoEstadoProduccion.FINALIZADA);
        when(produccionRepository.findByCodigoProduccion("PROD-001")).thenReturn(Optional.of(produccion));

        // Act & Assert
        assertThatThrownBy(() -> validator.validarProduccionParaEdicion("PROD-001"))
                .isInstanceOf(OperacionNoPermitida.class);
    }

    @Test
    void validarProduccionParaEdicion_whenInProceso_shouldReturnProduccion() {
        // Arrange
        ProduccionModel produccion = new ProduccionModel();
        produccion.setEstado(TipoEstadoProduccion.EN_PROCESO);
        when(produccionRepository.findByCodigoProduccion("PROD-001")).thenReturn(Optional.of(produccion));

        // Act
        ProduccionModel result = validator.validarProduccionParaEdicion("PROD-001");

        // Assert
        assertThat(result).isEqualTo(produccion);
    }
}
