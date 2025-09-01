package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.services.RecetaService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RecetaControllerUnitTest {

    @Mock
    private RecetaService recetaService;
    @InjectMocks
    private RecetaController recetaController;

    public RecetaControllerUnitTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRecetas() {
        List<RecetaResponseDTO> list = List.of(new RecetaResponseDTO("RTEST-001", "Milanesa", "Descripcion test", Instant.now(), "Id creador"));
        when(recetaService.findAllRecetas()).thenReturn(list);
        ResponseEntity<List<RecetaResponseDTO>> resp = recetaController.getAllRecetas();
        assertEquals(1, resp.getBody().size());
        verify(recetaService).findAllRecetas();
    }

   /* @Test
    void testAddReceta() {
        RecetaCreateDTO testCreateDTO = new RecetaCreateDTO("RTEST-001", "Tarta", "Desc test", 1L);
        RecetaResponseDTO testResponseDTO = new RecetaResponseDTO("RTEST-001", "Tarta", "Desc test", null, "1");
        when(recetaService.addReceta(testCreateDTO)).thenReturn(testResponseDTO);
        ResponseEntity<RecetaResponseDTO> resp = recetaController.addReceta(testCreateDTO);
        assertEquals("RTEST-001", resp.getBody().codigoReceta().toString());
        verify(recetaService).addReceta(testCreateDTO);
    }
*/
    @Test
    void testUpdateReceta() {
        RecetaModifyDTO testModifyDTO = new RecetaModifyDTO("Milanesa Napolitana", "desc");
        RecetaResponseDTO testResponseDTO = new RecetaResponseDTO("COD-001", "Milanesa Napolitana", "desc", Instant.now(),  "Id creador");
        when(recetaService.updateReceta("1", testModifyDTO)).thenReturn(testResponseDTO);
        ResponseEntity<RecetaResponseDTO> resp = recetaController.updateReceta("1", testModifyDTO);
        assertEquals("Milanesa Napolitana", resp.getBody().nombre());
        verify(recetaService).updateReceta("1", testModifyDTO);
    }
/*
    @Test
    void testDeleteReceta() {
        doNothing().when(recetaService).deleteRecetaByID(1L);
        ResponseEntity<Void> resp = recetaController.deleteRecetaById(1L);
        assertEquals(204, resp.getStatusCodeValue());
        verify(recetaService).deleteRecetaByID(1L);
    }

 */
}
