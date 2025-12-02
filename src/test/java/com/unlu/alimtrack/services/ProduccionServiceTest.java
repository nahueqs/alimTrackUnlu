package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.impl.ProduccionQueryServiceImpl;
import com.unlu.alimtrack.services.validators.ProduccionQueryServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProduccionServiceTest {

    @Mock
    private ProduccionRepository produccionRepository;

    @Mock
    private ProduccionMapper produccionMapper;

    @Mock
    private ProduccionQueryServiceValidator produccionQueryServiceValidator;

    @InjectMocks
    private ProduccionQueryServiceImpl produccionQueryService;

    private ProduccionModel produccionModel;
    private ProduccionMetadataResponseDTO produccionMetadataResponseDTO;

    @BeforeEach
    void setUp() {
        String codigoProduccion = "PROD-001";
        String codigoVersionReceta = "VR-001";
        String lote = "LOTE-001";
        String encargado = "Juan Perez";
        String email = "email@test.com";
        LocalDateTime fecha = LocalDateTime.of(2025, 1, 1, 10, 0);

        VersionRecetaModel versionReceta = new VersionRecetaModel();
        versionReceta.setCodigoVersionReceta(codigoVersionReceta);

        produccionModel = new ProduccionModel();
        produccionModel.setCodigoProduccion(codigoProduccion);
        produccionModel.setLote(lote);
        produccionModel.setEncargado(encargado);
        produccionModel.setEstado(TipoEstadoProduccion.EN_PROCESO);
        produccionModel.setFechaInicio(fecha);
        produccionModel.setVersionReceta(versionReceta);

        produccionMetadataResponseDTO = new ProduccionMetadataResponseDTO(
                codigoProduccion,
                codigoVersionReceta,
                encargado,
                email,
                lote,
                "EN_PROCESO",
                fecha,
                null,
                null,
                "Observaciones"
        );
    }

    @Test
    @DisplayName("Test para encontrar una producción por su código exitosamente")
    void testFindByCodigoProduccion_Success() {
        // Arrange
        when(produccionRepository.findByCodigoProduccion(produccionModel.getCodigoProduccion()))
                .thenReturn(Optional.of(produccionModel));
        when(produccionMapper.modelToResponseDTO(produccionModel)).thenReturn(produccionMetadataResponseDTO);

        // Act
        ProduccionMetadataResponseDTO result = produccionQueryService.findByCodigoProduccion(produccionModel.getCodigoProduccion());

        // Assert
        assertNotNull(result);
        assertEquals(produccionMetadataResponseDTO.codigoProduccion(), result.codigoProduccion());
        assertEquals(produccionMetadataResponseDTO.lote(), result.lote());
        verify(produccionRepository).findByCodigoProduccion(produccionModel.getCodigoProduccion());
        verify(produccionMapper).modelToResponseDTO(produccionModel);
    }

    @Test
    @DisplayName("Test para lanzar excepción cuando no se encuentra una producción por su código")
    void testFindByCodigoProduccion_NotFound() {
        // Arrange
        String codigoInexistente = "CODIGO_INEXISTENTE";
        when(produccionRepository.findByCodigoProduccion(codigoInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            produccionQueryService.findByCodigoProduccion(codigoInexistente);
        });

        verify(produccionRepository).findByCodigoProduccion(codigoInexistente);
        verify(produccionMapper, never()).modelToResponseDTO(any());
    }

    @Test
    @DisplayName("Test para encontrar producciones usando filtros")
    void testFindAllByFilters() {
        // Arrange
        ProduccionFilterRequestDTO filtros = new ProduccionFilterRequestDTO(
                "VR-001", "LOTE-001", "Juan Perez",
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), "EN_PROCESO"
        );
        List<ProduccionModel> produccionModelList = Collections.singletonList(produccionModel);
        List<ProduccionMetadataResponseDTO> produccionMetadataResponseDTOList = Collections.singletonList(produccionMetadataResponseDTO);

        when(produccionQueryServiceValidator.convertToStartOfDay(any())).thenReturn(LocalDateTime.MIN);
        when(produccionQueryServiceValidator.convertToEndOfDay(any())).thenReturn(LocalDateTime.MAX);
        when(produccionRepository.findByAdvancedFilters(any(), any(), any(), any(), any(), any()))
                .thenReturn(produccionModelList);
        when(produccionMapper.modelListToResponseDTOList(produccionModelList)).thenReturn(produccionMetadataResponseDTOList);

        // Act
        List<ProduccionMetadataResponseDTO> result = produccionQueryService.getAllProduccionesMetadata(filtros);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(produccionMetadataResponseDTO.codigoProduccion(), result.get(0).codigoProduccion());
        verify(produccionRepository).findByAdvancedFilters(any(), any(), any(), any(), any(), any());
        verify(produccionMapper).modelListToResponseDTOList(produccionModelList);
    }
}
