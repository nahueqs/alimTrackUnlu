package com.unlu.alimtrack.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.services.PublicRequestsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicRequestsService publicRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducciones_shouldReturnPublicDTOListAndOk() throws Exception {
        // Arrange
        MetadataProduccionPublicaResponseDTO produccion1 = new MetadataProduccionPublicaResponseDTO("PROD-001", "V1", "LOTE-A", "EN_PROCESO", LocalDateTime.now(), null, null);
        MetadataProduccionPublicaResponseDTO produccion2 = new MetadataProduccionPublicaResponseDTO("PROD-002", "V1", "LOTE-B", "FINALIZADO", LocalDateTime.now().minusDays(1), LocalDateTime.now(), LocalDateTime.now().minusDays(1));
        List<MetadataProduccionPublicaResponseDTO> producciones = Arrays.asList(produccion1, produccion2);

        given(publicRequestService.getAllProduccionesMetadataPublico()).willReturn(producciones);

        // Act & Assert
        mockMvc.perform(get("/api/v1/public/producciones"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].codigoProduccion", is("PROD-001")))
                .andExpect(jsonPath("$[0].lote", is("LOTE-A")))
                .andExpect(jsonPath("$[0].encargado").doesNotExist())
                .andExpect(jsonPath("$[0].emailCreador").doesNotExist());
    }
//
//    @Test
//    void obtenerEstadoActual_shouldReturnPublicEstadoDTOAndOkPublico() throws Exception {
//        // Arrange
//        String codigoProduccion = "PROD-001";
//        ProduccionMetadataPublicaResponseDTO produccionPublica = new ProduccionMetadataPublicaResponseDTO(codigoProduccion, "LOTE-A", "EN_PROCESO", LocalDateTime.now(), null);
//        List<RespuestaCampoResponseDTO> respuestasCampo = new ArrayList<>();
//        List<RespuestaCeldaTablaResponseDTO> respuestasTabla = new ArrayList<>();
//        ProgresoProduccionResponseDTO progreso = new ProgresoProduccionResponseDTO(
//                0,
//                0,
//                0,
//                0,
//                0,
//                0,
//                0
//        );
//        LocalDateTime fechaActual = LocalDateTime.now();
//
//        UltimasRespuestasProduccionResponseDTO respuestas = new UltimasRespuestasProduccionResponseDTO(
//                produccionPublica,
//                respuestasCampo,
//                respuestasTabla,
//                progreso,
//                fechaActual
//        );
//
//
//        given(publicRequestService.getEstadoActualProduccionPublico(codigoProduccion)).willReturn(respuestas);
//
//        // Act & Assert
//        mockMvc.perform(get("/api/v1/public/producciones/{codigoProduccion}/estado-actual", codigoProduccion))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.produccion.codigoProduccion", is(codigoProduccion)))
//                .andExpect(jsonPath("$.produccion.encargado").doesNotExist())
//                .andExpect(jsonPath("$.produccion.emailCreador").doesNotExist());
//    }

    @Test
    void getProduccionPublic_shouldReturnProduccionPublicDTOAndOk() throws Exception {
        // Arrange
        String codigoProduccion = "PROD-001";
        LocalDateTime now = LocalDateTime.now();
        EstadoProduccionPublicoResponseDTO produccionPublicDTO = new EstadoProduccionPublicoResponseDTO(codigoProduccion, TipoEstadoProduccion.EN_PROCESO, now);

        given(publicRequestService.getProduccionPublic(codigoProduccion)).willReturn(produccionPublicDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/public/producciones/{codigoProduccion}", codigoProduccion))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.codigoProduccion", is(codigoProduccion)))
                .andExpect(jsonPath("$.estado", is("EN_PROCESO")))
                .andExpect(jsonPath("$.fechaUltimaModificacion").exists());
    }
}
