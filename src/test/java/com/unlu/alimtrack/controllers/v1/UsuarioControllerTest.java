package com.unlu.alimtrack.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
@WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void getAllUsuarios_ShouldReturnList() throws Exception {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO("test@test.com", "Test User", "OPERADOR");
        when(usuarioService.getAllUsuarios()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@test.com"));
    }

    @Test
    void getUsuarioByEmail_ShouldReturnUsuario() throws Exception {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO("test@test.com", "Test User", "OPERADOR");
        when(usuarioService.getUsuarioByEmail("test@test.com")).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void addUsuario_ShouldReturnCreated() throws Exception {
        UsuarioCreateDTO createDTO = new UsuarioCreateDTO("New User", "new@test.com", "password");
        UsuarioResponseDTO created = new UsuarioResponseDTO("new@test.com", "New User", "OPERADOR");
        
        when(usuarioService.addUsuario(any(UsuarioCreateDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/usuarios/new@test.com"))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void modifyUsuario_ShouldReturnNoContent() throws Exception {
        UsuarioModifyDTO modifyDTO = new UsuarioModifyDTO("Updated Name", "newpassword123", "test@test.com");
        doNothing().when(usuarioService).modifyUsuario(eq("test@test.com"), any(UsuarioModifyDTO.class));

        mockMvc.perform(put("/api/v1/usuarios/test@test.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUsuario_ShouldReturnNoContent() throws Exception {
        doNothing().when(usuarioService).deleteUsuario("test@test.com");

        mockMvc.perform(delete("/api/v1/usuarios/test@test.com"))
                .andExpect(status().isNoContent());
    }
}
