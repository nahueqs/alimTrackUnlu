package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VersionRecetaControllerUnitTest {


    @Mock
    private VersionRecetaMetadataService versionRecetaMetadataService;

    @InjectMocks
    private VersionRecetaController versionRecetaController;

    @Test
    void testGetAllVersionRecetas() {
        List<VersionRecetaMetadataResponseDTO> listaResponseDTOs = List.of(
                new VersionRecetaMetadataResponseDTO(
                        "RTEST-001",
                        "COD_1",
                        "Nombre receta padre test",
                        "Nombre version test",
                        "Descripcion test",
                        "Id creador",
                        LocalDateTime.parse("2025-08-17T00:00:00"),
                        10,
                        10,
                        10
                )
        );
        when(versionRecetaMetadataService.findAllVersiones()).thenReturn(listaResponseDTOs);
        ResponseEntity<List<VersionRecetaMetadataResponseDTO>> resp = versionRecetaController.getAllVersiones();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(1, resp.getBody().size());
        verify(versionRecetaMetadataService).findAllVersiones();
    }

    @Test
    void testSaveVersionRecetaUsuarioNoExiste() {
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO(
                "codPadre",
                "codVersion",
                "nombre",
                "descripcion",
                "usernameCreador");

        // Simulamos excepción de usuario inexistente
        when(versionRecetaMetadataService.saveVersionReceta("codPadre", createDTO)).thenThrow(
                new RecursoNoEncontradoException("Usuario no encontrado"));

        Exception exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            versionRecetaController.saveVersionReceta("codPadre", createDTO);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(versionRecetaMetadataService).saveVersionReceta("codPadre", createDTO);
    }

    @Test
    void testSaveVersionReceta() {
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO(
                "codPadre",
                "codVersion",
                "Nombre version",
                "descripcion",
                "usernameCreador");

        VersionRecetaMetadataResponseDTO responseDTO = new VersionRecetaMetadataResponseDTO(
                "codVersion",
                "codrPadre",
                "nombre",
                "Nombre version",
                "descripcion",
                "usernameCreador",
                LocalDateTime.now(),
                10,
                10,
                10
        );

        when(versionRecetaMetadataService.saveVersionReceta("codPadre", createDTO)).thenReturn(responseDTO);

        ResponseEntity<VersionRecetaMetadataResponseDTO> resp = versionRecetaController.saveVersionReceta("codPadre", createDTO);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals("codVersion", resp.getBody().codigoVersionReceta());
        assertEquals("nombre", resp.getBody().nombreRecetaPadre());
        assertEquals("Nombre version", resp.getBody().nombre());
        assertEquals("descripcion", resp.getBody().descripcion());
        assertEquals("usernameCreador", resp.getBody().creadaPor());
        verify(versionRecetaMetadataService).saveVersionReceta("codPadre", createDTO);
    }


}

