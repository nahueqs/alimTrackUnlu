//package com.unlu.alimtrack.controllers.v1;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.unlu.alimtrack.AlimtrackApplication;
//import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
//import com.unlu.alimtrack.enums.TipoRolUsuario;
//import com.unlu.alimtrack.models.UsuarioModel;
//import com.unlu.alimtrack.repositories.UsuarioRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest(classes = AlimtrackApplication.class)
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class UsuarioControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UsuarioRepository usuarioRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    private static final String USUARIOS_ENDPOINT = "/api/v1/usuarios";
//
//    private UsuarioModel usuarioAdmin;
//    private String userEmail;
//    private UsuarioCreateDTO usuarioCreateDTO;
//
//    @BeforeEach
//    void setUp() {
//        usuarioRepository.deleteAll();
//
//        this.userEmail = "test@example.com";
//        String encodedPassword = passwordEncoder.encode("pass1"); // 🔑 Usar PasswordEncoder
//        this.usuarioAdmin = UsuarioModel.builder()
//
//                .nombre("Nombre")
//                .email(userEmail)
//                .estaActivo(true)
//                .password(encodedPassword)
//                .rol(TipoRolUsuario.ADMIN)
//                .build();
//
//        usuarioRepository.save(this.usuarioAdmin);
//
//        this.usuarioCreateDTO = new UsuarioCreateDTO(
//                "nombre",
//                userEmail,
//                "pass1"
//        );
//
//
//    }
//
//    @Test
//    @DisplayName("Test para obtener todos los usuarios")
//    @WithMockUser(roles = "ADMIN")
//    public void testGetAllUsuarios200() throws Exception {
//
//        mockMvc.perform(get(USUARIOS_ENDPOINT))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].email").value(this.userEmail));
//    }
//
//
/// /
/// /    @Test
/// /    @DisplayName("Test para obtener un usuario por su email")
/// /    @WithMockUser(roles = "ADMIN")
/// /    public void testGetUsuarioByEmail() throws Exception {
/// /
/// /        mockMvc.perform(get(USUARIOS_ENDPOINT + "/" + userEmail))
/// /                .andExpect(status().isOk())
/// /                .andExpect(jsonPath("$.email").value(userEmail));
/// /    }
/// /
/// /    @Test
/// /    @DisplayName("Test para crear un nuevo usuario")
/// /    @WithAnonymousUser
/// /    public void testAddUsuario() throws Exception {
/// /
/// /        mockMvc.perform(post(USUARIOS_ENDPOINT)
/// /                        .contentType(MediaType.APPLICATION_JSON)
/// /                        .content(objectMapper.writeValueAsString(usuario)))
/// /                .andExpect(status().isCreated())
/// /                .andExpect(header().string("Location", USUARIOS_ENDPOINT + "/" + userEmail))
/// /                .andExpect(jsonPath("$.email").value(userEmail));
/// /    }
//
////    @Test
////    @DisplayName("Test para modificar un usuario")
////    @WithMockUser(roles = "ADMIN")
////    public void testModifyUsuario() throws Exception {
////
////        UsuarioModifyDTO modificacion = new UsuarioModifyDTO("Nuevo Nombre", null, null);
////
////        mockMvc.perform(put(USUARIOS_ENDPOINT + "/" + userEmail)
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(modificacion)))
////                .andExpect(status().isNoContent());
////    }
//
////    @Test
////    @DisplayName("Test para eliminar un usuario")
////    @WithMockUser(roles = "ADMIN")
////    public void testDeleteUsuario() throws Exception {
////
////
////        usuarioRepository.save(usuario);
////
////        mockMvc.perform(delete(USUARIOS_ENDPOINT + "/" + userEmail))
////                .andExpect(status().isNoContent());
////    }
//}
