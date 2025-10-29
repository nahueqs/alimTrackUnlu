package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.ProduccionCreateDTO;
import com.unlu.alimtrack.dtos.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.dtos.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exception.OperacionNoPermitida;
import com.unlu.alimtrack.exception.RecursoIdentifierConflictException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.queries.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.ProduccionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProduccionServiceTest {

    @Mock
    private ProduccionRepository produccionRepository;

    @Mock
    private ProduccionMapper produccionMapper;

    @Mock
    private ProduccionValidator produccionValidator;

    @Mock
    private UsuarioQueryService usuarioQueryService;

    @Mock
    private VersionRecetaQueryService versionRecetaQueryService;

    @InjectMocks
    private ProduccionService produccionService;

    private String codigoProduccion;
    private String codigoVersionReceta;
    private String username;
    private String lote;
    private String encargado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDate fechaInicioDate;
    private LocalDate fechaFinDate;
    private ProduccionModel produccionModel;
    private ProduccionResponseDTO produccionResponseDTO;
    private ProduccionCreateDTO produccionCreateDTO;
    private ProduccionFilterRequestDTO produccionFilterRequestDTO;
    private ProduccionCambioEstadoRequestDTO produccionCambioEstadoRequestDTO;
    private List<ProduccionModel> produccionModelList;
    private List<ProduccionResponseDTO> produccionResponseDTOList;

    @BeforeEach
    void setUp() {
        codigoProduccion = "PROD-001";
        codigoVersionReceta = "VR-001";
        username = "testuser";
        lote = "LOTE-001";
        encargado = "Juan Perez";
        fechaInicio = LocalDateTime.of(2025, 1, 1, 0, 0);
        fechaFin = LocalDateTime.of(2025, 12, 31, 23, 59);
        fechaInicioDate = LocalDate.of(2025, 1, 1);
        fechaFinDate = LocalDate.of(2025, 12, 31);

        produccionModel = new ProduccionModel();
        produccionModel.setCodigoProduccion(codigoProduccion);
        produccionModel.setLote(lote);
        produccionModel.setEncargado(encargado);
        produccionModel.setEstado(TipoEstadoProduccion.EN_PROCESO);
        produccionModel.setFechaInicio(fechaInicio);

        produccionResponseDTO = new ProduccionResponseDTO(
                codigoProduccion,
                codigoVersionReceta,
                encargado,
                lote,
                "EN_PROCESO",
                fechaInicio
        );

        produccionCreateDTO = new ProduccionCreateDTO(
                codigoVersionReceta,
                codigoProduccion,
                username,
                lote,
                encargado,
                "Observaciones de prueba"

        );

        produccionFilterRequestDTO = new ProduccionFilterRequestDTO(
                codigoVersionReceta,
                lote,
                encargado,
                fechaInicioDate,
                fechaFinDate,
                "EN_PROCESO"
        );

        produccionModelList = new ArrayList<>();
        produccionModelList.add(produccionModel);

        produccionResponseDTOList = new ArrayList<>();
        produccionResponseDTOList.add(produccionResponseDTO);
    }

    @Test
    void testFindByCodigoProduccion() {
        when(produccionRepository.findByCodigoProduccion(codigoProduccion)).thenReturn(produccionModel);
        when(produccionMapper.modelToResponseDTO(produccionModel)).thenReturn(produccionResponseDTO);

        ProduccionResponseDTO result = produccionService.findByCodigoProduccion(codigoProduccion);

        assertNotNull(result);
        assertEquals(codigoProduccion, result.codigoProduccion());
        assertEquals(codigoVersionReceta, result.codigoVersion());
        assertEquals(encargado, result.encargado());
        assertEquals(lote, result.lote());
        assertEquals("EN_PROCESO", result.estado());
        verify(produccionRepository).findByCodigoProduccion(codigoProduccion);
        verify(produccionMapper).modelToResponseDTO(produccionModel);
    }

    @Test
    void testFindByCodigoProduccionNoEncontrado() {
        String codigoInexistente = "CODIGO_INEXISTENTE";
        when(produccionRepository.findByCodigoProduccion(codigoInexistente)).thenReturn(null);

        assertThrows(RecursoNoEncontradoException.class, () -> {
            produccionService.findByCodigoProduccion(codigoInexistente);
        });

        verify(produccionRepository).findByCodigoProduccion(codigoInexistente);
    }

    @Test
    void testFindAllByFiltersVacios() {
        LocalDateTime fechaInicioConverted = fechaInicioDate.atStartOfDay();
        LocalDateTime fechaFinConverted = fechaFinDate.atTime(23, 59, 59);
        TipoEstadoProduccion estado = TipoEstadoProduccion.EN_PROCESO;

        when(produccionValidator.convertToStartOfDay(fechaInicioDate)).thenReturn(fechaInicioConverted);
        when(produccionValidator.convertToEndOfDay(fechaFinDate)).thenReturn(fechaFinConverted);
        when(produccionRepository.findByAdvancedFilters(
                codigoVersionReceta, lote, encargado, estado, fechaInicioConverted, fechaFinConverted
        )).thenReturn(produccionModelList);
        when(produccionMapper.modelListToResponseDTOList(produccionModelList)).thenReturn(produccionResponseDTOList);

        List<ProduccionResponseDTO> result = produccionService.findAllByFilters(produccionFilterRequestDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(codigoProduccion, result.get(0).codigoProduccion());
        verify(produccionValidator).convertToStartOfDay(fechaInicioDate);
        verify(produccionValidator).convertToEndOfDay(fechaFinDate);
        verify(produccionRepository).findByAdvancedFilters(
                codigoVersionReceta, lote, encargado, estado, fechaInicioConverted, fechaFinConverted
        );
        verify(produccionMapper).modelListToResponseDTOList(produccionModelList);
    }

    @Test
    void testSaveProduccion() {

        when(produccionRepository.existsByCodigoProduccion(codigoProduccion)).thenReturn(false);
        when(versionRecetaQueryService.existsByCodigoVersion(codigoVersionReceta)).thenReturn(true);
        when(usuarioQueryService.existsByUsername(username)).thenReturn(true);
        when(usuarioQueryService.estaActivoByUsername(username)).thenReturn(true);
        when(produccionMapper.createDTOtoModel(produccionCreateDTO)).thenReturn(produccionModel);
        when(produccionRepository.save(produccionModel)).thenReturn(produccionModel);
        when(produccionMapper.modelToResponseDTO(produccionModel)).thenReturn(produccionResponseDTO);

        ProduccionResponseDTO result = produccionService.saveProduccion(codigoProduccion, produccionCreateDTO);

        assertNotNull(result);
        assertEquals(codigoProduccion, result.codigoProduccion());
        assertEquals(codigoVersionReceta, result.codigoVersion());
        assertEquals(encargado, result.encargado());
        assertEquals(lote, result.lote());
        assertEquals("EN_PROCESO", result.estado());

        verify(produccionRepository).existsByCodigoProduccion(codigoProduccion);
        verify(versionRecetaQueryService).existsByCodigoVersion(codigoVersionReceta);
        verify(usuarioQueryService).existsByUsername(username);
        verify(produccionMapper).modelToResponseDTO(produccionModel);
        verify(produccionRepository).save(produccionModel);
        verify(produccionMapper).createDTOtoModel(produccionCreateDTO);
    }

    @Test
    void testSaveProduccionCodigoNoCoincide() {
        String codigoProduccionIncorrecto = "PROD-999";

        assertThrows(RecursoIdentifierConflictException.class, () -> {
            produccionService.saveProduccion(codigoProduccionIncorrecto, produccionCreateDTO);
        });
    }

    @Test
    void testSaveProduccionCodigoDuplicado() {
        String codigoProduccionDuplicado = "PROD-002";

        assertThrows(RecursoIdentifierConflictException.class, () -> {
            produccionService.saveProduccion(codigoProduccionDuplicado, produccionCreateDTO);
        });
    }

    @Test
    void testSaveProduccionVersionNoExiste() {

        when(produccionRepository.existsByCodigoProduccion(codigoProduccion)).thenReturn(false);
        when(versionRecetaQueryService.existsByCodigoVersion(codigoVersionReceta)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> {
            produccionService.saveProduccion(codigoProduccion, produccionCreateDTO);
        });

        verify(produccionRepository).existsByCodigoProduccion(codigoProduccion);
        verify(versionRecetaQueryService).existsByCodigoVersion(codigoVersionReceta);
    }

    @Test
    void testSaveProduccionUsuarioNoExiste() {

        when(produccionRepository.existsByCodigoProduccion(codigoProduccion)).thenReturn(false);
        when(versionRecetaQueryService.existsByCodigoVersion(codigoVersionReceta)).thenReturn(true);
        when(usuarioQueryService.existsByUsername(username)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> {
            produccionService.saveProduccion(codigoProduccion, produccionCreateDTO);
        });

        verify(produccionRepository).existsByCodigoProduccion(codigoProduccion);
        verify(versionRecetaQueryService).existsByCodigoVersion(codigoVersionReceta);
        verify(usuarioQueryService).existsByUsername(username);

    }

    @Test
    void testSaveProduccionUsuarioInactivo() {

        when(usuarioQueryService.existsByUsername(username)).thenReturn(true);
        when(usuarioQueryService.estaActivoByUsername(username)).thenReturn(false);
        when(produccionRepository.existsByCodigoProduccion(codigoProduccion)).thenReturn(null);
        when(versionRecetaQueryService.existsByCodigoVersion(codigoVersionReceta)).thenReturn(true);

        assertThrows(OperacionNoPermitida.class, () -> {
            produccionService.saveProduccion(codigoProduccion, produccionCreateDTO);
        });

        verify(usuarioQueryService).existsByUsername(username);
        verify(usuarioQueryService).estaActivoByUsername(username);
        verify(produccionRepository).existsByCodigoProduccion(codigoProduccion);
        verify(versionRecetaQueryService).existsByCodigoVersion(codigoVersionReceta);
    }


}
