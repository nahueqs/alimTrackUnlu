package com.unlu.alimtrack.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionMetadataModifyRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaTablaRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.ProduccionQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "test@test.com", roles = {"OPERADOR"})
public class ProduccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProduccionQueryService produccionQueryService;

    @MockitoBean
    private ProduccionManagementService produccionManagementService;

    @Test
    void getAllProduccionesMetadata_ShouldReturnList() throws Exception {
        ProduccionMetadataResponseDTO produccion = new ProduccionMetadataResponseDTO(
                "PROD-1", "VER-1", "Encargado", "email", "Lote", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );
        when(produccionQueryService.getAllProduccionesMetadata(any())).thenReturn(List.of(produccion));

        mockMvc.perform(get("/api/v1/producciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoProduccion").value("PROD-1"));
    }

    @Test
    void getMetadataByCodigoProduccion_ShouldReturnProduccion() throws Exception {
        ProduccionMetadataResponseDTO produccion = new ProduccionMetadataResponseDTO(
                "PROD-1", "VER-1", "Encargado", "email", "Lote", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );
        when(produccionQueryService.findByCodigoProduccion("PROD-1")).thenReturn(produccion);

        mockMvc.perform(get("/api/v1/producciones/PROD-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoProduccion").value("PROD-1"));
    }

    @Test
    void iniciarProduccion_ShouldReturnCreated() throws Exception {
        ProduccionCreateDTO createDTO = new ProduccionCreateDTO(
                "REC-V1", "PROD-NEW", "creator@example.com", "LOTE-NEW", "Encargado", "Obs"
        );
        ProduccionMetadataResponseDTO created = new ProduccionMetadataResponseDTO(
                "PROD-NEW", "REC-V1", "Encargado", "creator@example.com", "LOTE-NEW", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );
        when(produccionManagementService.iniciarProduccion(any(ProduccionCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/producciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/producciones/PROD-NEW"))
                .andExpect(jsonPath("$.codigoProduccion").value("PROD-NEW"));
    }

    // --- NUEVO TEST: Código duplicado ---
    @Test
    void iniciarProduccion_DuplicateCode_ShouldReturnBadRequest() throws Exception {
        ProduccionCreateDTO createDTO = new ProduccionCreateDTO(
                "REC-V1", "PROD-DUPLICADO", "creator@example.com", "LOTE-NEW", "Encargado", "Obs"
        );

        // Simulamos que el servicio lanza excepción al detectar duplicado
        when(produccionManagementService.iniciarProduccion(any(ProduccionCreateDTO.class)))
                .thenThrow(new RecursoDuplicadoException("El código de producción ya existe"));

        mockMvc.perform(post("/api/v1/producciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void guardarRespuestaCampo_ShouldReturnOk() throws Exception {
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(1L);
        request.setEmailCreador("test@test.com");
        request.setValorTexto("valor");

        RespuestaCampoResponseDTO response = RespuestaCampoResponseDTO.builder()
                .idRespuesta(1L)
                .idCampo(1L)
                .valor("valor")
                .timestamp(LocalDateTime.now())
                .creadoPor("test@test.com")
                .build();

        when(produccionManagementService.guardarRespuestaCampo(eq("PROD-1"), eq(1L), any(RespuestaCampoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/producciones/PROD-1/campos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("valor"));
    }

    // --- NUEVO TEST: Sobreescribir con respuesta vacía ---
    @Test
    void guardarRespuestaCampo_EmptyValue_ShouldReturnOk() throws Exception {
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(1L);
        request.setEmailCreador("test@test.com");
        request.setValorTexto(""); // Valor vacío para "borrar" o actualizar a vacío

        // El servicio debería procesarlo y devolver el DTO con valor vacío/nulo
        RespuestaCampoResponseDTO response = RespuestaCampoResponseDTO.builder()
                .idRespuesta(1L)
                .idCampo(1L)
                .valor("") // Respuesta vacía
                .timestamp(LocalDateTime.now())
                .creadoPor("test@test.com")
                .build();

        when(produccionManagementService.guardarRespuestaCampo(eq("PROD-1"), eq(1L), any(RespuestaCampoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/producciones/PROD-1/campos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(""));
    }

    // --- NUEVO TEST: Tipo de dato inválido en Campo ---
    @Test
    void guardarRespuestaCampo_InvalidType_ShouldReturnBadRequest() throws Exception {
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setIdCampo(1L);
        request.setEmailCreador("test@test.com");
        request.setValorTexto("NO_ES_NUMERO"); // Enviamos texto para un campo que el servicio espera sea numérico

        // Simulamos que el servicio valida el tipo y lanza excepción
        when(produccionManagementService.guardarRespuestaCampo(eq("PROD-1"), eq(1L), any(RespuestaCampoRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("El valor no corresponde al tipo de dato del campo"));

        mockMvc.perform(put("/api/v1/producciones/PROD-1/campos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void guardarRespuestaCeldaTabla_ShouldReturnOk() throws Exception {
        RespuestaTablaRequestDTO request = new RespuestaTablaRequestDTO();
        request.setEmailCreador("test@test.com");
        request.setValorTexto("valor");

        RespuestaCeldaTablaResponseDTO response = new RespuestaCeldaTablaResponseDTO(
                1L, 1L, 1L, "TEXTO", "Fila 1", "Columna 1", "valor", LocalDateTime.now()
        );
        when(produccionManagementService.guardarRespuestaCeldaTabla(eq("PROD-1"), eq(1L), eq(1L), eq(1L), any(RespuestaTablaRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/producciones/PROD-1/tablas/1/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value("valor"));
    }

    // --- NUEVO TEST: Tipo de dato inválido en Tabla ---
    @Test
    void guardarRespuestaCeldaTabla_InvalidType_ShouldReturnBadRequest() throws Exception {
        RespuestaTablaRequestDTO request = new RespuestaTablaRequestDTO();
        request.setEmailCreador("test@test.com");
        request.setValorTexto("TEXTO_INVALIDO"); // Valor inválido para el tipo de columna

        // Simulamos excepción del servicio
        when(produccionManagementService.guardarRespuestaCeldaTabla(eq("PROD-1"), eq(1L), eq(1L), eq(1L), any(RespuestaTablaRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("El valor no corresponde al tipo de dato de la columna"));

        mockMvc.perform(put("/api/v1/producciones/PROD-1/tablas/1/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUltimasRespuestas_ShouldReturnOk() throws Exception {
        UltimasRespuestasProduccionResponseDTO response = new UltimasRespuestasProduccionResponseDTO(
                null, 
                Collections.emptyList(), 
                Collections.emptyList(), 
                null, 
                LocalDateTime.now() 
        );
        when(produccionManagementService.getUltimasRespuestas("PROD-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/producciones/PROD-1/ultimas-respuestas"))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstado_ShouldReturnNoContent() throws Exception {
        ProduccionCambioEstadoRequestDTO request = new ProduccionCambioEstadoRequestDTO(TipoEstadoProduccion.FINALIZADA.toString(), "test@test.com");
        doNothing().when(produccionManagementService).updateEstado(eq("PROD-1"), any(ProduccionCambioEstadoRequestDTO.class));

        mockMvc.perform(put("/api/v1/producciones/PROD-1/cambiar-estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isNoContent());
    }

    @Test
    void updateMetadata_ShouldReturnNoContent() throws Exception {
        ProduccionMetadataModifyRequestDTO request = new ProduccionMetadataModifyRequestDTO("Lote", "Encargado", "Obs");
        doNothing().when(produccionManagementService).updateMetadata(eq("PROD-1"), any(ProduccionMetadataModifyRequestDTO.class));

        mockMvc.perform(put("/api/v1/producciones/PROD-1/metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void getEstadoProduccion_ShouldReturnOk() throws Exception {
        EstadoProduccionPublicoResponseDTO response = new EstadoProduccionPublicoResponseDTO(
                "PROD-1", TipoEstadoProduccion.EN_PROCESO, LocalDateTime.now()
        );
        when(produccionQueryService.getEstadoProduccion("PROD-1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/producciones/PROD-1/estado-actual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoProduccion").value("PROD-1"));
    }

    @Test
    void deleteProduccion_ShouldReturnNoContent() throws Exception {
        doNothing().when(produccionManagementService).deleteProduccion("PROD-1");

        mockMvc.perform(delete("/api/v1/producciones/PROD-1"))
                .andExpect(status().isNoContent());
    }
}
