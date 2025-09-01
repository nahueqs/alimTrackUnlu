package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.services.VersionRecetaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VersionRecetaControllerUnitTest {
    @Mock
    private VersionRecetaService versionRecetaService;

    @InjectMocks
    private VersionRecetaController versionRecetaController;

    @Test
    void testGetAllVersionRecetas() {
        List<VersionRecetaResponseDTO> listaResponseDTOs = List.of(new VersionRecetaResponseDTO("RTEST-001", "Milanesa", "Nombre test", "Descripcion test", "Id creador", Instant.parse("2025-08-17T00:00:00Z")));
        when(versionRecetaService.findAllVersiones()).thenReturn(listaResponseDTOs);
        ResponseEntity<List<VersionRecetaResponseDTO>> resp = versionRecetaController.getAllVersiones();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
        verify(versionRecetaService).findAllVersiones();
    }

    @Test
    void testSaveVersionRecetaUsuarioNoExiste() {
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO("codPadre", "codVersion", "nombre", "descripcion", "usernameCreador");

        // Simulamos excepción de usuario inexistente
        when(versionRecetaService.saveVersionReceta("codPadre", createDTO)).thenThrow(new RecursoNoEncontradoException("Usuario no encontrado"));

        Exception exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            versionRecetaController.saveVersionReceta("codPadre", createDTO);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(versionRecetaService).saveVersionReceta("codPadre", createDTO);
    }

    @Test
    void testSaveVersionReceta() {
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO("codPadre", "codVersion", "nombre", "descripcion", "usernameCreador");

        VersionRecetaResponseDTO responseDTO = new VersionRecetaResponseDTO("codVersion", "nombrePadre", "nombre", "descripcion", "usernameCreador", Instant.now());

        when(versionRecetaService.saveVersionReceta("codPadre", createDTO)).thenReturn(responseDTO);

        ResponseEntity<VersionRecetaResponseDTO> resp = versionRecetaController.saveVersionReceta("codPadre", createDTO);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("codVersion", resp.getBody().codigoVersionReceta());
        assertEquals("nombre", resp.getBody().nombre());
        verify(versionRecetaService).saveVersionReceta("codPadre", createDTO);
    }


}

