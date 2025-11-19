package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VersionRecetaControllerTest {

    private final LocalDateTime fechaFija = LocalDateTime.of(2025, 9, 30, 0, 0);
    private final String codigoReceta = "REC001";
    private final String codigoPrimeraVersion = "VER001";

    @Mock
    private VersionRecetaMetadataService versionRecetaMetadataService;
    @InjectMocks
    private VersionRecetaController versionRecetaController;
    private VersionRecetaMetadataResponseDTO version1;
    private VersionRecetaMetadataResponseDTO version2;


    @BeforeEach
    void setUpDosVersionesResponseDTO() {

        version1 = new VersionRecetaMetadataResponseDTO(
                codigoPrimeraVersion,
                "1.0",
                "receta padre",
                "Version1 ",
                "Descripción primera version",
                "Usuario1",
                fechaFija
        );

        version2 = new VersionRecetaMetadataResponseDTO(
                "VER002",
                "1.1",
                "Nombre segunda versión",
                "Nombre segunda versión",
                "Descripción segunda versión",
                "Usuario1",
                fechaFija
        );
    }

    @Test
    void getAllVersiones_ShouldReturnListOfVersions() {
        List<VersionRecetaMetadataResponseDTO> versiones = Arrays.asList(version1, version2);

        when(versionRecetaMetadataService.findAllVersiones()).thenReturn(versiones);

        ResponseEntity<List<VersionRecetaMetadataResponseDTO>> response = versionRecetaController.getAllVersiones();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(versionRecetaMetadataService).findAllVersiones();
        verifyNoMoreInteractions(versionRecetaMetadataService);
    }

    @Test
    void getByCodigoVersion_ShouldReturnVersion() {
        when(versionRecetaMetadataService.findByCodigoVersion(codigoPrimeraVersion)).thenReturn(version1);

        ResponseEntity<VersionRecetaMetadataResponseDTO> response =
                versionRecetaController.getByCodigoVersion(codigoPrimeraVersion);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(codigoPrimeraVersion, response.getBody().codigoVersionReceta());
        verify(versionRecetaMetadataService).findByCodigoVersion(codigoPrimeraVersion);
        verifyNoMoreInteractions(versionRecetaMetadataService);
    }

    @Test
    void getAllByCodigoReceta_ShouldReturnVersionsForRecipe() {
        List<VersionRecetaMetadataResponseDTO> versiones = Arrays.asList(version1, version2);
        when(versionRecetaMetadataService.findAllByCodigoReceta(codigoReceta)).thenReturn(versiones);

        ResponseEntity<List<VersionRecetaMetadataResponseDTO>> response =
                versionRecetaController.getAllByCodigoReceta(codigoReceta);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(versionRecetaMetadataService).findAllByCodigoReceta(codigoReceta);
        verifyNoMoreInteractions(versionRecetaMetadataService);
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
        when(versionRecetaMetadataService.saveVersionReceta(codigoReceta, createDTO))
                .thenReturn(version1);

        // Act
        ResponseEntity<VersionRecetaMetadataResponseDTO> response =
                versionRecetaController.saveVersionReceta(codigoReceta, createDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/v1/recetas/" + codigoReceta + "/versiones/" + version1.codigoVersionReceta(),
                response.getHeaders().getLocation().getPath());
        verify(versionRecetaMetadataService).saveVersionReceta(codigoReceta, createDTO);
        verifyNoMoreInteractions(versionRecetaMetadataService);
    }

    @Test
    void updateVersionReceta_ShouldUpdateNombreYDescripcion() {

        VersionRecetaModifyDTO modifyDTO = new VersionRecetaModifyDTO(
                "Nombre primera versión actualizada",
                "Descripción primera versión actualizada"
        );

        VersionRecetaMetadataResponseDTO version1Actualizada = new VersionRecetaMetadataResponseDTO(
                codigoPrimeraVersion,
                "1.0",
                "Nombre receta padre",
                "Nombre primera versión actualizada",
                "Descripción primera versión actualizada",
                "Usuario1",
                fechaFija
        );

        when(versionRecetaMetadataService.updateVersionReceta(codigoPrimeraVersion, modifyDTO))
                .thenReturn(version1Actualizada);

        ResponseEntity<VersionRecetaMetadataResponseDTO> response =
                versionRecetaController.updateVersionReceta(codigoPrimeraVersion, modifyDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Nombre receta padre", response.getBody().nombreRecetaPadre());
        assertEquals("Nombre primera versión actualizada", response.getBody().nombre());
        assertEquals("Descripción primera versión actualizada", response.getBody().descripcion());
        verify(versionRecetaMetadataService).updateVersionReceta(anyString(), any(VersionRecetaModifyDTO.class));
        verifyNoMoreInteractions(versionRecetaMetadataService);
    }

    @Test
    void deleteVersionReceta_ShouldDeleteVersion() {
        doNothing().when(versionRecetaMetadataService).deleteVersionReceta(codigoPrimeraVersion);

        ResponseEntity<Void> response =
                versionRecetaController.deleteVersionReceta(codigoPrimeraVersion);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(versionRecetaMetadataService).deleteVersionReceta(codigoPrimeraVersion);
        verifyNoMoreInteractions(versionRecetaMetadataService);
    }
}
