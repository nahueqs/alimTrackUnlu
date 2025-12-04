package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.enums.TipoRolUsuario;
import com.unlu.alimtrack.exceptions.CambioEstadoProduccionInvalido;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.mappers.RespuestaCampoMapper;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.RespuestaCampoRepository;
import com.unlu.alimtrack.repositories.RespuestaTablaRepository;
import com.unlu.alimtrack.repositories.TablaRepository;
import com.unlu.alimtrack.services.impl.AutoSaveServiceImpl;
import com.unlu.alimtrack.services.impl.ProduccionManagementServiceImpl;
import com.unlu.alimtrack.services.impl.UsuarioServiceImpl;
import com.unlu.alimtrack.services.impl.VersionRecetaEstructuraServiceImpl;
import com.unlu.alimtrack.services.validators.ProductionManagerServiceValidator;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProduccionManagementServiceImplTest {

    @Mock
    private ProduccionRepository produccionRepository;
    @Mock
    private RespuestaCampoRepository respuestaCampoRepository;
    @Mock
    private RespuestaTablaRepository respuestaTablaRepository;
    @Mock
    private TablaRepository tablaRepository;
    @Mock
    private ProductionManagerServiceValidator productionManagerServiceValidator;
    @Mock
    private VersionRecetaValidator versionRecetaValidator;
    @Mock
    private VersionRecetaEstructuraServiceImpl versionRecetaEstructuraServiceImpl;
    @Mock
    private AutoSaveServiceImpl autoSaveServiceImpl;
    @Mock
    private ProduccionMapper produccionMapper;
    @Mock
    private RespuestaCampoMapper respuestaCampoMapper;
    @Mock
    private UsuarioServiceImpl usuarioServiceImpl; // Added mock for UsuarioService

    @InjectMocks
    private ProduccionManagementServiceImpl produccionManagementServiceImpl;

    private ProduccionModel produccionEnProceso;
    private VersionRecetaModel versionReceta;
    private UsuarioModel usuarioCreador; // Added UsuarioModel for testing

    @BeforeEach
    void setUp() {
        versionReceta = new VersionRecetaModel();
        versionReceta.setCodigoVersionReceta("V-001");

        usuarioCreador = new UsuarioModel(); // Initialize UsuarioModel
        usuarioCreador.setId(1L);
        usuarioCreador.setEmail("test@example.com");
        usuarioCreador.setRol(TipoRolUsuario.ADMIN); // Set a role

        produccionEnProceso = new ProduccionModel();
        produccionEnProceso.setProduccion(1L);
        produccionEnProceso.setCodigoProduccion("PROD-001");
        produccionEnProceso.setEstado(TipoEstadoProduccion.EN_PROCESO);
        produccionEnProceso.setVersionReceta(versionReceta);
        produccionEnProceso.setUsuarioCreador(usuarioCreador); // Set the usuarioCreador
    }

    @Test
    void iniciarProduccion_shouldCreateAndReturnProduccion() {
        // Arrange
        ProduccionCreateDTO createDTO = new ProduccionCreateDTO("V-001", "PROD-001", "creator@test.com", "LOTE-1", "ENCARGADO", "Obs");
        ProduccionModel nuevaProduccion = new ProduccionModel();
        nuevaProduccion.setUsuarioCreador(usuarioCreador); // Ensure the new production has a creator
        ProduccionMetadataResponseDTO responseDTO = new ProduccionMetadataResponseDTO("PROD-001", "V-001", "ENCARGADO", "creator@test.com", "LOTE-1", "EN_PROCESO", LocalDateTime.now(), null, null, "Obs");

        doNothing().when(productionManagerServiceValidator).verificarCreacionProduccion(createDTO);
        when(produccionMapper.createDTOtoModel(createDTO)).thenReturn(nuevaProduccion);
        when(produccionRepository.save(nuevaProduccion)).thenReturn(nuevaProduccion);
        when(produccionMapper.modelToResponseDTO(nuevaProduccion)).thenReturn(responseDTO);

        // Act
        ProduccionMetadataResponseDTO result = produccionManagementServiceImpl.iniciarProduccion(createDTO);

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        assertThat(nuevaProduccion.getEstado()).isEqualTo(TipoEstadoProduccion.EN_PROCESO);
        assertThat(nuevaProduccion.getFechaInicio()).isNotNull();
        verify(produccionRepository, times(1)).save(nuevaProduccion);
    }

    @Test
    void updateEstado_shouldChangeStateAndSetFechaFinWhenFinal() {
        // Arrange
        ProduccionCambioEstadoRequestDTO cambioDTO = new ProduccionCambioEstadoRequestDTO(TipoEstadoProduccion.FINALIZADA.name());
        when(produccionRepository.findByCodigoProduccion("PROD-001")).thenReturn(Optional.of(produccionEnProceso));

        // Act
        produccionManagementServiceImpl.updateEstado("PROD-001", cambioDTO);

        // Assert
        assertThat(produccionEnProceso.getEstado()).isEqualTo(TipoEstadoProduccion.FINALIZADA);
        assertThat(produccionEnProceso.getFechaFin()).isNotNull();
        verify(autoSaveServiceImpl, times(1)).ejecutarAutoSaveInmediato(1L);
        verify(produccionRepository, times(1)).save(produccionEnProceso);
    }

    @Test
    void updateEstado_shouldThrowExceptionForInvalidTransition() {
        // Arrange
        produccionEnProceso.setEstado(TipoEstadoProduccion.FINALIZADA);
        ProduccionCambioEstadoRequestDTO cambioDTO = new ProduccionCambioEstadoRequestDTO(TipoEstadoProduccion.EN_PROCESO.name());
        when(produccionRepository.findByCodigoProduccion("PROD-001")).thenReturn(Optional.of(produccionEnProceso));

        // Act & Assert
        assertThatThrownBy(() -> produccionManagementServiceImpl.updateEstado("PROD-001", cambioDTO))
                .isInstanceOf(CambioEstadoProduccionInvalido.class)
                .hasMessageContaining("No se puede modificar una producción FINALIZADA");
    }

    @Test
    void guardarRespuestaCampo_shouldSaveAndReturnRespuesta() {
        // Arrange
        long idCampo = 10L;
        String emailCreador = "test@example.com";
        RespuestaCampoRequestDTO requestDTO = new RespuestaCampoRequestDTO("valor", emailCreador);
        CampoSimpleModel campo = new CampoSimpleModel();
        campo.setId(idCampo);
        RespuestaCampoModel respuesta = new RespuestaCampoModel();
        respuesta.setCreadoPor(usuarioCreador); // Set the UsuarioModel
        RespuestaCampoResponseDTO responseDTO = new RespuestaCampoResponseDTO(1L, idCampo, "valor", LocalDateTime.now());

        when(productionManagerServiceValidator.validarProduccionParaEdicion("PROD-001")).thenReturn(produccionEnProceso);
        when(productionManagerServiceValidator.validarCampoExiste(idCampo)).thenReturn(campo);
        doNothing().when(versionRecetaValidator).validarCampoPerteneceAVersion(produccionEnProceso, campo);
        when(respuestaCampoRepository.findByIdProduccionAndIdCampo(produccionEnProceso, campo)).thenReturn(respuesta); // Changed to Optional.empty()
        when(respuestaCampoRepository.save(any(RespuestaCampoModel.class))).thenReturn(respuesta);
        when(respuestaCampoMapper.toResponseDTO(respuesta)).thenReturn(responseDTO);
        when(usuarioServiceImpl.getUsuarioModelByEmail(emailCreador)).thenReturn(usuarioCreador); // Mock user service call

        // Act
        RespuestaCampoResponseDTO result = produccionManagementServiceImpl.guardarRespuestaCampo("PROD-001", idCampo, requestDTO); // Corrected: Assign return value

        // Assert
        assertThat(result).isEqualTo(responseDTO);
        verify(autoSaveServiceImpl, times(1)).ejecutarAutoSaveInmediato(1L);
        verify(respuestaCampoRepository, times(1)).save(any(RespuestaCampoModel.class));
        verify(usuarioServiceImpl, times(1)).getUsuarioModelByEmail(emailCreador); // Verify user service was called
    }


}
