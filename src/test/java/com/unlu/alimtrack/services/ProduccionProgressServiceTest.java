package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import com.unlu.alimtrack.services.impl.ProduccionProgressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProduccionProgressServiceTest {

    @InjectMocks
    private ProduccionProgressServiceImpl produccionProgressService;

    @Test
    void calcularProgreso_ShouldCalculateCorrectly() {
        // Arrange
        int totalCampos = 10;
        int totalCeldas = 10;
        
        // Simular 5 respuestas de campo válidas
        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(1L);
        campo.setTipoDato(TipoDatoCampo.TEXTO);
        RespuestaCampoModel respCampo = new RespuestaCampoModel();
        respCampo.setIdCampo(campo);
        respCampo.setValorTexto("Valor");
        List<RespuestaCampoModel> respuestasCampos = List.of(respCampo); // 1 respuesta válida

        // Simular 0 respuestas de tabla
        List<RespuestaTablaModel> respuestasTablas = Collections.emptyList();

        // Act
        ProgresoProduccionResponseDTO result = produccionProgressService.calcularProgreso(
                totalCampos, totalCeldas, respuestasCampos, respuestasTablas);

        // Assert
        assertEquals(totalCampos, result.totalCampos());
        assertEquals(1, result.camposRespondidos()); // 1 respondido
        assertEquals(20, result.totalElementos()); // 10 + 10
        assertEquals(1, result.elementosRespondidos()); // 1 + 0
        assertEquals(5.0, result.porcentajeCompletado()); // 1/20 = 5%
    }

    @Test
    void calcularProgreso_ShouldHandleZeroTotals() {
        ProgresoProduccionResponseDTO result = produccionProgressService.calcularProgreso(
                0, 0, Collections.emptyList(), Collections.emptyList());

        assertEquals(0.0, result.porcentajeCompletado());
    }

    @Test
    void calcularProgreso_ShouldThrow_WhenNegativeTotals() {
        assertThrows(IllegalArgumentException.class, () -> 
            produccionProgressService.calcularProgreso(-1, 0, null, null)
        );
    }

    @Test
    void estaProduccionCompleta_ShouldReturnTrue_When100Percent() {
        // Arrange
        int totalCampos = 1;
        int totalCeldas = 0;
        
        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(1L);
        campo.setTipoDato(TipoDatoCampo.TEXTO);
        RespuestaCampoModel respCampo = new RespuestaCampoModel();
        respCampo.setIdCampo(campo);
        respCampo.setValorTexto("Valor");
        
        List<RespuestaCampoModel> respuestasCampos = List.of(respCampo);

        // Act
        boolean completa = produccionProgressService.estaProduccionCompleta(
                totalCampos, totalCeldas, respuestasCampos, Collections.emptyList());

        // Assert
        assertTrue(completa);
    }
}
