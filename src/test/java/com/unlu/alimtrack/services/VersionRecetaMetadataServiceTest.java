package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.BorradoFallidoException;
import com.unlu.alimtrack.exceptions.ModificacionInvalidaException;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.impl.VersionRecetaMetadataServiceImpl;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VersionRecetaMetadataServiceTest {

    @Mock
    private VersionRecetaRepository versionRecetaRepository;
    @Mock
    private RecetaService recetaService;
    @Mock
    private VersionRecetaMapper versionRecetaMapper;
    @Mock
    private VersionRecetaValidator versionRecetaValidator;
    @Mock
    private ProduccionQueryService produccionQueryService;
    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private VersionRecetaMetadataServiceImpl versionRecetaMetadataService;

    @Test
    void findAllVersiones_ShouldReturnList() {
        VersionRecetaModel model = new VersionRecetaModel();
        VersionMetadataResponseDTO dto = new VersionMetadataResponseDTO(
                "VER-1", "REC-1", "Receta Padre", "Nombre", "Desc", "user@test.com", LocalDateTime.now()
        );

        when(versionRecetaRepository.findAll()).thenReturn(List.of(model));
        when(versionRecetaMapper.toMetadataResponseDTOList(any())).thenReturn(List.of(dto));

        List<VersionMetadataResponseDTO> result = versionRecetaMetadataService.findAllVersiones();

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

        VersionMetadataResponseDTO result = versionRecetaMetadataService.findByCodigoVersion(codigo);

        assertNotNull(result);
        assertEquals(codigo, result.codigoVersionReceta());
    }

    @Test
    void findByCodigoVersion_ShouldThrow_WhenNotFound() {
        String codigo = "VER-INEXISTENTE";
        when(versionRecetaRepository.findByCodigoVersionReceta(codigo)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> 
            versionRecetaMetadataService.findByCodigoVersion(codigo)
        );
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
        when(recetaService.existsByCodigoReceta(codigoReceta)).thenReturn(true);
        when(usuarioService.existsByEmail("user@test.com")).thenReturn(true);
        when(usuarioService.estaActivoByEmail("user@test.com")).thenReturn(true);
        
        when(versionRecetaMapper.toModel(createDTO)).thenReturn(model);
        when(recetaService.findRecetaModelByCodigo(codigoReceta)).thenReturn(recetaPadre);
        when(usuarioService.getUsuarioModelByEmail("user@test.com")).thenReturn(usuario);
        when(versionRecetaRepository.save(model)).thenReturn(model);
        when(versionRecetaMapper.toMetadataResponseDTO(model)).thenReturn(responseDTO);

        VersionMetadataResponseDTO result = versionRecetaMetadataService.saveVersionReceta(codigoReceta, createDTO);

        assertNotNull(result);
        assertEquals("VER-NEW", result.codigoVersionReceta());
        verify(versionRecetaRepository).save(model);
    }

    @Test
    void saveVersionReceta_ShouldThrow_WhenCodeMismatch() {
        String codigoReceta = "REC-1";
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO(
                "REC-OTRA", "VER-NEW", "Nombre", "Desc", "user@test.com"
        );

        assertThrows(ModificacionInvalidaException.class, () -> 
            versionRecetaMetadataService.saveVersionReceta(codigoReceta, createDTO)
        );
    }

    @Test
    void saveVersionReceta_ShouldThrow_WhenDuplicateVersion() {
        String codigoReceta = "REC-1";
        VersionRecetaCreateDTO createDTO = new VersionRecetaCreateDTO(
                codigoReceta, "VER-DUP", "Nombre", "Desc", "user@test.com"
        );
        
        when(versionRecetaRepository.existsByCodigoVersionReceta("VER-DUP")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> 
            versionRecetaMetadataService.saveVersionReceta(codigoReceta, createDTO)
        );
    }

    @Test
    void updateVersionReceta_ShouldUpdate_WhenExists() {
        String codigo = "VER-1";
        VersionRecetaModifyDTO modifyDTO = new VersionRecetaModifyDTO("Nuevo Nombre", "Nueva Desc");
        VersionRecetaModel model = new VersionRecetaModel();
        model.setCodigoVersionReceta(codigo);
        
        VersionMetadataResponseDTO responseDTO = new VersionMetadataResponseDTO(
                codigo, "REC-1", "Receta Padre", "Nuevo Nombre", "Nueva Desc", "user@test.com", LocalDateTime.now()
        );

        when(versionRecetaRepository.findByCodigoVersionReceta(codigo)).thenReturn(Optional.of(model));
        when(versionRecetaRepository.save(model)).thenReturn(model);
        when(versionRecetaMapper.toMetadataResponseDTO(model)).thenReturn(responseDTO);

        VersionMetadataResponseDTO result = versionRecetaMetadataService.updateVersionReceta(codigo, modifyDTO);

        assertNotNull(result);
        verify(versionRecetaValidator).validateModification(modifyDTO);
        verify(versionRecetaMapper).updateModelFromModifyDTO(modifyDTO, model);
        verify(versionRecetaRepository).save(model);
    }

    @Test
    void deleteVersionReceta_ShouldDelete_WhenNoProducciones() {
        String codigo = "VER-1";
        VersionRecetaModel model = new VersionRecetaModel();
        
        when(versionRecetaRepository.findByCodigoVersionReceta(codigo)).thenReturn(Optional.of(model));
        when(produccionQueryService.existsByVersionRecetaPadre(codigo)).thenReturn(false);

        versionRecetaMetadataService.deleteVersionReceta(codigo);

        verify(versionRecetaRepository).delete(model);
    }

    @Test
    void deleteVersionReceta_ShouldThrow_WhenHasProducciones() {
        String codigo = "VER-CON-PROD";
        VersionRecetaModel model = new VersionRecetaModel();
        
        when(versionRecetaRepository.findByCodigoVersionReceta(codigo)).thenReturn(Optional.of(model));
        when(produccionQueryService.existsByVersionRecetaPadre(codigo)).thenReturn(true);

        assertThrows(BorradoFallidoException.class, () -> 
            versionRecetaMetadataService.deleteVersionReceta(codigo)
        );
        
        verify(versionRecetaRepository, never()).delete(any());
    }
}
