package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaTablaRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.exceptions.ValidationException;
import com.unlu.alimtrack.mappers.RespuestaTablaMapper;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.*;
import com.unlu.alimtrack.services.impl.RespuestaTablaServiceImpl;
import com.unlu.alimtrack.services.validators.RespuestaValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RespuestaTablaServiceTest {

    @Mock private RespuestaTablaRepository respuestaTablaRepository;
    @Mock private ProduccionRepository produccionRepository;
    @Mock private TablaRepository tablaRepository;
    @Mock private FilaTablaRepository filaTablaRepository;
    @Mock private ColumnaTablaRepository columnaTablaRepository;
    @Mock private UsuarioService usuarioService;
    @Mock private RespuestaTablaMapper respuestaTablaMapper;
    @Mock private RespuestaValidationService validationService;

    @InjectMocks
    private RespuestaTablaServiceImpl respuestaTablaService;

    @Test
    void guardarRespuestaTabla_ShouldSave_WhenValid() {
        // Arrange
        String codigo = "PROD-1";
        Long idTabla = 10L;
        Long idFila = 1L;
        Long idColumna = 2L;
        RespuestaTablaRequestDTO request = new RespuestaTablaRequestDTO();
        request.setEmailCreador("user@test.com");
        request.setValorTexto("valor");

        TablaModel tabla = new TablaModel();
        tabla.setId(idTabla);
        
        FilaTablaModel fila = new FilaTablaModel();
        fila.setId(idFila);
        fila.setTabla(tabla);
        fila.setNombre("Fila 1");
        
        ColumnaTablaModel columna = new ColumnaTablaModel();
        columna.setId(idColumna);
        columna.setTabla(tabla);
        columna.setTipoDato(TipoDatoCampo.TEXTO);
        columna.setNombre("Columna 1");

        ProduccionModel produccion = new ProduccionModel();
        produccion.setCodigoProduccion(codigo);

        RespuestaTablaModel respuestaModel = new RespuestaTablaModel();
        respuestaModel.setTimestamp(LocalDateTime.now());
        respuestaModel.setValorTexto("valor"); // IMPORTANTE: Setear el valor en el modelo mockeado

        when(usuarioService.getUsuarioModelByEmail("user@test.com")).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(produccion));
        when(tablaRepository.findById(idTabla)).thenReturn(Optional.of(tabla));
        when(filaTablaRepository.findById(idFila)).thenReturn(Optional.of(fila));
        when(columnaTablaRepository.findById(idColumna)).thenReturn(Optional.of(columna));
        
        when(respuestaTablaRepository.findByProduccionAndTablaIdAndFilaIdAndColumnaId(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        
        when(respuestaTablaRepository.save(any(RespuestaTablaModel.class))).thenReturn(respuestaModel);

        // Act
        RespuestaCeldaTablaResponseDTO result = respuestaTablaService.guardarRespuestaTabla(
                codigo, idTabla, idFila, idColumna, request);

        // Assert
        assertNotNull(result);
        assertEquals("valor", result.valor());
        verify(respuestaTablaRepository).save(any(RespuestaTablaModel.class));
    }

    @Test
    void guardarRespuestaTabla_ShouldThrow_WhenInconsistentRelations() {
        // Arrange
        Long idTabla = 10L;
        Long idColumna = 2L;
        RespuestaTablaRequestDTO request = new RespuestaTablaRequestDTO();
        request.setEmailCreador("user@test.com");

        TablaModel tabla = new TablaModel();
        tabla.setId(idTabla);
        
        TablaModel otraTabla = new TablaModel();
        otraTabla.setId(99L);
        
        ColumnaTablaModel columna = new ColumnaTablaModel();
        columna.setId(idColumna);
        columna.setTabla(otraTabla); // Columna pertenece a otra tabla

        when(usuarioService.getUsuarioModelByEmail(any())).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(any())).thenReturn(Optional.of(new ProduccionModel()));
        when(tablaRepository.findById(idTabla)).thenReturn(Optional.of(tabla));
        when(filaTablaRepository.findById(any())).thenReturn(Optional.of(new FilaTablaModel()));
        when(columnaTablaRepository.findById(idColumna)).thenReturn(Optional.of(columna));

        // Act & Assert
        assertThrows(ValidationException.class, () -> 
            respuestaTablaService.guardarRespuestaTabla("PROD-1", idTabla, 1L, idColumna, request)
        );
    }

    @Test
    void guardarRespuestaTabla_ShouldHandleEmptyValue() {
        // Arrange
        Long idTabla = 10L;
        RespuestaTablaRequestDTO request = new RespuestaTablaRequestDTO();
        request.setEmailCreador("user@test.com");
        request.setValorTexto(""); // Vacío

        TablaModel tabla = new TablaModel();
        tabla.setId(idTabla);
        
        FilaTablaModel fila = new FilaTablaModel();
        fila.setTabla(tabla);
        
        ColumnaTablaModel columna = new ColumnaTablaModel();
        columna.setTabla(tabla);
        columna.setTipoDato(TipoDatoCampo.TEXTO);

        when(usuarioService.getUsuarioModelByEmail(any())).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(any())).thenReturn(Optional.of(new ProduccionModel()));
        when(tablaRepository.findById(any())).thenReturn(Optional.of(tabla));
        when(filaTablaRepository.findById(any())).thenReturn(Optional.of(fila));
        when(columnaTablaRepository.findById(any())).thenReturn(Optional.of(columna));
        
        when(respuestaTablaRepository.save(any(RespuestaTablaModel.class))).thenReturn(new RespuestaTablaModel());

        // Act
        respuestaTablaService.guardarRespuestaTabla("PROD-1", idTabla, 1L, 1L, request);

        // Assert
        verify(validationService).validarRespuesta(eq(TipoDatoCampo.TEXTO), any(), any(), any(), any());
    }

    @Test
    void guardarRespuestaTabla_ShouldThrow_WhenInvalidType() {
        // Arrange
        Long idTabla = 10L;
        RespuestaTablaRequestDTO request = new RespuestaTablaRequestDTO();
        request.setEmailCreador("user@test.com");
        request.setValorTexto("NO_NUMERO");

        TablaModel tabla = new TablaModel();
        tabla.setId(idTabla);
        
        FilaTablaModel fila = new FilaTablaModel();
        fila.setTabla(tabla);
        
        ColumnaTablaModel columna = new ColumnaTablaModel();
        columna.setTabla(tabla);
        columna.setTipoDato(TipoDatoCampo.ENTERO);

        when(usuarioService.getUsuarioModelByEmail(any())).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(any())).thenReturn(Optional.of(new ProduccionModel()));
        when(tablaRepository.findById(any())).thenReturn(Optional.of(tabla));
        when(filaTablaRepository.findById(any())).thenReturn(Optional.of(fila));
        when(columnaTablaRepository.findById(any())).thenReturn(Optional.of(columna));

        doThrow(new IllegalArgumentException("Valor inválido")).when(validationService)
                .validarRespuesta(eq(TipoDatoCampo.ENTERO), any(), any(), any(), any());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            respuestaTablaService.guardarRespuestaTabla("PROD-1", idTabla, 1L, 1L, request)
        );
    }
}
