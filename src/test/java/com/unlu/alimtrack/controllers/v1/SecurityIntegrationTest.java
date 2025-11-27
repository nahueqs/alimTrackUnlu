package com.unlu.alimtrack.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles; // Import added
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Added this line
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenAccessingProtectedEndpointWithoutToken_thenReturns403() throws Exception {
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
}
