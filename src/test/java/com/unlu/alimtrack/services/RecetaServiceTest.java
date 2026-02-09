package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.Receta.RecetaMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.BorradoFallidoException;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import com.unlu.alimtrack.services.impl.RecetaServiceImpl;
import com.unlu.alimtrack.services.validators.RecetaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;
    @Mock
    private RecetaMapper recetaMapper;
    @Mock
    private RecetaValidator recetaValidator;
    @Mock
    private RecetaQueryService recetaQueryService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private UsuarioValidationService usuarioValidationService;

    @InjectMocks
    private RecetaServiceImpl recetaService;

    @Test
    void findAllRecetas_ShouldReturnList() {
        RecetaModel model = new RecetaModel();
        RecetaMetadataResponseDTO dto = new RecetaMetadataResponseDTO(
                "REC-1", "Desc", "Nombre", "user@test.com", LocalDateTime.now()
        );

        when(recetaRepository.findAll()).thenReturn(List.of(model));
        when(recetaMapper.recetaModelsToRecetaResponseDTOs(any())).thenReturn(List.of(dto));

        List<RecetaMetadataResponseDTO> result = recetaService.findAllRecetas();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(recetaValidator).validateModelList(any());
    }

    @Test
    void findReceta_ShouldReturnReceta_WhenExists() {
        String codigo = "REC-1";
        RecetaModel model = new RecetaModel();
        RecetaMetadataResponseDTO dto = new RecetaMetadataResponseDTO(
                codigo, "Desc", "Nombre", "user@test.com", LocalDateTime.now()
        );

        when(recetaRepository.findByCodigoReceta(codigo)).thenReturn(model);
        when(recetaMapper.recetaModeltoRecetaResponseDTO(model)).thenReturn(dto);

        RecetaMetadataResponseDTO result = recetaService.findReceta(codigo);

        assertNotNull(result);
        assertEquals(codigo, result.codigoReceta());
        verify(recetaValidator).validateCodigoReceta(codigo);
        verify(recetaValidator).validateModel(model, codigo);
    }

    @Test
    void findReceta_ShouldThrow_WhenNotFound() {
        String codigo = "REC-INEXISTENTE";
        when(recetaRepository.findByCodigoReceta(codigo)).thenReturn(null);

        assertThrows(RecursoNoEncontradoException.class, () -> 
            recetaService.findReceta(codigo)
        );
    }

    @Test
    void addReceta_ShouldSave_WhenValid() {
        RecetaCreateDTO createDTO = new RecetaCreateDTO(
                "REC-NEW", "Nombre", "Desc", "user@test.com"
        );
        RecetaModel model = new RecetaModel();
        model.setCodigoReceta("REC-NEW");
        UsuarioModel usuario = new UsuarioModel();
        
        RecetaMetadataResponseDTO responseDTO = new RecetaMetadataResponseDTO(
                "REC-NEW", "Desc", "Nombre", "user@test.com", LocalDateTime.now()
        );

        when(recetaRepository.existsByCodigoReceta("REC-NEW")).thenReturn(false);
        when(usuarioValidationService.validarUsuarioAutorizado("user@test.com")).thenReturn(usuario);
        when(recetaMapper.recetaCreateDTOtoModel(createDTO)).thenReturn(model);
        when(recetaRepository.save(model)).thenReturn(model);
        when(recetaMapper.recetaModeltoRecetaResponseDTO(model)).thenReturn(responseDTO);

        RecetaMetadataResponseDTO result = recetaService.addReceta(createDTO);

        assertNotNull(result);
        assertEquals("REC-NEW", result.codigoReceta());
        verify(recetaRepository).save(model);
    }

    @Test
    void addReceta_ShouldThrow_WhenDuplicateCode() {
        RecetaCreateDTO createDTO = new RecetaCreateDTO(
                "REC-DUP", "Nombre", "Desc", "user@test.com"
        );
        when(recetaRepository.existsByCodigoReceta("REC-DUP")).thenReturn(true);

        assertThrows(RecursoDuplicadoException.class, () -> 
            recetaService.addReceta(createDTO)
        );
    }

    @Test
    void addReceta_ShouldThrow_WhenUserNotAuthorized() {
        RecetaCreateDTO createDTO = new RecetaCreateDTO(
                "REC-NEW", "Nombre", "Desc", "unknown@test.com"
        );
        when(recetaRepository.existsByCodigoReceta("REC-NEW")).thenReturn(false);
        when(usuarioValidationService.validarUsuarioAutorizado("unknown@test.com"))
                .thenThrow(new OperacionNoPermitida("Usuario no autorizado"));

        assertThrows(OperacionNoPermitida.class, () -> 
            recetaService.addReceta(createDTO)
        );
    }

    @Test
    void updateReceta_ShouldUpdate_WhenExists() {
        String codigo = "REC-1";
        RecetaModifyDTO modifyDTO = new RecetaModifyDTO("Nuevo Nombre", "Nueva Desc");
        RecetaModel model = new RecetaModel();
        model.setCodigoReceta(codigo);
        
        RecetaMetadataResponseDTO responseDTO = new RecetaMetadataResponseDTO(
                codigo, "Nueva Desc", "Nuevo Nombre", "user@test.com", LocalDateTime.now()
        );

        when(recetaRepository.findByCodigoReceta(codigo)).thenReturn(model);
        when(recetaRepository.save(model)).thenReturn(model);
        when(recetaMapper.recetaModeltoRecetaResponseDTO(model)).thenReturn(responseDTO);

        RecetaMetadataResponseDTO result = recetaService.updateReceta(codigo, modifyDTO);

        assertNotNull(result);
        verify(recetaValidator).validateDatosModification(modifyDTO);
        verify(recetaMapper).updateModelFromModifyDTO(modifyDTO, model);
        verify(recetaRepository).save(model);
    }

    @Test
    void deleteReceta_ShouldDelete_WhenNoVersions() {
        String codigo = "REC-1";
        RecetaModel model = new RecetaModel();
        
        when(recetaRepository.findByCodigoReceta(codigo)).thenReturn(model);
        when(recetaQueryService.recetaTieneVersiones(codigo)).thenReturn(false);

        recetaService.deleteReceta(codigo);

        verify(recetaRepository).delete(model);
    }

    @Test
    void deleteReceta_ShouldThrow_WhenHasVersions() {
        String codigo = "REC-CON-VERSIONES";
        RecetaModel model = new RecetaModel();
        
        when(recetaRepository.findByCodigoReceta(codigo)).thenReturn(model);
        when(recetaQueryService.recetaTieneVersiones(codigo)).thenReturn(true);

        assertThrows(BorradoFallidoException.class, () -> 
            recetaService.deleteReceta(codigo)
        );
        
        verify(recetaRepository, never()).delete(any());
    }
}
