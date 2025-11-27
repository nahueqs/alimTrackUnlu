//package com.unlu.alimtrack.controllers.v1;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.unlu.alimtrack.AlimtrackApplication;
//import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
//import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
//import com.unlu.alimtrack.models.Receta;
//import com.unlu.alimtrack.models.RecetaModel;
//import com.unlu.alimtrack.models.VersionReceta;
//import com.unlu.alimtrack.models.VersionRecetaModel;
//import com.unlu.alimtrack.repositories.RecetaRepository;
//import com.unlu.alimtrack.repositories.VersionRecetaRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest(classes = AlimtrackApplication.class)
//@AutoConfigureMockMvc
//public class VersionRecetaControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private VersionRecetaRepository versionRecetaRepository;
//
//    @Autowired
//    private RecetaRepository recetaRepository;
//
//    private RecetaModel receta;
//    private VersionRecetaModel version1;
//    private VersionRecetaModel version2;
//
//    @BeforeEach
//    void setUp() {
//        versionRecetaRepository.deleteAll();
//        recetaRepository.deleteAll();
//
//        RecetaModel receta = new RecetaModel(1L, "RTEST-001", "Milanesa", null, "Usuario creador", null, null);
//        recetaRepository.save(receta);
//
//        version1 = new VersionRecetaModel(receta, "VER001", "1.0", "Version 1", "Descripcion 1", "Test User");
//        version2 = new VersionRecetaModel(receta, "VER002", "1.1", "Version 2", "Descripcion 2", "Test User");
//        versionRecetaRepository.save(version1);
//        versionRecetaRepository.save(version2);
//    }
//
//    @Test
//    void getAllVersiones_ShouldReturnListOfVersions() throws Exception {
//        mockMvc.perform(get("/api/v1/versiones-receta"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2));
//    }
//
//    @Test
//    void getByCodigoVersion_ShouldReturnVersion() throws Exception {
//        mockMvc.perform(get("/api/v1/versiones-receta/" + version1.getCodigoVersionReceta()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.codigoVersionReceta").value(version1.getCodigoVersionReceta()));
//    }
//
//    @Test
//    void getAllByCodigoReceta_ShouldReturnVersionsForRecipe() throws Exception {
//        mockMvc.perform(get("/api/v1/recetas/" + receta.getCodigoReceta() + "/versiones-receta"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2));
//    }
//
//    @Test
//    void saveVersionReceta_ShouldCreateNewVersion() throws Exception {
//        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO(receta.getCodigoReceta(), "VER003", "Version 3", "Desc 3", "User");
//
//        mockMvc.perform(post("/api/v1/recetas/" + receta.getCodigoReceta() + "/versiones-receta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location", "/api/v1/versiones-receta/VER003"));
//    }
//
//    @Test
//    void updateVersionReceta_ShouldUpdateAndReturnVersion() throws Exception {
//        VersionRecetaModifyDTO modifyDTO = new VersionRecetaModifyDTO("Nuevo Nombre", "Nueva Desc");
//
//        mockMvc.perform(put("/api/v1/versiones-receta/" + version1.getCodigoVersionReceta())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(modifyDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nombre").value("Nuevo Nombre"));
//    }
//
//    @Test
//    void deleteVersionReceta_ShouldDeleteAndReturnNoContent() throws Exception {
//        mockMvc.perform(delete("/api/v1/versiones-receta/" + version1.getCodigoVersionReceta()))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void obtenerEstructuraCompleta_ShouldReturnFullStructure() throws Exception {
//        mockMvc.perform(get("/api/v1/versiones-receta/" + version1.getCodigoVersionReceta() + "/estructura-completa"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.versionRecetaMetadata.codigoVersionReceta").value(version1.getCodigoVersionReceta()));
//    }
//}
