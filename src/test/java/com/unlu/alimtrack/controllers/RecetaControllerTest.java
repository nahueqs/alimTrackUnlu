package com.unlu.alimtrack.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.unlu.alimtrack.services.RecetaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class RecetaControllerTest {

    @Mock
    private RecetaService recetaService;

    @InjectMocks
    private RecetaController recetaController;

    @Test
    public void testGetAllRecetas() {
      List<RecetaResponseDTO> listaResponseDTOs = List.of (
          new RecetaResponseDTO(
              "RTEST-001",
              "Milanesa",
              "Nombre test",
              LocalDateTime.parse("2025-08-17T00:00:00"),
              "Nombre creador"
          )
      );

      when(recetaService.findAllRecetas()).thenReturn(listaResponseDTOs);

      ResponseEntity<List<RecetaResponseDTO>> resp = recetaController.getAllRecetas();

      assertEquals(HttpStatus.OK, resp.getStatusCode());
      assertEquals(1, resp.getBody().size());
      verify(recetaService).findAllRecetas();
    }

    @Test
    public void testGetReceta() {
      RecetaResponseDTO responseDTO = new RecetaResponseDTO(
          "RTEST-001",
          "Milanesa",
          "Nombre test",
          LocalDateTime.parse("2025-08-17T00:00:00"),
          "Nombre creador");

      when(recetaService.findReceta("RTEST-001")).thenReturn(responseDTO);

      ResponseEntity<RecetaResponseDTO> resp = recetaController.getReceta("RTEST-001");

      assertEquals(HttpStatus.OK, resp.getStatusCode());
      assertEquals("RTEST-001", resp.getBody().codigoReceta());
      assertEquals("Milanesa", resp.getBody().nombre());
      assertEquals("Nombre test", resp.getBody().descripcion());
      assertEquals(LocalDateTime.parse("2025-08-17T00:00:00"), resp.getBody().fechaCreacion());
      assertEquals("Nombre creador", resp.getBody().creadaPor());
      verify(recetaService).findReceta("RTEST-001");

    }

    @Test
    public void testUpdateRecetaNombreReceta() {

      LocalDateTime fechaFija = LocalDateTime.of(2025, 9, 30, 0, 0);

      RecetaModifyDTO modificacion = new RecetaModifyDTO(
          "Nombre modificado",
          null
      );

      RecetaResponseDTO respuesta = new RecetaResponseDTO(
          "RTEST-001",
          "Nombre modificado",
          "Descripcion receta",
          fechaFija,
          "Nombre creador"
      );

      when(recetaService.updateReceta("RTEST-001", modificacion)).thenReturn(respuesta);

      ResponseEntity<RecetaResponseDTO> resp = recetaController.updateReceta("RTEST-001", modificacion);

      assertEquals(HttpStatus.OK, resp.getStatusCode());
      assertEquals("RTEST-001", resp.getBody().codigoReceta());
      assertEquals("Nombre modificado", resp.getBody().nombre());
      assertEquals("Descripcion receta", resp.getBody().descripcion());
      assertEquals(fechaFija, resp.getBody().fechaCreacion());
      assertEquals("Nombre creador", resp.getBody().creadaPor());
      verify(recetaService).updateReceta("RTEST-001", modificacion);

    }

    @Test
    public void testAddReceta() {
      LocalDateTime fechaFija = LocalDateTime.of(2025, 9, 30, 0, 0);

      RecetaCreateDTO nuevaReceta = new RecetaCreateDTO(
          "RTEST-002",
          "Nombre receta",
          "Descripción de la nueva receta",
          "Usuario Test"
      );

      RecetaResponseDTO respuestaEsperada = new RecetaResponseDTO(
          "RTEST-002",
          "Nombre receta",
          "Descripción de la nueva receta",
          fechaFija,
          "Usuario Test"
      );

      when(recetaService.addReceta(nuevaReceta)).thenReturn(respuestaEsperada);

      ResponseEntity<RecetaResponseDTO> respuesta = recetaController.addReceta("RTEST-002", nuevaReceta);

      assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
      assertEquals("/recipes/" + "RTEST-002",
          respuesta.getHeaders().getFirst("Location"));
      assertEquals("RTEST-002", respuesta.getBody().codigoReceta());
      assertEquals("Nombre receta", respuesta.getBody().nombre());
      assertEquals("Descripción de la nueva receta", respuesta.getBody().descripcion());
      assertEquals(fechaFija, respuesta.getBody().fechaCreacion());
      assertEquals("Usuario Test", respuesta.getBody().creadaPor());
      verify(recetaService).addReceta(nuevaReceta);
    }

    @Test
    public void testDeleteReceta() {
      String codigoReceta = "RTEST-001";
      ResponseEntity<Void> respuesta = recetaController.deleteReceta(codigoReceta);
      assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
      verify(recetaService).deleteReceta(codigoReceta);
    }
}
