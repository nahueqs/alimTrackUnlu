package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.services.VersionRecetaService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VersionRecetaControllerUnitTest {
    @Mock
    private VersionRecetaService versionRecetaService;

    @InjectMocks
    private VersionRecetaController versionRecetaController;

    public VersionRecetaControllerUnitTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllVersionRecetas() {
        List<VersionRecetaResponseDTO> listaResponseDTOs = List.of(new VersionRecetaResponseDTO("RTEST-001", "Milanesa", "Nombre test", "Descripcion test", "Id creador", Instant.parse("2025-08-17T00:00:00Z")));
        when(versionRecetaService.findAllVersiones()).thenReturn(listaResponseDTOs);
        ResponseEntity<List<VersionRecetaResponseDTO>> resp = versionRecetaController.getAllVersiones();
        assertEquals(1, resp.getBody().size());
        verify(versionRecetaService).findAllVersiones();
    }

    @Test
    void testGetVersionByIdRecetaNoExiste() {
        when(versionRecetaService.findVersionRecetaByIdRecetaAndIdVersion(999L, 1L))
                .thenThrow(new RecursoNoEncontradoException("Receta no encontrada"));

        Exception exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            versionRecetaController.getVersionById(999L, 1L);
        });

        assertEquals("Receta no encontrada", exception.getMessage());
        verify(versionRecetaService).findVersionRecetaByIdRecetaAndIdVersion(999L, 1L);
    }

    @Test
    void testSaveVersionRecetaUsuarioNoExiste() {
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO("CVRTEST-001", "Nombre", "Descripcion", 1L);

        // Simulamos excepción de usuario inexistente
        when(versionRecetaService.saveVersionReceta(1L, createDTO))
                .thenThrow(new RecursoNoEncontradoException("Usuario no encontrado"));

        Exception exception = assertThrows(RecursoNoEncontradoException.class, () -> {
            versionRecetaController.saveVersionReceta(1L, createDTO);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(versionRecetaService).saveVersionReceta(1L, createDTO);
    }

    @Test
    void testGetVersionesByIdRecetaPadreSinVersiones() {
        // Simulamos que devuelve lista vacía
        when(versionRecetaService.findAllVersionesByIdRecetaPadre(2L))
                .thenReturn(List.of());

        ResponseEntity<List<VersionRecetaResponseDTO>> resp =
                versionRecetaController.getVersionesByIdRecetaPadre(2L);

        assertEquals(0, resp.getBody().size());
        verify(versionRecetaService).findAllVersionesByIdRecetaPadre(2L);
    }

    @Test
    void testGetVersionById() {
        VersionRecetaResponseDTO dto = new VersionRecetaResponseDTO(
                "VTEST-001", "Receta Padre", "Nombre Version", "Descripcion test", "Creador", Instant.now()
        );

        when(versionRecetaService.findVersionRecetaByIdRecetaAndIdVersion(1L, 1L)).thenReturn(dto);

        ResponseEntity<VersionRecetaResponseDTO> resp = versionRecetaController.getVersionById(1L, 1L);

        assertEquals("VTEST-001", resp.getBody().codigoVersionReceta());
        assertEquals("Nombre Version", resp.getBody().nombre());
        verify(versionRecetaService).findVersionRecetaByIdRecetaAndIdVersion(1L, 1L);
    }

    @Test
    void testGetVersionesByIdRecetaPadre() {
        VersionRecetaResponseDTO dto1 = new VersionRecetaResponseDTO(
                "VTEST-001", "Receta Padre", "Version 1", "Desc 1", "Creador", Instant.now()
        );
        VersionRecetaResponseDTO dto2 = new VersionRecetaResponseDTO(
                "VTEST-002", "Receta Padre", "Version 2", "Desc 2", "Creador", Instant.now()
        );

        List<VersionRecetaResponseDTO> list = List.of(dto1, dto2);
        when(versionRecetaService.findAllVersionesByIdRecetaPadre(1L)).thenReturn(list);

        ResponseEntity<List<VersionRecetaResponseDTO>> resp = versionRecetaController.getVersionesByIdRecetaPadre(1L);

        assertEquals(2, resp.getBody().size());
        assertEquals("VTEST-001", resp.getBody().get(0).codigoVersionReceta());
        assertEquals("VTEST-002", resp.getBody().get(1).codigoVersionReceta());
        verify(versionRecetaService).findAllVersionesByIdRecetaPadre(1L);
    }

    @Test
    void testSaveVersionReceta() {
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO("CVRTEST-001", "Nombre", "Descripcion", 1L);

        VersionRecetaResponseDTO responseDTO = new VersionRecetaResponseDTO(
                "CVRTEST-001", "Receta Padre", "Nombre Version", "Descripcion test", "Creador", Instant.now()
        );

        when(versionRecetaService.saveVersionReceta(1L, createDTO)).thenReturn(responseDTO);

        ResponseEntity<VersionRecetaResponseDTO> resp = versionRecetaController.saveVersionReceta(1L, createDTO);

        assertEquals("CVRTEST-001", resp.getBody().codigoVersionReceta());
        assertEquals("Nombre Version", resp.getBody().nombre());
        verify(versionRecetaService).saveVersionReceta(1L, createDTO);
    }


}

