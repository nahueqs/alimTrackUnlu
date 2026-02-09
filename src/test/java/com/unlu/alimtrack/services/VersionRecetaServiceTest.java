package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.BorradoFallidoException;
import com.unlu.alimtrack.exceptions.ModificacionInvalidaException;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.PublicMapper;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.impl.VersionRecetaServiceImpl;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VersionRecetaServiceTest {

    @Mock private VersionRecetaRepository versionRecetaRepository;
    @Mock private RecetaService recetaService;
    @Mock private VersionRecetaMapper versionRecetaMapper;
    @Mock private VersionRecetaValidator versionRecetaValidator;
    @Mock private ProduccionQueryService produccionQueryService;
    @Mock private UsuarioService usuarioService;
    @Mock private UsuarioValidationService usuarioValidationService;
    @Mock private SeccionManagementService seccionManagementService;
    @Mock private PublicMapper publicMapper;

    @InjectMocks
    private VersionRecetaServiceImpl versionRecetaService;

    @Test
    void findAllVersiones_ShouldReturnList() {
        VersionRecetaModel model = new VersionRecetaModel();
        VersionMetadataResponseDTO dto = new VersionMetadataResponseDTO(
                "VER-1", "REC-1", "Receta Padre", "Nombre", "Desc", "user@test.com", LocalDateTime.now()
        );

        when(versionRecetaRepository.findAll()).thenReturn(List.of(model));
        when(versionRecetaMapper.toMetadataResponseDTOList(any())).thenReturn(List.of(dto));

        List<VersionMetadataResponseDTO> result = versionRecetaService.findAllVersiones();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findByCodigoVersion_ShouldReturnVersion_WhenExists() {
        String codigo = "VER-1";
        VersionRecetaModel model = new VersionRecetaModel();
        VersionMetadataResponseDTO dto = new VersionMetadataResponseDTO(
                codigo, "REC-1", "Receta Padre", "Nombre", "Desc", "user@test.com", LocalDateTime.now()
        );

        when(versionRecetaRepository.findByCodigoVersionReceta(codigo)).thenReturn(Optional.of(model));
        when(versionRecetaMapper.toMetadataResponseDTO(model)).thenReturn(dto);

        VersionMetadataResponseDTO result = versionRecetaService.findByCodigoVersion(codigo);

        assertNotNull(result);
        assertEquals(codigo, result.codigoVersionReceta());
    }

    @Test
    void saveVersionReceta_ShouldSave_WhenValid() {
        String codigoReceta = "REC-1";
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO(
                codigoReceta, "VER-NEW", "Nombre", "Desc", "user@test.com"
        );
        
        VersionRecetaModel model = new VersionRecetaModel();
        model.setCodigoVersionReceta("VER-NEW");
        
        RecetaModel recetaPadre = new RecetaModel();
        UsuarioModel usuario = new UsuarioModel();

        VersionMetadataResponseDTO responseDTO = new VersionMetadataResponseDTO(
                "VER-NEW", codigoReceta, "Receta Padre", "Nombre", "Desc", "user@test.com", LocalDateTime.now()
        );

        when(versionRecetaRepository.existsByCodigoVersionReceta("VER-NEW")).thenReturn(false);
        // Restaurado mock necesario
        when(recetaService.existsByCodigoReceta(codigoReceta)).thenReturn(true);
        when(usuarioValidationService.validarUsuarioAutorizado("user@test.com")).thenReturn(usuario);
        
        when(versionRecetaMapper.toModel(createDTO)).thenReturn(model);
        when(recetaService.findRecetaModelByCodigo(codigoReceta)).thenReturn(recetaPadre);
        when(versionRecetaRepository.save(model)).thenReturn(model);
        when(versionRecetaMapper.toMetadataResponseDTO(model)).thenReturn(responseDTO);

        VersionMetadataResponseDTO result = versionRecetaService.saveVersionReceta(codigoReceta, createDTO);

        assertNotNull(result);
        assertEquals("VER-NEW", result.codigoVersionReceta());
        verify(versionRecetaRepository).save(model);
    }
}
