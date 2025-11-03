package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.UsuarioResponseDTO;
import com.unlu.alimtrack.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;


    @Test
    public void testGetAllUsuarios() {
        List<UsuarioResponseDTO> usuarios = List.of(
                new UsuarioResponseDTO(
                        "Username1",
                        "Nombre1",
                        "Email1",
                        "OPERADOR"
                )
        );
        when(usuarioService.getAllUsuarios()).thenReturn(usuarios);
        ResponseEntity<List<UsuarioResponseDTO>> response = usuarioController.getAllUsuarios();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(usuarioService).getAllUsuarios();
        verifyNoMoreInteractions(usuarioService);
        assertEquals("Username1", response.getBody().get(0).username());
        assertEquals("Nombre1", response.getBody().get(0).nombre());
        assertEquals("Email1", response.getBody().get(0).email());
    }

    @Test
    public void testGetUsuarioByEmail() {
        UsuarioResponseDTO usuario = new UsuarioResponseDTO(
                "Username1",
                "Nombre1",
                "Email1",
                "OPERADOR"
        );

        when(usuarioService.getUsuarioByUsername("Username1")).thenReturn(usuario);
        ResponseEntity<UsuarioResponseDTO> response = usuarioController.getUsuarioByUsername("Username1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usuarioService).getUsuarioByUsername("Username1");
        assertEquals("Username1", response.getBody().username());
        assertEquals("Nombre1", response.getBody().nombre());
        assertEquals("Email1", response.getBody().email());
        verifyNoMoreInteractions(usuarioService);
    }


    @Test
    public void testSaveUsuario() {
        UsuarioCreateDTO nuevoUsuario = new UsuarioCreateDTO(
                "Username1",
                "Nombre1",
                "Email1",
                "Password1"
        );

        UsuarioResponseDTO nuevoUsuarioResponse = new UsuarioResponseDTO(
                "Username1",
                "Nombre1",
                "Email1",
                "OPERADOR"
        );

        when(usuarioService.addUsuario(nuevoUsuario)).thenReturn(nuevoUsuarioResponse);
        ResponseEntity<UsuarioResponseDTO> response = usuarioController.addUsuario(nuevoUsuario);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Username1", response.getBody().username());
        assertEquals("Nombre1", response.getBody().nombre());
        assertEquals("Email1", response.getBody().email());
        verify(usuarioService).addUsuario(nuevoUsuario);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    public void testUpdateUsuarioUsername() {
        String usernameActual = "Username1";
        String nuevoUsername = "NuevoUsername";

        UsuarioModifyDTO requestCambioUsername = new UsuarioModifyDTO(
                nuevoUsername,
                null,
                null,
                null
        );

        doNothing().when(usuarioService).modifyUsuario(usernameActual, requestCambioUsername);

        ResponseEntity<Void> response = usuarioController.modifyUsuario(usernameActual, requestCambioUsername);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(usuarioService).modifyUsuario(usernameActual, requestCambioUsername);
        verifyNoMoreInteractions(usuarioService);
    }

    @Test
    public void testDeleteUsuario() {
        String username = "Username1";
        doNothing().when(usuarioService).deleteUsuario(username);
        ResponseEntity<Void> response = usuarioController.deleteUsuario(username);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(usuarioService).deleteUsuario(username);
        verifyNoMoreInteractions(usuarioService);
    }

//    @Test
//    public void testGetUsuariosByTipoRolUsuario() {
//        usuarioController.getUsuariosByTipoRolUsuario(TipoRolUsuario.ADMIN);
//        verify(usuarioService).getUsuariosByTipoRolUsuario(TipoRolUsuario.ADMIN);
//    }

//  @Test
//  public void testGetUsuarioByEmailAndPassword() {
//
//  }

}
