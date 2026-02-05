package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.exceptions.ValidationException;
import com.unlu.alimtrack.mappers.RespuestaCampoMapper;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.CampoSimpleRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
import com.unlu.alimtrack.services.impl.RespuestaCampoServiceImpl;
import com.unlu.alimtrack.services.validators.RespuestaValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RespuestaCampoServiceTest {

    @Mock private RespuestaCampoRepository respuestaCampoRepository;
    @Mock private ProduccionRepository produccionRepository;
    @Mock private CampoSimpleRepository campoSimpleRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private RespuestaCampoMapper respuestaCampoMapper;
    @Mock private RespuestaValidationService validationService;

    @InjectMocks
    private RespuestaCampoServiceImpl respuestaCampoService;

    @Test
    void guardarRespuestaCampo_ShouldSave_WhenValid() {
        // Arrange
        String codigo = "PROD-1";
        Long idCampo = 1L;
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(idCampo);
        request.setEmailCreador("user@test.com");
        request.setValorTexto("valor");

        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(idCampo);
        campo.setTipoDato(TipoDatoCampo.TEXTO);

        ProduccionModel produccion = new ProduccionModel();
        produccion.setCodigoProduccion(codigo);

        UsuarioModel usuario = new UsuarioModel();

        RespuestaCampoModel respuestaModel = new RespuestaCampoModel();
        RespuestaCampoResponseDTO responseDTO = RespuestaCampoResponseDTO.builder().valor("valor").build();

        when(campoSimpleRepository.findById(idCampo)).thenReturn(Optional.of(campo));
        when(usuarioService.getUsuarioModelByEmail("user@test.com")).thenReturn(usuario);
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(produccion));
        when(respuestaCampoRepository.findTopByIdProduccionAndIdCampoOrderByTimestampDesc(produccion, campo))
                .thenReturn(Optional.empty()); // No existe previa
        
        when(respuestaCampoRepository.save(any(RespuestaCampoModel.class))).thenReturn(respuestaModel);
        when(respuestaCampoMapper.toResponseDTO(respuestaModel)).thenReturn(responseDTO);

        // Act
        RespuestaCampoResponseDTO result = respuestaCampoService.guardarRespuestaCampo(codigo, idCampo, request);

        // Assert
        assertNotNull(result);
        verify(validationService).validarRespuesta(eq(TipoDatoCampo.TEXTO), any(), any(), any(), any());
        verify(respuestaCampoRepository).save(any(RespuestaCampoModel.class));
    }

    @Test
    void guardarRespuestaCampo_ShouldThrow_WhenIdMismatch() {
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(99L);
        request.setEmailCreador("user@test.com");

        assertThrows(ValidationException.class, () -> 
            respuestaCampoService.guardarRespuestaCampo("PROD-1", 1L, request)
        );
    }

    @Test
    void guardarRespuestaCampo_ShouldHandleEmptyValue() {
        // Arrange
        String codigo = "PROD-1";
        Long idCampo = 1L;
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(idCampo);
        request.setEmailCreador("user@test.com");
        request.setValorTexto(""); // Valor vacío

        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(idCampo);
        campo.setTipoDato(TipoDatoCampo.TEXTO);

        when(campoSimpleRepository.findById(idCampo)).thenReturn(Optional.of(campo));
        when(usuarioService.getUsuarioModelByEmail(any())).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(new ProduccionModel()));
        when(respuestaCampoRepository.findTopByIdProduccionAndIdCampoOrderByTimestampDesc(any(), any()))
                .thenReturn(Optional.empty());
        
        when(respuestaCampoRepository.save(any(RespuestaCampoModel.class))).thenReturn(new RespuestaCampoModel());
        when(respuestaCampoMapper.toResponseDTO(any())).thenReturn(RespuestaCampoResponseDTO.builder().valor("").build());

        // Act
        RespuestaCampoResponseDTO result = respuestaCampoService.guardarRespuestaCampo(codigo, idCampo, request);

        // Assert
        assertNotNull(result);
        verify(validationService).validarRespuesta(eq(TipoDatoCampo.TEXTO), any(), any(), any(), any());
    }

    @Test
    void guardarRespuestaCampo_ShouldThrow_WhenInvalidType_Numeric() {
        // Arrange
        Long idCampo = 1L;
        String codigo = "PROD-1";
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(idCampo);
        request.setEmailCreador("user@test.com");
        request.setValorTexto("NO_NUMERO"); // Texto en campo numérico

        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(idCampo);
        campo.setTipoDato(TipoDatoCampo.ENTERO);

        // Mocks necesarios para que el flujo llegue a la validación
        when(campoSimpleRepository.findById(idCampo)).thenReturn(Optional.of(campo));
        when(usuarioService.getUsuarioModelByEmail(any())).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(new ProduccionModel()));
        when(respuestaCampoRepository.findTopByIdProduccionAndIdCampoOrderByTimestampDesc(any(), any()))
                .thenReturn(Optional.empty());
        
        // Simulamos que el validador lanza excepción
        doThrow(new IllegalArgumentException("Valor inválido")).when(validationService)
                .validarRespuesta(eq(TipoDatoCampo.ENTERO), any(), any(), any(), any());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            respuestaCampoService.guardarRespuestaCampo(codigo, idCampo, request)
        );
    }

    @Test
    void guardarRespuestaCampo_ShouldProcess_Boolean() {
        // Arrange
        Long idCampo = 1L;
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(idCampo);
        request.setEmailCreador("user@test.com");
        request.setValorBooleano(true);

        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(idCampo);
        campo.setTipoDato(TipoDatoCampo.BOOLEANO);

        when(campoSimpleRepository.findById(idCampo)).thenReturn(Optional.of(campo));
        when(usuarioService.getUsuarioModelByEmail(any())).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(any())).thenReturn(Optional.of(new ProduccionModel()));
        when(respuestaCampoRepository.findTopByIdProduccionAndIdCampoOrderByTimestampDesc(any(), any()))
                .thenReturn(Optional.empty());
        
        when(respuestaCampoRepository.save(any(RespuestaCampoModel.class))).thenReturn(new RespuestaCampoModel());
        when(respuestaCampoMapper.toResponseDTO(any())).thenReturn(RespuestaCampoResponseDTO.builder().valor("true").build());

        // Act
        respuestaCampoService.guardarRespuestaCampo("PROD-1", idCampo, request);

        // Assert
        verify(validationService).validarRespuesta(eq(TipoDatoCampo.BOOLEANO), any(), any(), any(), eq(true));
    }

    @Test
    void guardarRespuestaCampo_ShouldProcess_Date() {
        // Arrange
        Long idCampo = 1L;
        LocalDateTime now = LocalDateTime.now();
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(idCampo);
        request.setEmailCreador("user@test.com");
        request.setValorFecha(now);

        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(idCampo);
        campo.setTipoDato(TipoDatoCampo.FECHA);

        when(campoSimpleRepository.findById(idCampo)).thenReturn(Optional.of(campo));
        when(usuarioService.getUsuarioModelByEmail(any())).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(any())).thenReturn(Optional.of(new ProduccionModel()));
        when(respuestaCampoRepository.findTopByIdProduccionAndIdCampoOrderByTimestampDesc(any(), any()))
                .thenReturn(Optional.empty());
        
        when(respuestaCampoRepository.save(any(RespuestaCampoModel.class))).thenReturn(new RespuestaCampoModel());
        when(respuestaCampoMapper.toResponseDTO(any())).thenReturn(RespuestaCampoResponseDTO.builder().valor(now.toString()).build());

        // Act
        respuestaCampoService.guardarRespuestaCampo("PROD-1", idCampo, request);

        // Assert
        verify(validationService).validarRespuesta(eq(TipoDatoCampo.FECHA), any(), any(), eq(now), any());
    }
}
