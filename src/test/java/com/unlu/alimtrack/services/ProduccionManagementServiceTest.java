package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionMetadataModifyRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaTablaRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.eventos.ProduccionEventPublisher;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.mappers.RespuestaCampoMapper;
import com.unlu.alimtrack.mappers.RespuestaTablaMapper;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.impl.ProduccionManagementServiceImpl;
import com.unlu.alimtrack.services.validators.ProductionManagerServiceValidator;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduccionManagementServiceTest {

    @Mock
    private ProduccionRepository produccionRepository;
    @Mock
    private ProduccionEventPublisher produccionEventPublisher;
    @Mock
    private ProductionManagerServiceValidator productionManagerServiceValidator;
    @Mock
    private ProduccionMapper produccionMapper;
    @Mock
    private UsuarioValidationService usuarioValidationService;
    @Mock
    private ProduccionStateService produccionStateService;
    
    // Mocks adicionales necesarios para el constructor
    @Mock private VersionRecetaEstructuraService versionRecetaEstructuraService;
    @Mock private VersionRecetaValidator versionRecetaValidator;
    @Mock private RespuestaCampoMapper respuestaCampoMapper;
    @Mock private RespuestaTablaMapper respuestaTablasMapper;
    @Mock private RespuestaCampoService respuestaCampoService;
    @Mock private RespuestaTablaService respuestaTablaService;
    @Mock private ProduccionProgressService produccionProgressService;

    @InjectMocks
    private ProduccionManagementServiceImpl produccionManagementService;

    @Test
    void iniciarProduccion_ShouldSaveAndPublishEvent() {
        // Arrange
        ProduccionCreateDTO createDTO = new ProduccionCreateDTO(
                "REC-V1", "PROD-1", "creator@example.com", "LOTE-1", "Encargado", "Obs"
        );
        
        ProduccionModel model = new ProduccionModel();
        model.setCodigoProduccion("PROD-1");
        model.setLote("LOTE-1");
        VersionRecetaModel version = new VersionRecetaModel();
        version.setCodigoVersionReceta("REC-V1");
        model.setVersionReceta(version);

        when(produccionMapper.createDTOtoModel(createDTO)).thenReturn(model);
        when(produccionRepository.save(any(ProduccionModel.class))).thenReturn(model);
        when(produccionMapper.modelToResponseDTO(model)).thenReturn(new ProduccionMetadataResponseDTO(
                "PROD-1", "REC-V1", "Encargado", "creator@example.com", "LOTE-1", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        ));

        // Act
        ProduccionMetadataResponseDTO result = produccionManagementService.iniciarProduccion(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals("PROD-1", result.codigoProduccion());
        verify(productionManagerServiceValidator).verificarCreacionProduccion(createDTO);
        verify(produccionRepository).save(any(ProduccionModel.class));
        verify(produccionEventPublisher).publicarProduccionCreada(any(), eq("PROD-1"), any(), any(), any(), any());
    }

    @Test
    void updateEstado_ShouldCallStateServiceAndPublish() {
        // Arrange
        String codigo = "PROD-1";
        ProduccionCambioEstadoRequestDTO request = new ProduccionCambioEstadoRequestDTO(
                TipoEstadoProduccion.FINALIZADA.toString(), "user@test.com"
        );
        
        ProduccionModel model = new ProduccionModel();
        model.setCodigoProduccion(codigo);
        model.setEstado(TipoEstadoProduccion.FINALIZADA);

        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(model));

        // Act
        produccionManagementService.updateEstado(codigo, request);

        // Assert
        verify(usuarioValidationService).validarUsuarioAutorizado("user@test.com");
        verify(produccionStateService).cambiarEstado(codigo, request);
        verify(produccionEventPublisher).publicarEstadoCambiado(any(), eq(codigo), eq(TipoEstadoProduccion.FINALIZADA), any());
    }

    @Test
    void updateMetadata_ShouldUpdateFieldsAndSave() {
        // Arrange
        String codigo = "PROD-1";
        ProduccionMetadataModifyRequestDTO request = new ProduccionMetadataModifyRequestDTO(
                "Nuevo Encargado", "NUEVO-LOTE", "Nuevas Obs"
        );
        
        ProduccionModel model = new ProduccionModel();
        model.setCodigoProduccion(codigo);
        model.setLote("VIEJO-LOTE");

        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(model));

        // Act
        produccionManagementService.updateMetadata(codigo, request);

        // Assert
        assertEquals("NUEVO-LOTE", model.getLote());
        assertEquals("Nuevo Encargado", model.getEncargado());
        assertEquals("Nuevas Obs", model.getObservaciones());
        
        verify(productionManagerServiceValidator).validarUpdateMetadata(codigo, request);
        verify(produccionRepository).save(model);
        verify(produccionEventPublisher).publicarMetadataActualizada(any(), eq(codigo), eq("NUEVO-LOTE"), any(), any());
    }

    @Test
    void deleteProduccion_ShouldDeleteIfFound() {
        // Arrange
        String codigo = "PROD-1";
        ProduccionModel model = new ProduccionModel();
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(model));

        // Act
        produccionManagementService.deleteProduccion(codigo);

        // Assert
        verify(produccionRepository).delete(model);
    }

    @Test
    void deleteProduccion_ShouldThrowIfNotFound() {
        // Arrange
        String codigo = "PROD-INEXISTENTE";
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> 
            produccionManagementService.deleteProduccion(codigo)
        );
    }

    @Test
    void guardarRespuestaCampo_ShouldSaveAndPublishEvent() {
        // Arrange
        String codigo = "PROD-1";
        Long idCampo = 1L;
        RespuestaCampoRequestDTO request = new RespuestaCampoRequestDTO();
        request.setEmailCreador("user@test.com");
        request.setValorTexto("valor");

        ProduccionModel produccion = new ProduccionModel();
        produccion.setCodigoProduccion(codigo);
        
        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(idCampo);

        RespuestaCampoResponseDTO respuestaDTO = RespuestaCampoResponseDTO.builder()
                .valor("valor")
                .build();

        when(productionManagerServiceValidator.validarProduccionParaEdicion(codigo)).thenReturn(produccion);
        when(productionManagerServiceValidator.validarCampoExiste(idCampo)).thenReturn(campo);
        when(respuestaCampoService.guardarRespuestaCampo(codigo, idCampo, request)).thenReturn(respuestaDTO);

        // Act
        produccionManagementService.guardarRespuestaCampo(codigo, idCampo, request);

        // Assert
        verify(usuarioValidationService).validarUsuarioAutorizado("user@test.com");
        verify(versionRecetaValidator).validarCampoPerteneceAVersion(produccion, campo);
        verify(produccionEventPublisher).publicarRespuestaCampoGuardada(any(), eq(codigo), eq(idCampo), eq("valor"));
    }

    @Test
    void guardarRespuestaCeldaTabla_ShouldSaveAndPublishEvent() {
        // Arrange
        String codigo = "PROD-1";
        Long idTabla = 10L;
        Long idFila = 1L;
        Long idColumna = 2L;
        RespuestaTablaRequestDTO request = new RespuestaTablaRequestDTO();
        request.setEmailCreador("user@test.com");
        request.setValorTexto("valor");

        ProduccionModel produccion = new ProduccionModel();
        produccion.setCodigoProduccion(codigo);
        VersionRecetaModel version = new VersionRecetaModel();
        produccion.setVersionReceta(version);

        RespuestaCeldaTablaResponseDTO respuestaDTO = new RespuestaCeldaTablaResponseDTO(
                idTabla, idFila, idColumna, "TEXTO", "Fila", "Columna", "valor", LocalDateTime.now()
        );

        when(productionManagerServiceValidator.validarProduccionParaEdicion(codigo)).thenReturn(produccion);
        when(respuestaTablaService.guardarRespuestaTabla(codigo, idTabla, idFila, idColumna, request)).thenReturn(respuestaDTO);

        // Act
        produccionManagementService.guardarRespuestaCeldaTabla(codigo, idTabla, idFila, idColumna, request);

        // Assert
        verify(usuarioValidationService).validarUsuarioAutorizado("user@test.com");
        verify(productionManagerServiceValidator).validarTablaPertenceAVersionProduccion(version, idTabla);
        verify(productionManagerServiceValidator).combinacionFilaColumnaPerteneceTabla(idFila, idColumna, idTabla);
        verify(produccionEventPublisher).publicarRespuestaTablaGuardada(any(), eq(codigo), eq(idTabla), eq(idFila), eq(idColumna), eq("valor"));
    }
}
