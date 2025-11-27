//package com.unlu.alimtrack.controllers.v1;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.unlu.alimtrack.AlimtrackApplication;
//import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
//import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
//import com.unlu.alimtrack.models.Receta;
//import com.unlu.alimtrack.repositories.RecetaRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithAnonymousUser;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest(classes = AlimtrackApplication.class)
//@AutoConfigureMockMvc
//public class RecetaControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private RecetaRepository recetaRepository;
//
//    @BeforeEach
//    public void setUp() {
//        recetaRepository.deleteAll();
//    }
//
//    @Test
//    public void testGetAllRecetas() throws Exception {
//        Receta receta = new Receta("RTEST-001", "Milanesa", "Nombre creador", "Usuario creador");
//        recetaRepository.save(receta);
//
//        mockMvc.perform(get("/api/v1/recetas"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].codigoReceta").value("RTEST-001"));
//    }
//
//    @Test
//    public void testGetReceta() throws Exception {
//        Receta receta = new Receta("RTEST-001", "Milanesa", "Nombre receta", "Usuario creador");
//        recetaRepository.save(receta);
//
//        mockMvc.perform(get("/api/v1/recetas/RTEST-001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.codigoReceta").value("RTEST-001"))
//                .andExpect(jsonPath("$.descripcion").value("Milanesa"));
//    }
//
//    @Test
//    public void testUpdateReceta() throws Exception {
//        Receta receta = new Receta("RTEST-001", "Descripcion original", "Nombre original", "Creador");
//        recetaRepository.save(receta);
//
//        RecetaModifyDTO modificacion = new RecetaModifyDTO("Nombre modificado", "Descripcion actualizada");
//
//        mockMvc.perform(put("/api/v1/recetas/RTEST-001")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(modificacion)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nombre").value("Nombre modificado"))
//                .andExpect(jsonPath("$.descripcion").value("Descripcion actualizada"));
//    }
//
//    @Test
//    @WithAnonymousUser
//    public void testAddReceta() throws Exception {
//        RecetaCreateDTO nuevaReceta = new RecetaCreateDTO(
//                "RTEST-002", "Nombre receta", "Descripción", "Usuario Test"
//        );
//
//        mockMvc.perform(post("/api/v1/recetas/RTEST-002")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(nuevaReceta)))
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location", "/api/v1/recetas/RTEST-002"))
//                .andExpect(jsonPath("$.codigoReceta").value("RTEST-002"));
//    }
//
//    @Test
//    @WithMockUser
//    public void testDeleteReceta() throws Exception {
//        Receta receta = new Receta("RTEST-001", "Milanesa", "Nombre creador", "Usuario creador");
//        recetaRepository.save(receta);
//
//        mockMvc.perform(delete("/api/v1/recetas/RTEST-001"))
//                .andExpect(status().isNoContent());
//    }
//}
