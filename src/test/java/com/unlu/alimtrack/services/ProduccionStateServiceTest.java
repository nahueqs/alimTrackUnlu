package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.CambioEstadoProduccionInvalido;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.impl.ProduccionStateServiceImpl;
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
class ProduccionStateServiceTest {

    @Mock
    private ProduccionRepository produccionRepository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private UsuarioValidationService usuarioValidationService;

    @InjectMocks
    private ProduccionStateServiceImpl produccionStateService;

    @Test
    void cambiarEstado_ShouldUpdateStateAndSave_WhenValidTransition() {
        // Arrange
        String codigo = "PROD-1";
        ProduccionCambioEstadoRequestDTO request = new ProduccionCambioEstadoRequestDTO(
                TipoEstadoProduccion.FINALIZADA.toString(), "user@test.com"
        );
        
        ProduccionModel produccion = new ProduccionModel();
        produccion.setCodigoProduccion(codigo);
        produccion.setEstado(TipoEstadoProduccion.EN_PROCESO);
        
        UsuarioModel usuario = new UsuarioModel();
        usuario.setEstaActivo(true);

        when(usuarioValidationService.validarUsuarioAutorizado("user@test.com")).thenReturn(usuario);
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(produccion));

        // Act
        produccionStateService.cambiarEstado(codigo, request);

        // Assert
        assertEquals(TipoEstadoProduccion.FINALIZADA, produccion.getEstado());
        assertNotNull(produccion.getFechaFin());
        assertNotNull(produccion.getFechaModificacion());
        verify(produccionRepository).save(produccion);
    }

    @Test
    void cambiarEstado_ShouldThrow_WhenSameState() {
        // Arrange
        String codigo = "PROD-1";
        ProduccionCambioEstadoRequestDTO request = new ProduccionCambioEstadoRequestDTO(
                TipoEstadoProduccion.EN_PROCESO.toString(), "user@test.com"
        );
        
        ProduccionModel produccion = new ProduccionModel();
        produccion.setEstado(TipoEstadoProduccion.EN_PROCESO);

        when(usuarioValidationService.validarUsuarioAutorizado("user@test.com")).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(produccion));

        // Act & Assert
        assertThrows(CambioEstadoProduccionInvalido.class, () -> 
            produccionStateService.cambiarEstado(codigo, request)
        );
    }

    @Test
    void cambiarEstado_ShouldThrow_WhenProductionIsFinal() {
        // Arrange
        String codigo = "PROD-1";
        ProduccionCambioEstadoRequestDTO request = new ProduccionCambioEstadoRequestDTO(
                TipoEstadoProduccion.EN_PROCESO.toString(), "user@test.com"
        );
        
        ProduccionModel produccion = new ProduccionModel();
        produccion.setEstado(TipoEstadoProduccion.FINALIZADA);

        when(usuarioValidationService.validarUsuarioAutorizado("user@test.com")).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(produccion));

        // Act & Assert
        assertThrows(OperacionNoPermitida.class, () -> 
            produccionStateService.cambiarEstado(codigo, request)
        );
    }

    @Test
    void cambiarEstado_ShouldThrow_WhenInvalidTransition() {
        // Arrange
        // Intentar pasar de EN_PROCESO a un estado no permitido (si existiera otro estado intermedio no válido)
        // Como solo hay EN_PROCESO, FINALIZADA, CANCELADA, probemos una transición hipotética inválida
        // O si agregamos un estado "PAUSADA" y no está en las reglas.
        // Por ahora, probemos con un estado que no sea FINALIZADA ni CANCELADA desde EN_PROCESO si hubiera.
        // Pero como no hay, probemos el caso de estado inválido (string random).
        
        String codigo = "PROD-1";
        ProduccionCambioEstadoRequestDTO request = new ProduccionCambioEstadoRequestDTO(
                "ESTADO_INEXISTENTE", "user@test.com"
        );
        
        when(usuarioValidationService.validarUsuarioAutorizado("user@test.com")).thenReturn(new UsuarioModel());
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(new ProduccionModel()));

        // Act & Assert
        assertThrows(CambioEstadoProduccionInvalido.class, () -> 
            produccionStateService.cambiarEstado(codigo, request)
        );
    }

    @Test
    void obtenerEstadoActual_ShouldReturnState() {
        String codigo = "PROD-1";
        ProduccionModel produccion = new ProduccionModel();
        produccion.setEstado(TipoEstadoProduccion.EN_PROCESO);
        
        when(produccionRepository.findByCodigoProduccion(codigo)).thenReturn(Optional.of(produccion));

        TipoEstadoProduccion result = produccionStateService.obtenerEstadoActual(codigo);

        assertEquals(TipoEstadoProduccion.EN_PROCESO, result);
    }

    @Test
    void esEstadoFinal_ShouldReturnTrueForFinalStates() {
        assertTrue(produccionStateService.esEstadoFinal(TipoEstadoProduccion.FINALIZADA));
        assertTrue(produccionStateService.esEstadoFinal(TipoEstadoProduccion.CANCELADA));
    }

    @Test
    void esEstadoFinal_ShouldReturnFalseForNonFinalStates() {
        assertFalse(produccionStateService.esEstadoFinal(TipoEstadoProduccion.EN_PROCESO));
    }
}
