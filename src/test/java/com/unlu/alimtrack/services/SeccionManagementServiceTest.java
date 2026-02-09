package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.secciones.SeccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.SeccionResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.*;
import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.SeccionRepository;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.impl.SeccionManagementServiceImpl;
import com.unlu.alimtrack.services.validators.SeccionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeccionManagementServiceTest {

    @Mock private VersionRecetaRepository versionRecetaRepository;
    @Mock private VersionRecetaQueryService versionRecetaQueryService;
    @Mock private SeccionRepository seccionRepository;
    @Mock private SeccionValidator seccionValidator;
    @Mock private UsuarioService usuarioService;
    @Mock private SeccionMapperManual seccionMapperManual;
    @Mock private CampoSimpleMapper campoSimpleMapper;
    @Mock private GrupoCamposMapper grupoCamposMapper;
    @Mock private TablaMapperManual tablaMapper;
    @Mock private ColumnaTablaMapper columnaTablaMapper;
    @Mock private FilaTablaMapper filaTablaMapper;

    @InjectMocks
    private SeccionManagementServiceImpl seccionManagementService;

    @Test
    void crearSeccion_ShouldSave_WhenValid() {
        // Arrange
        String codigoReceta = "VER-1";
        // Corregido: Eliminado argumento "TIPO" que ya no existe en el DTO
        SeccionCreateDTO dto = new SeccionCreateDTO(
                codigoReceta, "user@test.com", "Titulo", 1, null, null, null
        );
        
        VersionRecetaModel version = new VersionRecetaModel();
        UsuarioModel usuario = new UsuarioModel();
        SeccionModel seccionGuardada = new SeccionModel();
        seccionGuardada.setId(1L);

        when(versionRecetaQueryService.existsByCodigoVersion(codigoReceta)).thenReturn(true);
        when(usuarioService.existsByEmail("user@test.com")).thenReturn(true);
        when(versionRecetaRepository.findByCodigoVersionReceta(codigoReceta)).thenReturn(Optional.of(version));
        when(usuarioService.getUsuarioModelByEmail("user@test.com")).thenReturn(usuario);
        when(seccionRepository.save(any(SeccionModel.class))).thenReturn(seccionGuardada);

        // Act
        SeccionModel result = seccionManagementService.crearSeccion(codigoReceta, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(seccionValidator).validarCreacionSeccion(codigoReceta, dto);
        verify(seccionRepository).save(any(SeccionModel.class));
    }

    @Test
    void crearSeccion_ShouldThrow_WhenVersionNotFound() {
        String codigoReceta = "VER-INEXISTENTE";
        // Corregido: Eliminado argumento "TIPO" que ya no existe en el DTO
        SeccionCreateDTO dto = new SeccionCreateDTO(
                codigoReceta, "u", "T", 1, null, null, null
        );
        
        when(versionRecetaQueryService.existsByCodigoVersion(codigoReceta)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> 
            seccionManagementService.crearSeccion(codigoReceta, dto)
        );
    }

    @Test
    void obtenerSeccionesDTOCompletasPorVersion_ShouldReturnList() {
        String codigo = "VER-1";
        VersionRecetaModel version = new VersionRecetaModel();
        SeccionModel seccion = new SeccionModel();
        SeccionResponseDTO dto = new SeccionResponseDTO(
                1L, "VER-1", "Titulo", 1, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()
        );

        when(versionRecetaRepository.findByCodigoVersionReceta(codigo)).thenReturn(Optional.of(version));
        when(seccionRepository.findSeccionesBasicas(version)).thenReturn(List.of(seccion));
        when(seccionMapperManual.toResponseDTOList(any())).thenReturn(List.of(dto));

        List<SeccionResponseDTO> result = seccionManagementService.obtenerSeccionesDTOCompletasPorVersion(codigo);

        assertFalse(result.isEmpty());
        verify(seccionRepository).fetchCamposSimples(any());
    }
}
