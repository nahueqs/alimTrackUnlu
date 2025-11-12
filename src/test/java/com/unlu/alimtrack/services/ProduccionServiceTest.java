package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ProduccionResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.queries.ProduccionQueryServiceImpl;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.queries.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.ProduccionQueryServiceValidator;
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
    private ProduccionQueryServiceValidator produccionQueryServiceValidator;

    @Mock
    private UsuarioQueryService usuarioQueryService;

    @Mock
    private VersionRecetaQueryService versionRecetaQueryService;

    @InjectMocks
    private ProduccionQueryServiceImpl produccionManagementService;

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
        String observaciones = "Observaciones de prueba";

        produccionModel = new ProduccionModel();
        produccionModel.setCodigoProduccion(codigoProduccion);
        produccionModel.setLote(lote);
        produccionModel.setEncargado(encargado);
        produccionModel.setEstado(TipoEstadoProduccion.EN_PROCESO);
        produccionModel.setFechaInicio(fechaInicio);
        produccionModel.setFechaFin(fechaFin);
        produccionModel.setObservaciones("Observaciones de prueba");

        produccionResponseDTO = new ProduccionResponseDTO(
                codigoProduccion,
                codigoVersionReceta,
                encargado,
                "testuser",
                lote,
                "EN_PROCESO",

                fechaInicio,
                fechaFin,
                observaciones
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

        ProduccionResponseDTO result = produccionManagementService.findByCodigoProduccion(codigoProduccion);

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
            produccionManagementService.findByCodigoProduccion(codigoInexistente);
        });

        verify(produccionRepository).findByCodigoProduccion(codigoInexistente);
    }

    @Test
    void testFindAllByFiltersVacios() {
        LocalDateTime fechaInicioConverted = fechaInicioDate.atStartOfDay();
        LocalDateTime fechaFinConverted = fechaFinDate.atTime(23, 59, 59);
        TipoEstadoProduccion estado = TipoEstadoProduccion.EN_PROCESO;

        when(produccionQueryServiceValidator.convertToStartOfDay(fechaInicioDate)).thenReturn(fechaInicioConverted);
        when(produccionQueryServiceValidator.convertToEndOfDay(fechaFinDate)).thenReturn(fechaFinConverted);
        when(produccionRepository.findByAdvancedFilters(
                codigoVersionReceta, lote, encargado, estado, fechaInicioConverted, fechaFinConverted
        )).thenReturn(produccionModelList);
        when(produccionMapper.modelListToResponseDTOList(produccionModelList)).thenReturn(produccionResponseDTOList);

        List<ProduccionResponseDTO> result = produccionManagementService.findAllByFilters(produccionFilterRequestDTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(codigoProduccion, result.get(0).codigoProduccion());
        verify(produccionQueryServiceValidator).convertToStartOfDay(fechaInicioDate);
        verify(produccionQueryServiceValidator).convertToEndOfDay(fechaFinDate);
        verify(produccionRepository).findByAdvancedFilters(
                codigoVersionReceta, lote, encargado, estado, fechaInicioConverted, fechaFinConverted
        );
        verify(produccionMapper).modelListToResponseDTOList(produccionModelList);
    }


}
