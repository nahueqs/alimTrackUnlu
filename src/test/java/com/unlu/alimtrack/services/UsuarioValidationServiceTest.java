package com.unlu.alimtrack.services;

import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.services.impl.UsuarioValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioValidationServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioValidationServiceImpl usuarioValidationService;

    @Test
    void validarUsuarioAutorizado_ShouldReturnUser_WhenExistsAndActive() {
        String email = "user@test.com";
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail(email);
        usuario.setEstaActivo(true);

        when(usuarioService.getUsuarioModelByEmail(email)).thenReturn(usuario);

        UsuarioModel result = usuarioValidationService.validarUsuarioAutorizado(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void validarUsuarioAutorizado_ShouldThrow_WhenUserNotFound() {
        String email = "unknown@test.com";
        when(usuarioService.getUsuarioModelByEmail(email)).thenThrow(new RecursoNoEncontradoException("User not found"));

        assertThrows(RecursoNoEncontradoException.class, () -> 
            usuarioValidationService.validarUsuarioAutorizado(email)
        );
    }

    @Test
    void validarUsuarioAutorizado_ShouldThrow_WhenUserInactive() {
        String email = "inactive@test.com";
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail(email);
        usuario.setEstaActivo(false);

        when(usuarioService.getUsuarioModelByEmail(email)).thenReturn(usuario);

        assertThrows(OperacionNoPermitida.class, () -> 
            usuarioValidationService.validarUsuarioAutorizado(email)
        );
    }

    @Test
    void validarUsuarioActivo_ShouldReturnUser_WhenActive() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEstaActivo(true);

        UsuarioModel result = usuarioValidationService.validarUsuarioActivo(usuario);

        assertEquals(usuario, result);
    }

    @Test
    void validarUsuarioActivo_ShouldThrow_WhenInactive() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEstaActivo(false);

        assertThrows(OperacionNoPermitida.class, () -> 
            usuarioValidationService.validarUsuarioActivo(usuario)
        );
    }

    @Test
    void validarUsuarioActivo_ShouldThrow_WhenNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            usuarioValidationService.validarUsuarioActivo(null)
        );
    }
}
