package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduccionQueryServiceTest {

    @Mock
    private ProduccionRepository produccionRepository;
    @Mock
    private ProduccionMapper produccionMapper;
    @Mock
    private ProduccionQueryServiceValidator produccionQueryServiceValidator;

    @InjectMocks
    private ProduccionQueryServiceImpl produccionQueryService;

    @Test
    void findByCodigoProduccion_ShouldReturnDTO_WhenExists() {
        String codigo = "PROD-1";
        ProduccionModel model = new ProduccionModel();
        ProduccionMetadataResponseDTO dto = new ProduccionMetadataResponseDTO(
                codigo, "VER-1", "Encargado", "email", "Lote", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );

        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(model));
        when(produccionMapper.modelToResponseDTO(model)).thenReturn(dto);

        ProduccionMetadataResponseDTO result = produccionQueryService.findByCodigoProduccion(codigo);

        assertNotNull(result);
        assertEquals(codigo, result.codigoProduccion());
    }

    @Test
    void findByCodigoProduccion_ShouldThrow_WhenNotFound() {
        String codigo = "PROD-999";
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> 
            produccionQueryService.findByCodigoProduccion(codigo)
        );
    }

    @Test
    void getAllProduccionesMetadata_ShouldReturnList() {
        ProduccionFilterRequestDTO filtros = new ProduccionFilterRequestDTO(
                null, null, null, null, null, null
        );
        ProduccionModel model = new ProduccionModel();
        ProduccionMetadataResponseDTO dto = new ProduccionMetadataResponseDTO(
                "PROD-1", "VER-1", "Encargado", "email", "Lote", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );

        when(produccionQueryServiceValidator.convertToStartOfDay(any())).thenReturn(LocalDateTime.now());
        when(produccionQueryServiceValidator.convertToEndOfDay(any())).thenReturn(LocalDateTime.now());
        when(produccionRepository.findByAdvancedFilters(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(model));
        when(produccionMapper.modelListToResponseDTOList(any())).thenReturn(List.of(dto));

        List<ProduccionMetadataResponseDTO> result = produccionQueryService.getAllProduccionesMetadata(filtros);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getEstadoProduccion_ShouldReturnDTO_WhenExists() {
        String codigo = "PROD-1";
        EstadoProduccionPublicoResponseDTO dto = new EstadoProduccionPublicoResponseDTO(
                codigo, TipoEstadoProduccion.EN_PROCESO, LocalDateTime.now()
        );

        when(produccionRepository.findProduccionPublicByCodigoProduccion(codigo)).thenReturn(Optional.of(dto));

        EstadoProduccionPublicoResponseDTO result = produccionQueryService.getEstadoProduccion(codigo);

        assertNotNull(result);
        assertEquals(codigo, result.codigoProduccion());
    }

    @Test
    void existsByVersionRecetaPadre_ShouldReturnTrue() {
        String codigoVersion = "VER-1";
        when(produccionRepository.existsByVersionReceta_CodigoVersionReceta(codigoVersion)).thenReturn(true);

        assertTrue(produccionQueryService.existsByVersionRecetaPadre(codigoVersion));
    }
}
