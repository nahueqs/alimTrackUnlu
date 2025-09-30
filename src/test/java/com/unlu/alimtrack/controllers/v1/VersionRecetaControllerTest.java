package com.unlu.alimtrack.controllers.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class VersionRecetaControllerTest {

  private final LocalDateTime fechaFija = LocalDateTime.of(2025, 9, 30, 0, 0);
  private final String codigoReceta = "REC001";
  private final String codigoPrimeraVersion = "VER001";
  @Mock
  private VersionRecetaService versionRecetaService;
  @InjectMocks
  private VersionRecetaController versionRecetaController;
  private VersionRecetaResponseDTO version1;
  private VersionRecetaResponseDTO version2;


  @BeforeEach
  void setUpDosVersionesResponseDTO() {

    version1 = new VersionRecetaResponseDTO(
        codigoPrimeraVersion,
        "1.0",
        "Nombre primera versión",
        "Descripción primera version",
        "Usuario1",
        fechaFija
    );

    version2 = new VersionRecetaResponseDTO(
        "VER002",
        "1.1",
        "Nombre segunda versión",
        "Descripción segunda versión",
        "Usuario1",
        fechaFija
    );
  }

  @Test
  void getAllVersiones_ShouldReturnListOfVersions() {
    List<VersionRecetaResponseDTO> versiones = Arrays.asList(version1, version2);

    when(versionRecetaService.findAllVersiones()).thenReturn(versiones);

    ResponseEntity<List<VersionRecetaResponseDTO>> response = versionRecetaController.getAllVersiones();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    verify(versionRecetaService).findAllVersiones();
    verifyNoMoreInteractions(versionRecetaService);
  }

  @Test
  void getByCodigoVersion_ShouldReturnVersion() {
    when(versionRecetaService.findByCodigoVersion(codigoPrimeraVersion)).thenReturn(version1);

    ResponseEntity<VersionRecetaResponseDTO> response =
        versionRecetaController.getByCodigoVersion(codigoPrimeraVersion);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(codigoPrimeraVersion, response.getBody().codigoVersionReceta());
    verify(versionRecetaService).findByCodigoVersion(codigoPrimeraVersion);
    verifyNoMoreInteractions(versionRecetaService);
  }

  @Test
  void getAllByCodigoReceta_ShouldReturnVersionsForRecipe() {
    List<VersionRecetaResponseDTO> versiones = Arrays.asList(version1, version2);
    when(versionRecetaService.findAllByCodigoReceta(codigoReceta)).thenReturn(versiones);

    ResponseEntity<List<VersionRecetaResponseDTO>> response =
        versionRecetaController.getAllByCodigoReceta(codigoReceta);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    verify(versionRecetaService).findAllByCodigoReceta(codigoReceta);
    verifyNoMoreInteractions(versionRecetaService);
  }

  @Test
  void saveVersionReceta_ShouldCreateNewVersion() {
    // Arrange
    VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO(
        codigoReceta,
        codigoPrimeraVersion,
        "Nombre primera versión",
        "Descripción primera versión",
        "Usuario1"
    );

    // Expectations
    when(versionRecetaService.saveVersionReceta(codigoReceta, createDTO))
        .thenReturn(version1);

    // Act
    ResponseEntity<VersionRecetaResponseDTO> response =
        versionRecetaController.saveVersionReceta(codigoReceta, createDTO);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("/api/v1/recetas/" + codigoReceta + "/versiones/" + version1.codigoVersionReceta(),
        response.getHeaders().getLocation().getPath());
    verify(versionRecetaService).saveVersionReceta(codigoReceta, createDTO);
    verifyNoMoreInteractions(versionRecetaService);
  }

  @Test
  void updateVersionReceta_ShouldUpdateNombreYDescripcion() {

    VersionRecetaModifyDTO modifyDTO = new VersionRecetaModifyDTO(
        "Nombre primera versión actualizada",
        "Descripción primera versión actualizada"
    );

    VersionRecetaResponseDTO version1Actualizada = new VersionRecetaResponseDTO(
        codigoPrimeraVersion,
        "1.0",
        "Nombre primera versión actualizada",
        "Descripción primera versión actualizada",
        "Usuario1",
        fechaFija
    );

    when(versionRecetaService.updateVersionReceta(codigoPrimeraVersion, modifyDTO))
        .thenReturn(version1Actualizada);

    ResponseEntity<VersionRecetaResponseDTO> response =
        versionRecetaController.updateVersionReceta(codigoPrimeraVersion, modifyDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Nombre primera versión actualizada", response.getBody().nombre());
    assertEquals("Descripción primera versión actualizada", response.getBody().descripcion());
    verify(versionRecetaService).updateVersionReceta(anyString(), any(VersionRecetaModifyDTO.class));
    verifyNoMoreInteractions(versionRecetaService);
  }

  @Test
  void deleteVersionReceta_ShouldDeleteVersion() {
    doNothing().when(versionRecetaService).deleteVersionReceta(codigoPrimeraVersion);

    ResponseEntity<Void> response =
        versionRecetaController.deleteVersionReceta(codigoPrimeraVersion);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(versionRecetaService).deleteVersionReceta(codigoPrimeraVersion);
    verifyNoMoreInteractions(versionRecetaService);
  }
}
