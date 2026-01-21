// ProduccionStateServiceImpl.java
package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.CambioEstadoProduccionInvalido;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProduccionStateServiceImpl implements ProduccionStateService {

    private final ProduccionRepository produccionRepository;
    private final UsuarioService usuarioService;
    private final UsuarioValidationService usuarioValidationService;

    // Estados finales (no se puede salir de ellos)
    private static final Set<TipoEstadoProduccion> ESTADOS_FINALES = Set.of(
            TipoEstadoProduccion.FINALIZADA,
            TipoEstadoProduccion.CANCELADA
    );

    // Reglas de transición: estado actual -> estados permitidos
    private static final Map<TipoEstadoProduccion, Set<TipoEstadoProduccion>> REGLAS_TRANSICION = Map.of(
            TipoEstadoProduccion.EN_PROCESO, Set.of(
                    TipoEstadoProduccion.FINALIZADA,
                    TipoEstadoProduccion.CANCELADA
            )
            // Agregar más reglas según necesites
    );

    @Override
    public void cambiarEstado(String codigoProduccion, ProduccionCambioEstadoRequestDTO request) {
        log.info("Cambiando estado de producción {} a {}", codigoProduccion, request.valor());

        // 1. Validar y obtener usuario
        UsuarioModel usuario = usuarioValidationService.validarUsuarioAutorizado(request.emailCreador());

        // 2. Buscar producción
        ProduccionModel produccion = buscarProduccion(codigoProduccion);

        // 3. Convertir y validar nuevo estado
        TipoEstadoProduccion nuevoEstado = convertirAEstado(request.valor());
        validarTransicionEstado(produccion, nuevoEstado);

        // 4. Aplicar cambio de estado
        aplicarCambioEstado(produccion, nuevoEstado);

        // 5. Guardar cambios
        produccionRepository.save(produccion);

        log.info("Estado de producción {} cambiado exitosamente a {}", codigoProduccion, nuevoEstado);
    }

    @Override
    public void validarTransicionEstado(ProduccionModel produccion, TipoEstadoProduccion nuevoEstado) {
        TipoEstadoProduccion estadoActual = produccion.getEstado();

        log.debug("Validando transición de estado: {} -> {}", estadoActual, nuevoEstado);

        // 1. Validar que no sea el mismo estado
        if (estadoActual == nuevoEstado) {
            throw new CambioEstadoProduccionInvalido(
                    "La producción ya está en estado " + estadoActual);
        }

        // 2. Validar que no esté en estado final
        if (esEstadoFinal(estadoActual)) {
            throw new OperacionNoPermitida(
                    "No se puede modificar una producción en estado " + estadoActual);
        }

        // 3. Validar reglas de transición específicas
        validarReglasTransicion(estadoActual, nuevoEstado);

        log.debug("Transición de estado válida");
    }

    @Override
    public boolean esEstadoFinal(TipoEstadoProduccion estado) {
        return ESTADOS_FINALES.contains(estado);
    }

    @Override
    public TipoEstadoProduccion obtenerEstadoActual(String codigoProduccion) {
        ProduccionModel produccion = buscarProduccion(codigoProduccion);
        return produccion.getEstado();
    }

    // Métodos privados de apoyo
    private UsuarioModel validarUsuarioAutorizado(String email) {
        UsuarioModel usuario = usuarioService.getUsuarioModelByEmail(email);

        if (!usuario.getEstaActivo()) {
            throw new OperacionNoPermitida("El usuario no está activo");
        }

        return usuario;
    }

    private ProduccionModel buscarProduccion(String codigoProduccion) {
        return produccionRepository.findByCodigoProduccion(codigoProduccion)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producción no encontrada: " + codigoProduccion));
    }

    private TipoEstadoProduccion convertirAEstado(String valorEstado) {
        try {
            return TipoEstadoProduccion.valueOf(valorEstado);
        } catch (IllegalArgumentException e) {
            throw new CambioEstadoProduccionInvalido(
                    "Estado no válido: " + valorEstado);
        }
    }

    private void validarReglasTransicion(TipoEstadoProduccion actual, TipoEstadoProduccion destino) {
        Set<TipoEstadoProduccion> estadosPermitidos = REGLAS_TRANSICION.get(actual);

        if (estadosPermitidos != null && !estadosPermitidos.contains(destino)) {
            throw new CambioEstadoProduccionInvalido(
                    String.format("Transición no permitida: %s -> %s. Transiciones permitidas desde %s: %s",
                            actual, destino, actual, String.join(", ", estadosPermitidos.stream()
                                    .map(Enum::name)
                                    .toArray(String[]::new))));
        }

        // Validación específica para EN_PROCESO
        if (actual == TipoEstadoProduccion.EN_PROCESO && !esEstadoFinal(destino)) {
            throw new CambioEstadoProduccionInvalido(
                    "Desde EN_PROCESO solo se puede cambiar a FINALIZADA o CANCELADA");
        }
    }

    private void aplicarCambioEstado(ProduccionModel produccion, TipoEstadoProduccion nuevoEstado) {
        // Cambiar estado
        produccion.setEstado(nuevoEstado);

        // Si es estado final, establecer fecha fin
        if (esEstadoFinal(nuevoEstado)) {
            produccion.setFechaFin(LocalDateTime.now());
            log.debug("Producción marcada como finalizada. Fecha fin: {}", produccion.getFechaFin());
        }

        // Actualizar fecha de modificación
        produccion.setFechaModificacion(LocalDateTime.now());
    }

}