package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionEstadoPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionMetadataPublicaResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.mappers.PublicMapper;
import com.unlu.alimtrack.services.impl.ProduccionManagementServiceImpl;
import com.unlu.alimtrack.services.impl.PublicRequestsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublicRequestServiceTest {

    @Mock
    private ProduccionQueryService produccionQueryService;

    @Mock
    private ProduccionManagementServiceImpl produccionManagementServiceImpl;

    @Mock
    private PublicMapper publicMapper;

    @InjectMocks
    private PublicRequestsServiceImpl publicRequestService;

    @Test
    void getAllProduccionesPublicas_shouldCallQueryServiceAndMapper() {
        // Arrange
        ProduccionMetadataResponseDTO fullDto = new ProduccionMetadataResponseDTO("PROD-001", "V1", "encargado", "email", "lote", "ESTADO", LocalDateTime.now(), null, null);
        ProduccionMetadataPublicaResponseDTO publicDto = new ProduccionMetadataPublicaResponseDTO("PROD-001", "lote", "ESTADO", LocalDateTime.now(), null);

        when(produccionQueryService.getAllProduccionesMetadata(any(ProduccionFilterRequestDTO.class))).thenReturn(Collections.singletonList(fullDto));
        when(publicMapper.metadataProduccionToPublicDTO(fullDto)).thenReturn(publicDto);

        // Act
        List<ProduccionMetadataPublicaResponseDTO> result = publicRequestService.getAllProduccionesMetadataPublico();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(publicDto);
        verify(produccionQueryService, times(1)).getAllProduccionesMetadata(any(ProduccionFilterRequestDTO.class));
        verify(publicMapper, times(1)).metadataProduccionToPublicDTO(fullDto);
    }


    @Test
    void getProduccionPublic_shouldCallQueryService() {
        // Arrange
        String codigoProduccion = "PROD-001";
        ProduccionEstadoPublicaResponseDTO produccionPublicDTO = new ProduccionEstadoPublicaResponseDTO(codigoProduccion, TipoEstadoProduccion.EN_PROCESO, LocalDateTime.now());

        when(produccionQueryService.getProduccionPublic(codigoProduccion)).thenReturn(produccionPublicDTO);

        // Act
        ProduccionEstadoPublicaResponseDTO result = publicRequestService.getProduccionPublic(codigoProduccion);

        // Assert
        assertThat(result).isEqualTo(produccionPublicDTO);
        verify(produccionQueryService, times(1)).getProduccionPublic(codigoProduccion);
        verifyNoInteractions(produccionManagementServiceImpl, publicMapper); // No deben ser llamados
    }
}
