package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.impl.ProduccionQueryServiceImpl;
import com.unlu.alimtrack.services.validators.ProduccionQueryServiceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProduccionQueryServiceTest {

    @Mock
    private ProduccionRepository produccionRepository;

    @Mock
    private ProduccionMapper produccionMapper;

    @Mock
    private ProduccionQueryServiceValidator produccionQueryServiceValidator;

    @InjectMocks
    private ProduccionQueryServiceImpl produccionQueryService;

    @Test
    void findByCodigoProduccion_whenFound_shouldReturnDTO() {
        // Arrange
        String codigo = "PROD-001";
        ProduccionModel model = new ProduccionModel();
        ProduccionMetadataResponseDTO dto = mock(ProduccionMetadataResponseDTO.class);

        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(model));
        when(produccionMapper.modelToResponseDTO(model)).thenReturn(dto);

        // Act
        ProduccionMetadataResponseDTO result = produccionQueryService.findByCodigoProduccion(codigo);

        // Assert
        assertThat(result).isEqualTo(dto);
        verify(produccionRepository).findByCodigoProduccion(codigo);
        verify(produccionMapper).modelToResponseDTO(model);
    }

    @Test
    void findByCodigoProduccion_whenNotFound_shouldThrowException() {
        // Arrange
        String codigo = "PROD-999";
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produccionQueryService.findByCodigoProduccion(codigo))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void getAllProduccionesMetadata_shouldCallValidatorAndRepositoryWithCorrectParams() {
        // Arrange
        ProduccionFilterRequestDTO filtros = new ProduccionFilterRequestDTO(
                "V-001",
                "LOTE-A",
                "encargado",
                LocalDate.now(),
                LocalDate.now(),
                "EN_PROCESO"
        );

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23);

        when(produccionQueryServiceValidator.convertToStartOfDay(filtros.fechaInicio())).thenReturn(startOfDay);
        when(produccionQueryServiceValidator.convertToEndOfDay(filtros.fechaFin())).thenReturn(endOfDay);
        when(produccionRepository.findByAdvancedFilters(any(), any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(produccionMapper.modelListToResponseDTOList(any())).thenReturn(Collections.emptyList());

        // Act
        List<ProduccionMetadataResponseDTO> result = produccionQueryService.getAllProduccionesMetadata(filtros);

        // Assert
        assertThat(result).isNotNull();
        verify(produccionQueryServiceValidator).convertToStartOfDay(filtros.fechaInicio());
        verify(produccionQueryServiceValidator).convertToEndOfDay(filtros.fechaFin());
        verify(produccionRepository).findByAdvancedFilters(
                eq(filtros.codigoVersionReceta()),
                eq(filtros.lote()),
                eq(filtros.encargado()),
                any(), // El enum se convierte internamente
                eq(startOfDay),
                eq(endOfDay)
        );
    }
}
