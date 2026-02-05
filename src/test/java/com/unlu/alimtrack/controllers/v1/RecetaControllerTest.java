package com.unlu.alimtrack.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.Receta.RecetaMetadataResponseDTO;
import com.unlu.alimtrack.services.RecetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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
public class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RecetaService recetaService;

    @Test
    void getAllRecetas_ShouldReturnList() throws Exception {
        RecetaMetadataResponseDTO receta = new RecetaMetadataResponseDTO(
                "REC-1", "Descripción", "Nombre", "user@test.com", LocalDateTime.now()
        );
        when(recetaService.findAllRecetas()).thenReturn(List.of(receta));

        mockMvc.perform(get("/api/v1/recetas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoReceta").value("REC-1"));
    }

    @Test
    void getReceta_ShouldReturnReceta() throws Exception {
        RecetaMetadataResponseDTO receta = new RecetaMetadataResponseDTO(
                "REC-1", "Descripción", "Nombre", "user@test.com", LocalDateTime.now()
        );
        when(recetaService.findReceta("REC-1")).thenReturn(receta);

        mockMvc.perform(get("/api/v1/recetas/REC-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoReceta").value("REC-1"));
    }

    @Test
    void addReceta_ShouldReturnCreated() throws Exception {
        RecetaCreateDTO createDTO = new RecetaCreateDTO(
                "REC-NEW", "Nombre", "Descripción", "user@test.com"
        );
        RecetaMetadataResponseDTO created = new RecetaMetadataResponseDTO(
                "REC-NEW", "Descripción", "Nombre", "user@test.com", LocalDateTime.now()
        );
        when(recetaService.addReceta(any(RecetaCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/recetas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/recetas/REC-NEW"))
                .andExpect(jsonPath("$.codigoReceta").value("REC-NEW"));
    }

    @Test
    void updateReceta_ShouldReturnUpdated() throws Exception {
        RecetaModifyDTO modifyDTO = new RecetaModifyDTO("Nuevo Nombre", "Nueva Desc");
        RecetaMetadataResponseDTO updated = new RecetaMetadataResponseDTO(
                "REC-1", "Nueva Desc", "Nuevo Nombre", "user@test.com", LocalDateTime.now()
        );
        when(recetaService.updateReceta(eq("REC-1"), any(RecetaModifyDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/recetas/REC-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo Nombre"));
    }

    @Test
    void deleteReceta_ShouldReturnNoContent() throws Exception {
        doNothing().when(recetaService).deleteReceta("REC-1");

        mockMvc.perform(delete("/api/v1/recetas/REC-1"))
                .andExpect(status().isNoContent());
    }
}
