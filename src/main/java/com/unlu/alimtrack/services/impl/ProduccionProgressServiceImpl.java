package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import com.unlu.alimtrack.services.ProduccionProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para calcular el progreso de una producción.
 * Determina el porcentaje de completitud basándose en los campos y celdas respondidos.
 */
@Slf4j
@Service
public class ProduccionProgressServiceImpl implements ProduccionProgressService {

    private record CeldaKey(Long idTabla, Long idFila, Long idColumna) {
        static CeldaKey fromRespuestaTabla(RespuestaTablaModel respuesta) {
            if (respuesta == null) return null;

            Long idTabla = respuesta.getId() != null ? respuesta.getId() : null;
            Long idFila = respuesta.getFila() != null ? respuesta.getFila().getId() : null;
            Long idColumna = respuesta.getColumna() != null ? respuesta.getColumna().getId() : null;

            return (idTabla != null && idFila != null && idColumna != null)
                    ? new CeldaKey(idTabla, idFila, idColumna)
                    : null;
        }
    }

    /**
     * Calcula el progreso general de una producción.
     *
     * @param totalCampos Total de campos simples esperados.
     * @param totalCeldas Total de celdas de tabla esperadas.
     * @param respuestasCampos Lista de respuestas a campos simples.
     * @param respuestasTablas Lista de respuestas a celdas de tabla.
     * @return DTO con el detalle del progreso calculado.
     */
    @Override
    public ProgresoProduccionResponseDTO calcularProgreso(
            Integer totalCampos, Integer totalCeldas,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas) {

        log.debug("Calculando progreso. Total campos: {}, Total celdas: {}", totalCampos, totalCeldas);
        validarParametros(totalCampos, totalCeldas);

        long camposRespondidos = calcularCamposRespondidos(respuestasCampos);
        long celdasRespondidas = calcularCeldasRespondidas(respuestasTablas);

        int totalGlobal = totalCampos + totalCeldas;
        long respondidoGlobal = camposRespondidos + celdasRespondidas;
        double porcentaje = calcularPorcentaje(totalGlobal, respondidoGlobal);

        log.debug("Progreso calculado: Campos {}/{}, Celdas {}/{}, Global {}/{} ({}%)",
                camposRespondidos, totalCampos,
                celdasRespondidas, totalCeldas,
                respondidoGlobal, totalGlobal,
                porcentaje);

        return new ProgresoProduccionResponseDTO(
                totalCampos, (int) camposRespondidos,
                totalCeldas, (int) celdasRespondidas,
                totalGlobal, (int) respondidoGlobal,
                porcentaje
        );
    }

    @Override
    public long calcularCamposRespondidos(List<RespuestaCampoModel> respuestasCampos) {
        if (respuestasCampos == null || respuestasCampos.isEmpty()) {
            return 0;
        }

        // Usar Set para evitar duplicados
        Set<Long> camposIdsUnicos = respuestasCampos.stream()
                .filter(Objects::nonNull)
                .filter(this::esRespuestaCampoValida)
                .map(respuesta -> respuesta.getIdCampo() != null ? respuesta.getIdCampo().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return camposIdsUnicos.size();
    }

    @Override
    public long calcularCeldasRespondidas(List<RespuestaTablaModel> respuestasTablas) {
        if (respuestasTablas == null || respuestasTablas.isEmpty()) {
            return 0;
        }

        // Usar Set para celdas únicas
        Set<CeldaKey> celdasUnicas = respuestasTablas.stream()
                .filter(Objects::nonNull)
                .filter(this::esRespuestaTablaValida)
                .map(CeldaKey::fromRespuestaTabla)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return celdasUnicas.size();
    }

    private void validarParametros(Integer totalCampos, Integer totalCeldas) {
        if (totalCampos == null || totalCampos < 0) {
            log.error("Parámetro totalCampos inválido: {}", totalCampos);
            throw new IllegalArgumentException("totalCampos no puede ser nulo o negativo");
        }

        if (totalCeldas == null || totalCeldas < 0) {
            log.error("Parámetro totalCeldas inválido: {}", totalCeldas);
            throw new IllegalArgumentException("totalCeldas no puede ser nulo o negativo");
        }

        // Log de advertencia si hay valores cero
        if (totalCampos == 0) {
            log.warn("totalCampos es 0 - verificar configuración de campos en la receta");
        }
        if (totalCeldas == 0) {
            log.warn("totalCeldas es 0 - verificar configuración de tablas en la receta");
        }
    }

    private boolean esRespuestaCampoValida(RespuestaCampoModel respuesta) {
        if (respuesta == null || respuesta.getIdCampo() == null) {
            return false;
        }

        // Usar método del modelo si existe
        if (respuesta.esRespuestaVacia()) {
            return false;
        }

        // Validar que el valor corresponda al tipo esperado
        boolean valorCoherente = validarValorSegunTipoCampo(respuesta);
        if (!valorCoherente) {
            log.debug("Respuesta de campo {} descartada por valor incoherente con tipo {}",
                    respuesta.getIdCampo().getId(),
                    respuesta.getIdCampo().getTipoDato());
            return false;
        }

        return true;
    }

    private boolean esRespuestaTablaValida(RespuestaTablaModel respuesta) {
        if (respuesta == null) {
            return false;
        }

        // Usar método del modelo
        return respuesta.esRespuestaValida();
    }

    private boolean validarValorSegunTipoCampo(RespuestaCampoModel respuesta) {
        if (respuesta.getIdCampo() == null || respuesta.getIdCampo().getTipoDato() == null) {
            return false;
        }

        TipoDatoCampo tipo = respuesta.getIdCampo().getTipoDato();

        switch (tipo) {
            case TEXTO:
                return respuesta.getValorTexto() != null && !respuesta.getValorTexto().trim().isEmpty();
            case DECIMAL:
            case ENTERO:
                return respuesta.getValorNumerico() != null;
            case FECHA:
            case HORA:
                return respuesta.getValorFecha() != null;
            case BOOLEANO:
                // Para booleanos, aceptar cualquier valor (true/false representado como BigDecimal)
                return respuesta.getValorNumerico() != null;
            default:
                log.warn("Tipo de campo no reconocido durante validación de progreso: {}", tipo);
                return false;
        }
    }

    private double calcularPorcentaje(int total, long respondido) {
        if (total <= 0) {
            return 0.0;
        }

        // Calcular con precisión
        double porcentaje = (respondido * 100.0) / total;

        // Redondear a 2 decimales
        return Math.round(porcentaje * 100.0) / 100.0;
    }

    // ========== MÉTODOS ADICIONALES UTILES ==========

    /**
     * Calcula el progreso filtrado por una sección específica.
     */
    public ProgresoProduccionResponseDTO calcularProgresoPorSeccion(
            Integer totalCampos, Integer totalCeldas,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas,
            Long idSeccion) {

        log.debug("Calculando progreso para sección ID: {}", idSeccion);
        // Filtrar respuestas por sección
        List<RespuestaCampoModel> camposFiltrados = respuestasCampos.stream()
                .filter(respuesta -> respuesta.getIdCampo() != null &&
                        respuesta.getIdCampo().getSeccion() != null &&
                        Objects.equals(respuesta.getIdCampo().getSeccion().getId(), idSeccion))
                .toList();

        // Nota: La lógica de filtrado de tablas por sección debe implementarse según el modelo de datos
        // Asumiendo que las tablas también tienen referencia a sección o se puede inferir
        List<RespuestaTablaModel> tablasFiltradas = respuestasTablas.stream()
                .filter(respuesta -> {
                    // Implementar lógica específica si es necesario
                    return true; 
                })
                .toList();

        return calcularProgreso(totalCampos, totalCeldas, camposFiltrados, tablasFiltradas);
    }

    /**
     * Verifica si una producción está completa (100%).
     */
    public boolean estaProduccionCompleta(
            Integer totalCampos, Integer totalCeldas,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas) {

        ProgresoProduccionResponseDTO progreso = calcularProgreso(
                totalCampos, totalCeldas, respuestasCampos, respuestasTablas);

        boolean completa = Math.abs(progreso.porcentajeCompletado() - 100.0) < 0.01;
        log.debug("Verificación de completitud: {} ({}%)", completa, progreso.porcentajeCompletado());
        return completa;
    }

    /**
     * Obtiene lista de IDs de campos que aún no han sido respondidos.
     */
    public List<Long> obtenerCamposNoRespondidos(
            List<Long> todosLosCamposIds,
            List<RespuestaCampoModel> respuestasCampos) {

        Set<Long> camposRespondidosIds = respuestasCampos.stream()
                .filter(this::esRespuestaCampoValida)
                .map(respuesta -> respuesta.getIdCampo() != null ? respuesta.getIdCampo().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return todosLosCamposIds.stream()
                .filter(id -> !camposRespondidosIds.contains(id))
                .toList();
    }

    /**
     * Calcula la tendencia de progreso comparando con un estado anterior.
     */
    public double calcularTendenciaProgreso(
            ProgresoProduccionResponseDTO progresoActual,
            ProgresoProduccionResponseDTO progresoAnterior) {

        if (progresoAnterior == null) {
            return 0.0;
        }

        return progresoActual.porcentajeCompletado() -
                progresoAnterior.porcentajeCompletado();
    }
}
