package com.unlu.alimtrack.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenAccessingRecetasPostWithoutToken_thenReturns403() throws Exception {
        // Arrange
        RecetaCreateDTO newReceta = new RecetaCreateDTO(
                "REC-TEST-SEC",
                "Receta para Test de Seguridad",
                "Descripción de la receta",
                "creator@example.com"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/recetas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReceta)))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenAccessingPublicEndpoint_thenReturns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/public/producciones"))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessingProduccionesGet_thenReturns200() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/producciones"))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessingProduccionesPostWithoutToken_thenReturns403() throws Exception {
        // Arrange
        ProduccionCreateDTO newProduccion = new ProduccionCreateDTO(
                "REC-V1",
                "PROD-TEST-SEC",
                "creator@example.com",
                "LOTE-TEST",
                "Encargado Test",
                "Observaciones Test"
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/producciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduccion)))
                .andExpect(status().isForbidden());
    }
}
