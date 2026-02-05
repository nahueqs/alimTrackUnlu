package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestasProduccionPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.mappers.PublicMapper;
import com.unlu.alimtrack.services.impl.PublicRequestsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicRequestsServiceTest {

    @Mock private ProduccionQueryService produccionQueryService;
    @Mock private ProduccionManagementService produccionManagementService;
    @Mock private VersionRecetaEstructuraService versionRecetaEstructuraService;
    @Mock private PublicMapper publicMapper;

    @InjectMocks
    private PublicRequestsServiceImpl publicRequestsService;

    @Test
    void getAllProduccionesMetadataPublico_ShouldReturnList() {
        ProduccionMetadataResponseDTO dto = new ProduccionMetadataResponseDTO(
                "PROD-1", "VER-1", "Encargado", "email", "Lote", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );
        
        MetadataProduccionPublicaResponseDTO publicDto = new MetadataProduccionPublicaResponseDTO(
                "PROD-1", "VER-1", "Lote", "EN_PROCESO", LocalDateTime.now(), null, LocalDateTime.now()
        );

        when(produccionQueryService.getAllProduccionesMetadata(any())).thenReturn(List.of(dto));
        when(publicMapper.metadataProduccionToPublicDTO(dto)).thenReturn(publicDto);

        List<MetadataProduccionPublicaResponseDTO> result = publicRequestsService.getAllProduccionesMetadataPublico();

        assertFalse(result.isEmpty());
        assertEquals("PROD-1", result.get(0).codigoProduccion());
    }

    @Test
    void getEstadoActualProduccionPublico_ShouldReturnRespuestas() {
        String codigo = "PROD-1";
        UltimasRespuestasProduccionResponseDTO respuestas = new UltimasRespuestasProduccionResponseDTO(
                null, Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now()
        );
        
        RespuestasProduccionPublicResponseDTO publicRespuestas = new RespuestasProduccionPublicResponseDTO(
                null, Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now()
        );

        when(produccionManagementService.getUltimasRespuestas(codigo)).thenReturn(respuestas);
        when(publicMapper.respuestasToPublicDTO(respuestas)).thenReturn(publicRespuestas);

        RespuestasProduccionPublicResponseDTO result = publicRequestsService.getEstadoActualProduccionPublico(codigo);

        assertNotNull(result);
    }

    @Test
    void getProduccionPublic_ShouldReturnEstado() {
        String codigo = "PROD-1";
        EstadoProduccionPublicoResponseDTO estado = new EstadoProduccionPublicoResponseDTO(
                codigo, TipoEstadoProduccion.EN_PROCESO, LocalDateTime.now()
        );

        when(produccionQueryService.getEstadoProduccion(codigo)).thenReturn(estado);

        EstadoProduccionPublicoResponseDTO result = publicRequestsService.getProduccionPublic(codigo);

        assertNotNull(result);
        assertEquals(codigo, result.codigoProduccion());
    }

    @Test
    void getEstructuraProduccion_ShouldReturnEstructura() {
        String codigo = "PROD-1";
        String version = "VER-1";
        ProduccionMetadataResponseDTO produccion = new ProduccionMetadataResponseDTO(
                codigo, version, "Encargado", "email", "Lote", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );
        
        VersionEstructuraPublicResponseDTO estructura = new VersionEstructuraPublicResponseDTO(
                null, Collections.emptyList(), 0, 0
        );

        when(produccionQueryService.findByCodigoProduccion(codigo)).thenReturn(produccion);
        when(versionRecetaEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(version)).thenReturn(estructura);

        VersionEstructuraPublicResponseDTO result = publicRequestsService.getEstructuraProduccion(codigo);

        assertNotNull(result);
    }
}
