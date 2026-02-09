package com.unlu.alimtrack.services;

import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.services.impl.UsuarioValidationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioValidationServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UsuarioValidationServiceImpl usuarioValidationService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void validarUsuarioAutorizado_ShouldReturnUser_WhenExistsAndActiveAndAuthenticated() {
        String email = "user@test.com";
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEmail(email);
        usuario.setEstaActivo(true);

        // Mockear autenticación exitosa
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(authentication.getName()).thenReturn(email);

        when(usuarioService.getUsuarioModelByEmail(email)).thenReturn(usuario);

        UsuarioModel result = usuarioValidationService.validarUsuarioAutorizado(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void validarUsuarioAutorizado_ShouldThrow_WhenUserNotFound() {
        String email = "unknown@test.com";
        
        // Mockear autenticación exitosa para pasar la primera validación
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(authentication.getName()).thenReturn(email);

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

        // Mockear autenticación exitosa
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(authentication.getName()).thenReturn(email);

        when(usuarioService.getUsuarioModelByEmail(email)).thenReturn(usuario);

        assertThrows(OperacionNoPermitida.class, () -> 
            usuarioValidationService.validarUsuarioAutorizado(email)
        );
    }

    @Test
    void validarUsuarioAutorizado_ShouldThrow_WhenNotAuthenticated() {
        String email = "user@test.com";
        
        // Mockear falta de autenticación
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(OperacionNoPermitida.class, () -> 
            usuarioValidationService.validarUsuarioAutorizado(email)
        );
    }

    @Test
    void validarUsuarioAutorizado_ShouldThrow_WhenEmailMismatch() {
        String email = "user@test.com";
        String otherEmail = "other@test.com";
        
        // Mockear autenticación con otro usuario
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(authentication.getName()).thenReturn(otherEmail);

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
