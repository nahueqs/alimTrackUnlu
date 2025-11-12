//package com.unlu.alimtrack.services;
//
//import com.unlu.alimtrack.DTOS.response.producciones.ProduccionResponseDTO;
//import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
//import com.unlu.alimtrack.exceptions.RecursoIdentifierConflictException;
//import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
//import org.junit.jupiter.api.Test;
//
//import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//public class ProduccionManagerServiceTest {
//
//    @Test
//    void testSaveProduccion() {
//
//        when(produccionRepository.existsByCodigoProduccion(codigoProduccion)).thenReturn(false);
//        when(versionRecetaQueryService.existsByCodigoVersion(codigoVersionReceta)).thenReturn(true);
//        when(usuarioQueryService.existsByUsername(username)).thenReturn(true);
//        when(usuarioQueryService.estaActivoByUsername(username)).thenReturn(true);
//        when(produccionMapper.createDTOtoModel(produccionCreateDTO)).thenReturn(produccionModel);
//        when(produccionRepository.save(produccionModel)).thenReturn(produccionModel);
//        when(produccionMapper.modelToResponseDTO(produccionModel)).thenReturn(produccionResponseDTO);
//
//        ProduccionResponseDTO result = produccionManagementService.saveProduccion(codigoProduccion, produccionCreateDTO);
//
//        assertNotNull(result);
//        assertEquals(codigoProduccion, result.codigoProduccion());
//        assertEquals(codigoVersionReceta, result.codigoVersion());
//        assertEquals(encargado, result.encargado());
//        assertEquals(lote, result.lote());
//        assertEquals("EN_PROCESO", result.estado());
//
//        verify(produccionRepository).existsByCodigoProduccion(codigoProduccion);
//        verify(versionRecetaQueryService).existsByCodigoVersion(codigoVersionReceta);
//        verify(usuarioQueryService).existsByUsername(username);
//        verify(produccionMapper).modelToResponseDTO(produccionModel);
//        verify(produccionRepository).save(produccionModel);
//        verify(produccionMapper).createDTOtoModel(produccionCreateDTO);
//    }
//
//    @Test
//    void testSaveProduccionCodigoNoCoincide() {
//        String codigoProduccionIncorrecto = "PROD-999";
//
//        assertThrows(RecursoIdentifierConflictException.class, () -> {
//            produccionManagementService.saveProduccion(codigoProduccionIncorrecto, produccionCreateDTO);
//        });
//    }
//
//    @Test
//    void testSaveProduccionCodigoDuplicado() {
//        String codigoProduccionDuplicado = "PROD-002";
//
//        assertThrows(RecursoIdentifierConflictException.class, () -> {
//            produccionManagementService.saveProduccion(codigoProduccionDuplicado, produccionCreateDTO);
//        });
//    }
//
//    @Test
//    void testSaveProduccionVersionNoExiste() {
//
//        when(produccionRepository.existsByCodigoProduccion(codigoProduccion)).thenReturn(false);
//        when(versionRecetaQueryService.existsByCodigoVersion(codigoVersionReceta)).thenReturn(false);
//
//        assertThrows(RecursoNoEncontradoException.class, () -> {
//            produccionManagementService.saveProduccion(codigoProduccion, produccionCreateDTO);
//        });
//
//        verify(produccionRepository).existsByCodigoProduccion(codigoProduccion);
//        verify(versionRecetaQueryService).existsByCodigoVersion(codigoVersionReceta);
//    }
//
//    @Test
//    void testSaveProduccionUsuarioNoExiste() {
//
//        when(produccionRepository.existsByCodigoProduccion(codigoProduccion)).thenReturn(false);
//        when(versionRecetaQueryService.existsByCodigoVersion(codigoVersionReceta)).thenReturn(true);
//        when(usuarioQueryService.existsByUsername(username)).thenReturn(false);
//
//        assertThrows(RecursoNoEncontradoException.class, () -> {
//            produccionManagementService.saveProduccion(codigoProduccion, produccionCreateDTO);
//        });
//
//        verify(produccionRepository).existsByCodigoProduccion(codigoProduccion);
//        verify(versionRecetaQueryService).existsByCodigoVersion(codigoVersionReceta);
//        verify(usuarioQueryService).existsByUsername(username);
//
//    }
//
//    @Test
//    void testSaveProduccionUsuarioInactivo() {
//
//        when(usuarioQueryService.existsByUsername(username)).thenReturn(true);
//        when(usuarioQueryService.estaActivoByUsername(username)).thenReturn(false);
//        when(produccionRepository.existsByCodigoProduccion(codigoProduccion)).thenReturn(null);
//        when(versionRecetaQueryService.existsByCodigoVersion(codigoVersionReceta)).thenReturn(true);
//
//        assertThrows(OperacionNoPermitida.class, () -> {
//            produccionManagementService.saveProduccion(codigoProduccion, produccionCreateDTO);
//        });
//
//        verify(usuarioQueryService).existsByUsername(username);
//        verify(usuarioQueryService).estaActivoByUsername(username);
//        verify(produccionRepository).existsByCodigoProduccion(codigoProduccion);
//        verify(versionRecetaQueryService).existsByCodigoVersion(codigoVersionReceta);
//    }
//}
